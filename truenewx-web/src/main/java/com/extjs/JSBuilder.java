package com.extjs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * JS压缩合并器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JSBuilder {

    private static Logger logger = LoggerFactory.getLogger(JSBuilder.class);

    private static ArrayList<File> outputFiles = new ArrayList<>();

    private static String homeDir;
    private static String projectFile;
    private static String debugSuffix = "-debug";

    private static JSONObject projCfg;
    private static JSONArray pkgs;

    private static Boolean verbose = Boolean.FALSE;

    private static File deployDir;

    private static String projectHome;

    public static void build(final String homeDir2, final String projectFile2) {
        homeDir = homeDir2;
        projectFile = projectFile2;
        if (homeDir == "") {
            logger.error(
                    "The --homeDir or -d argument is required and was not included in the commandline arguments.");
        }
        if (projectFile == "") {
            logger.error(
                    "The --projectFile or -p argument is required and was not included in the commandline arguments.");
        }
        if (homeDir == "" || projectFile == "") {
            return;
        }
        openProjectFile(projectFile);
        loadPackages();
        mkDeployDir();
        createTargetsWithFileIncludes();
        createTargetsWithDeps();
        copyResources();
        compressOutputFiles();
    }

    private static void openProjectFile(final String projectFileName) {
        try {
            final File inputFile = new File(projectFileName);
            projectHome = inputFile.getParent();

            /* read the file into a string */
            final String s = FileHelper.readFileToString(inputFile);

            /* create json obj from string */
            projCfg = new JSONObject(s);
            logger.debug("Loading the '%s' Project%n", projCfg.get("projectName"));
        } catch (final Exception e) {
            logger.error(e.getMessage());
            logger.error("Failed to open project file.");
        }
    }

    private static void loadPackages() {
        try {
            pkgs = projCfg.getJSONArray("pkgs");
            logger.debug("Loaded %d Packages%n", pkgs.length());
        } catch (final Exception e) {
            logger.error(e.getMessage());
            logger.error("Failed to find \'pkgs\' configuration.");
        }
    }

    private static void mkDeployDir() {
        try {
            deployDir = new File(homeDir + File.separatorChar + projCfg.getString("deployDir"));
            deployDir.mkdirs();
        } catch (final Exception e) {
            logger.error(e.getMessage());
            logger.error("Failed to create deploy directory.");
        }
    }

    private static void createTargetsWithFileIncludes() {
        try {
            final int len = pkgs.length();
            /* loop over packages for fileIncludes */
            for (int i = 0; i < len; i++) {
                /* Build pkg and include file deps */
                final JSONObject pkg = pkgs.getJSONObject(i);
                /* if we don't include dependencies, it must be fileIncludes */
                if (!pkg.optBoolean("includeDeps", false)) {
                    String targFileName = pkg.getString("file");
                    if (targFileName.indexOf(".js") != -1) {
                        targFileName = FileHelper.insertFileSuffix(pkg.getString("file"),
                                debugSuffix);
                    }
                    if (verbose) {
                        logger.debug("Building the '%s' package as '%s'%n", pkg.getString("name"),
                                targFileName);
                    }

                    /* create file and write out header */
                    final File targetFile = new File(
                            deployDir.getCanonicalPath() + File.separatorChar + targFileName);
                    outputFiles.add(targetFile);
                    targetFile.getParentFile().mkdirs();
                    FileHelper.writeStringToFile("", targetFile, false);

                    /* get necessary file includes for this specific package */
                    final JSONArray fileIncludes = pkg.getJSONArray("fileIncludes");
                    final int fileIncludesLen = fileIncludes.length();
                    if (verbose) {
                        logger.debug("- There are %d file include(s).%n", fileIncludesLen);
                    }

                    /* loop over file includes */
                    for (int j = 0; j < fileIncludesLen; j++) {
                        /*
                         * open each file, read into string and append to target
                         */
                        final JSONObject fileCfg = fileIncludes.getJSONObject(j);

                        final String subFileName = projectHome + File.separatorChar
                                + fileCfg.getString("path") + fileCfg.getString("text");
                        if (verbose) {
                            logger.debug("- - %s%s%n", fileCfg.getString("path"),
                                    fileCfg.getString("text"));
                        }
                        final File subFile = new File(subFileName);
                        final String tempString = FileHelper.readFileToString(subFile);
                        logger.debug(tempString);
                        FileHelper.writeStringToFile(tempString, targetFile, true);
                    }
                }
            }
        } catch (final Exception e) {
            logger.error(e.getMessage());
            logger.error("Failed to create targets with fileIncludes.");
        }
    }

    private static void createTargetsWithDeps() {
        try {
            final int len = pkgs.length();
            for (int i = 0; i < len; i++) {
                /* Build pkg and include file deps */
                final JSONObject pkg = pkgs.getJSONObject(i);
                /* if we need to includeDeps, they shoudl already be built. */
                if (pkg.optBoolean("includeDeps", false)) {
                    String targFileName = pkg.getString("file");
                    if (targFileName.indexOf(".js") != -1) {
                        targFileName = FileHelper.insertFileSuffix(pkg.getString("file"),
                                debugSuffix);
                    }
                    if (verbose) {
                        logger.debug("Building the '%s' package as '%s'%n", pkg.getString("name"),
                                targFileName);
                        logger.debug("This package is built by included dependencies.");
                    }

                    /* create file and write out header */
                    final File targetFile = new File(
                            deployDir.getCanonicalPath() + File.separatorChar + targFileName);
                    outputFiles.add(targetFile);
                    targetFile.getParentFile().mkdirs();
                    FileHelper.writeStringToFile("", targetFile, false);

                    /* get necessary pkg includes for this specific package */
                    final JSONArray pkgDeps = pkg.getJSONArray("pkgDeps");
                    final int pkgDepsLen = pkgDeps.length();
                    if (verbose) {
                        logger.debug("- There are %d package include(s).%n", pkgDepsLen);
                    }

                    /* loop over file includes */
                    for (int j = 0; j < pkgDepsLen; j++) {
                        /*
                         * open each file, read into string and append to target
                         */
                        final String pkgDep = pkgDeps.getString(j);
                        if (verbose) {
                            logger.debug("- - %s%n", pkgDep);
                        }
                        String nameWithorWithoutSuffix = pkgDep;
                        if (pkgDep.indexOf(".js") != -1) {
                            nameWithorWithoutSuffix = FileHelper.insertFileSuffix(pkgDep,
                                    debugSuffix);
                        }

                        final String subFileName = deployDir.getCanonicalPath() + File.separatorChar
                                + nameWithorWithoutSuffix;
                        final File subFile = new File(subFileName);
                        final String tempString = FileHelper.readFileToString(subFile);
                        FileHelper.writeStringToFile(tempString, targetFile, true);
                    }
                }
            }
        } catch (final Exception e) {
            logger.error("Failed to create target with package dependencies.", e);
        }
    }

    public static void compressOutputFiles() {
        Reader in = null;
        Writer out = null;
        logger.debug("Compressing output files...");
        for (final File f : outputFiles) {
            try {
                if (f.getName().indexOf(".js") != -1) {
                    if (verbose) {
                        logger.debug("- - " + f.getName() + " -> "
                                + f.getName().replace(debugSuffix, ""));
                    }
                    in = new InputStreamReader(new FileInputStream(f), "UTF-8");
                    final JavaScriptCompressor compressor = new JavaScriptCompressor(in,
                            new ErrorReporter() {

                                @Override
                                public void warning(final String message, final String sourceName,
                                        final int line, final String lineSource,
                                        final int lineOffset) {
                                    if (line < 0) {
                                        logger.error("\n[WARNING] " + message);
                                    } else {
                                        logger.error("\n[WARNING] " + line + ':' + lineOffset + ':'
                                                + message);
                                    }
                                }

                                @Override
                                public void error(final String message, final String sourceName,
                                        final int line, final String lineSource,
                                        final int lineOffset) {
                                    if (line < 0) {
                                        logger.error("\n[ERROR] " + message);
                                    } else {
                                        logger.error("\n[ERROR] " + line + ':' + lineOffset + ':'
                                                + message);
                                    }
                                }

                                @Override
                                public EvaluatorException runtimeError(final String message,
                                        final String sourceName, final int line,
                                        final String lineSource, final int lineOffset) {
                                    error(message, sourceName, line, lineSource, lineOffset);
                                    return new EvaluatorException(message);
                                }
                            });

                    // Close the input stream first, and then open the output
                    // stream,
                    // in case the output file should override the input file.
                    in.close();
                    in = null;

                    out = new OutputStreamWriter(
                            new FileOutputStream(f.getAbsolutePath().replace(debugSuffix, "")),
                            "UTF-8");

                    final boolean munge = true;
                    final boolean preserveAllSemiColons = false;
                    final boolean disableOptimizations = false;
                    final int linebreakpos = -1;

                    compressor.compress(out, linebreakpos, munge, false, preserveAllSemiColons,
                            disableOptimizations);
                }
            } catch (final EvaluatorException e) {

                logger.error(e.getMessage(), e);
                // Return a special error code used specifically by the web
                // front-end.
                System.exit(2);

            } catch (final IOException e) {

                logger.error(e.getMessage(), e);
                System.exit(1);

            } finally {

                if (in != null) {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }

                if (out != null) {
                    try {
                        out.close();
                    } catch (final IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

        }
    }

    public static void copyResources() {
        try {
            final JSONArray resources = projCfg.getJSONArray("resources");
            final int resourceLen = resources.length();

            for (int z = 0; z < resourceLen; z++) {
                final JSONObject resourceCfg = resources.getJSONObject(z);
                final String filters = resourceCfg.getString("filters");
                final File srcDir = new File(
                        projectHome + File.separatorChar + resourceCfg.getString("src"));
                final File destDir = new File(deployDir.getCanonicalPath() + File.separatorChar
                        + resourceCfg.getString("dest"));
                FileHelper.copyDirectory(srcDir, destDir, filters);
            }
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
