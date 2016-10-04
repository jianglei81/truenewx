package com.extjs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * 文件操作帮助类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FileHelper {
    public static String readFileToString(final File file) {
        try {
            final FileInputStream in = new FileInputStream(file);
            final byte bt[] = new byte[(int) file.length()];
            in.read(bt);
            final String s = new String(bt, "UTF-8");
            in.close();
            return s;
        } catch (final Exception ex) {
        }
        return null;
    }

    public static void writeStringToFile(final String s, final File file, final Boolean append) {
        try {
            final OutputStreamWriter fw = new OutputStreamWriter(
                    new FileOutputStream(file, append), "UTF-8");
            fw.write(s, 0, s.length());
            fw.flush();
            fw.close();
        } catch (final Exception ex) {
        }
    }

    public static String insertFileSuffix(final String fileName, final String suffix)
            throws Exception {
        final int endOfDot = fileName.lastIndexOf(".");
        if (endOfDot == -1) {
            throw new Exception("No period in the target file output.");
        }
        return fileName.substring(0, endOfDot) + suffix + fileName.substring(endOfDot);
    }

    // http://www.java-tips.org/java-se-tips/java.io/how-to-copy-a-directory-from-one-location-to-another-loc.html
    public static void copyDirectory(final File sourceLocation, final File targetLocation,
            final String regExPattern) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            final String[] children = sourceLocation.list(new FilenameFilter() {
                private Pattern pattern = Pattern.compile(regExPattern);

                @Override
                public boolean accept(final File dir, final String name) {
                    final File newFile = new File(dir.getAbsolutePath() + File.separatorChar + name);
                    final Boolean isSvn = (newFile.getAbsolutePath().indexOf(".svn") != -1);
                    final Boolean isHidden = newFile.isHidden();
                    final Boolean isDir = newFile.isDirectory();
                    final Boolean matches = this.pattern.matcher(name).matches();
                    if (isSvn || isHidden) {
                        return false;
                    } else if (isDir) {
                        return true;
                    } else {
                        return matches;
                    }
                }
            });

            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation,
                        children[i]), regExPattern);
            }
        } else {
            final Boolean isSvn = (sourceLocation.getAbsolutePath().indexOf(".svn") != -1);
            final Boolean isHidden = sourceLocation.isHidden();
            if (isSvn || isHidden) {
                return;
            }

            final InputStream in = new FileInputStream(sourceLocation);
            final OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public static File[] listFilesAsArray(final File directory, final FilenameFilter filter,
            final boolean recurse) {
        final Collection<File> files = listFiles(directory, filter, recurse);

        final File[] arr = new File[files.size()];
        return files.toArray(arr);
    }

    public static Collection<File> listFiles(final File directory, final FilenameFilter filter,
            final boolean recurse) {
        final Vector<File> files = new Vector<File>();

        // Get files / directories in the directory
        final File[] entries = directory.listFiles();

        // Go over entries
        for (final File entry : entries) {
            // If there is no filter or the filter accepts the
            // file / directory, add it to the list
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }

            // If the file is a directory and the recurse flag
            // is set, recurse into the directory
            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }
        // Return collection of files
        return files;
    }

}
