package org.truenewx.core.util;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.truenewx.core.Strings;

/**
 * IO工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class IOUtil {
    private IOUtil() {
    }

    /**
     * 文件路径分隔符
     */
    public static final String FILE_SEPARATOR = System.getProperties()
                    .getProperty("file.separator");

    /**
     * 将指定输入流中的数据全部写入指定输出流中。除读写操作，本方法不对输入流和输出流做任何其它操作。
     *
     * @param in
     *            输入流
     * @param out
     *            输出流
     * @throws IOException
     *             如果读写过程中出现错误
     */
    public static void writeAll(final InputStream in, final OutputStream out) throws IOException {
        final byte[] b = new byte[1024];
        int len;
        while ((len = in.read(b)) >= 0) {
            out.write(b, 0, len);
        }
    }

    public static void coverToFile(final File file, final String data, final String encoding)
                    throws IOException {
        final File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        OutputStream out = new FileOutputStream(file);
        out.write(data.getBytes(encoding));
        out.flush();
        out.close();
        out = null;
    }

    /**
     * 以非阻塞方式，读取指定字节输入流中的数据为字符串
     *
     * @param in
     *            字节输入流
     * @return 结果字符串
     * @throws IOException
     *             如果读取过程中出现错误
     */
    public static String readUnblocklyToString(final InputStream in) throws IOException {
        return readUnblocklyToString(new BufferedReader(new InputStreamReader(in)));
    }

    /**
     * 以非阻塞方式，以指定字符集读取指定字节输入流中的数据为字符串
     *
     * @param in
     *            字节输入流
     * @param charsetName
     *            字符集
     * @return 结果字符串
     * @throws IOException
     *             如果读取过程中出现错误
     */
    public static String readUnblocklyToString(final InputStream in, final String charsetName)
                    throws IOException {
        return readUnblocklyToString(new BufferedReader(new InputStreamReader(in, charsetName)));
    }

    /**
     * 以非阻塞方式，读取指定字符输入流中的数据为字符串。如果对字符集有要求，请先将字符输入流的字符集设置好
     *
     * @param reader
     *            字符输入流
     * @return 结果字符串
     * @throws IOException
     *             如果读取过程中出现错误
     */
    public static String readUnblocklyToString(final Reader reader) throws IOException {
        String s = "";
        final char[] c = new char[1024];
        while (reader.ready()) {
            s += new String(c, 0, reader.read(c));
        }
        return s;
    }

    /**
     * 执行指定命令行指令，如果等待毫秒数大于0，则当前线程等待指定毫秒数之后返回，
     *
     * @param command
     *            命令行指令
     * @param waitInterval
     *            等待毫秒数
     */
    public static String executeCommand(final String command) {
        String result = "";
        try {
            final Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            result = IOUtils.toString(process.getInputStream());
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建指定文件到本地文件系统，如果该文件已存在则不创建
     *
     * @param file
     *            文件
     * @throws IOException
     *             如果创建文件时出现错误
     */
    public static void createFile(final File file) throws IOException {
        if (!file.exists()) {
            final File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.createNewFile();
        }
    }

    /**
     * 创建指定目录到本地文件系统，如果该目录已存在则不创建
     *
     * @param dir
     *            目录
     */
    public static void createDirectory(final File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 替换指定文件指定内容
     *
     * @param filePath
     *            被修改文件路径
     * @param regex
     *            被替换内容
     * @param replacement
     *            替换内容
     */
    public static void replaceFileContent(final String filePath, final String regex,
                    final String replacement) {
        BufferedReader br = null;
        String line = "";
        final StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    br = null;
                }
            }
        }
        final String s = sb.toString().replaceAll(regex, replacement);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(s);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (final IOException e) {
                    bw = null;
                }
            }
        }
    }

    /**
     * 截取指定输入流中图片的指定矩形区域范围内的内容
     *
     * @param imageInput
     *            图片输入流，如果该输入流中的数据不是图片，将抛出IOException
     * @param x
     *            截取矩形区域相对于图片的x轴坐标
     * @param y
     *            截取矩形区域相对于图片的y轴坐标
     * @param width
     *            截取矩形区域的宽度
     * @param height
     *            截取矩形区域的高度
     * @return 截取得到的图片数据
     * @throws IOException
     *             如果截取过程中出现IO错误
     */
    public static byte[] shootImage(final InputStream imageInput, final int x, final int y,
                    final int width, final int height) throws IOException {
        BufferedImage image = ImageIO.read(imageInput);
        image = image.getSubimage(x, y, width, height);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        out.flush();
        final byte[] b = out.toByteArray();
        out.close();
        return b;
    }

    /**
     * 查找与指定区域匹配的国际化的资源
     *
     * @param basename
     *            文件基本名
     * @param locale
     *            区域
     * @param extension
     *            文件扩展名
     * @return 与指定区域匹配的国际化的资源，如果找不到则返回null
     */
    public static Resource findI18nResource(String basename, final Locale locale,
                    final String extension) {
        basename = basename.trim();
        Assert.hasText(basename, "Basename must not be empty");
        basename = basename.replace('\\', '/');
        // 把basename中classpath:替换为classpath*:后进行查找
        final StringBuffer searchBasename = new StringBuffer(
                        basename.replace(ResourceUtils.CLASSPATH_URL_PREFIX,
                                        ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX))
                                                        .append(Strings.ASTERISK);
        if (!extension.startsWith(Strings.DOT)) {
            searchBasename.append(Strings.DOT);
        }
        searchBasename.append(extension);
        // 查找文件资源
        final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] resources = resourcePatternResolver
                            .getResources(searchBasename.toString());
            Resource result = null;
            for (final Resource resource : resources) {
                final String fileName = resource.getFilename();
                final String[] fileNameArray = fileName.split(Strings.UNDERLINE);
                if (StringUtils.indexOfIgnoreCase(fileName,
                                locale.getLanguage() + "_" + locale.getCountry(), 0) >= 0) {
                    result = resource;
                    break;
                } else if (StringUtils.indexOfIgnoreCase(fileName, locale.getLanguage(), 0) >= 0) {
                    result = resource;
                } else if (result == null && fileNameArray.length == 1) {
                    result = resource;
                }
            }
            return result;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找与指定目录下区域匹配的国际化的文件
     *
     * @param baseDir
     *            目录
     * @param basename
     *            文件基本名称 aa(aa.txt)
     * @param locale
     *            区域
     * @param extension
     *            文件扩展名
     * @return
     * @throws IOException
     */
    public static File findI18nFileByDir(final String baseDir, final String basename,
                    final String extension, final Locale locale) throws IOException {
        final StringBuffer searchFileName = new StringBuffer(basename).append(Strings.ASTERISK);
        if (!extension.startsWith(Strings.DOT)) {
            searchFileName.append(Strings.DOT);
        }
        searchFileName.append(extension);
        final List<File> resultList = new ArrayList<>();
        findFiles(baseDir, searchFileName.toString(), resultList);

        File returnFile = null;
        if (resultList.size() > 0) {
            for (final File file : resultList) {
                final String resultFileName = file.getName();
                final String[] fileNameArray = resultFileName.split(Strings.UNDERLINE);
                if (StringUtils.indexOfIgnoreCase(resultFileName,
                                locale.getLanguage() + "_" + locale.getCountry(), 0) >= 0) {
                    returnFile = file;
                    break;
                } else if (StringUtils.indexOfIgnoreCase(resultFileName, locale.getLanguage(),
                                0) >= 0) {
                    returnFile = file;
                } else if (returnFile == null && fileNameArray.length == 1) {
                    returnFile = file;
                }
            }

        }
        return returnFile;
    }

    /**
     * 递归查找文件
     *
     * @param baseDirName
     *            查找的文件夹路径
     * @param targetFileName
     *            需要查找的文件名
     * @param fileList
     *            查找到的文件集合
     */
    public static void findFiles(final String baseDirName, final String targetFileName,
                    final List<File> fileList) {

        final File baseDir = new File(baseDirName); // 创建一个File对象
        if (baseDir.exists() && baseDir.isDirectory()) { // 判断目录是否存在
            String tempName = null;
            // 判断目录是否存在
            File tempFile;
            final File[] files = baseDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                tempFile = files[i];
                if (tempFile.isDirectory()) {
                    findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);
                } else if (tempFile.isFile()) {
                    tempName = tempFile.getName();
                    if (wildcardMatch(targetFileName, tempName)) {
                        // 匹配成功，将文件名添加到结果集
                        fileList.add(tempFile);
                    }
                }
            }
        }
    }

    /**
     * 通配符匹配
     *
     * @param pattern
     *            通配符模式
     * @param str
     *            待匹配的字符串
     * @return 匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(final String pattern, final String str) {
        final int patternLength = pattern.length();
        final int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                // 通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                                    str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                // 通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    // 表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }

    /**
     * 图片装载器
     */
    public static final MediaTracker tracker = new MediaTracker(new Component() {
        private static final long serialVersionUID = 1234162663955668507L;
    });

    /**
     * 检核图片是否合法
     *
     * @param image
     *            图片
     */
    private static void checkImage(final Image image) {
        waitForImage(image);
        final int width = image.getWidth(null);
        if (width < 1) {
            throw new IllegalArgumentException("image width " + width + " is out of range");
        }
        final int height = image.getHeight(null);
        if (height < 1) {
            throw new IllegalArgumentException("image height " + height + " is out of range");
        }
    }

    /**
     * 等待图片装载
     *
     * @param image
     *            图片
     */
    private static void waitForImage(final Image image) {
        try {
            tracker.addImage(image, 0);
            tracker.waitForID(0);
            tracker.removeImage(image, 0);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缩放图片
     *
     * @param in
     *            原图片输入流
     * @param width
     *            缩放目标宽度
     * @return 缩放后得到的图片
     * @throws IOException
     *             缩放异常(原文件损坏或指定缩放大小错误)
     */
    public static BufferedImage zoomImage(final InputStream in, final int width)
                    throws IOException {
        try {
            final byte[] bytes = IOUtils.toByteArray(in);// 将文件流转换为Byte数组
            final Image originalImage = Toolkit.getDefaultToolkit().createImage(bytes);// 将Byte数组转换为图片
            waitForImage(originalImage); // 等待图片加载
            return zoomImage(originalImage, width);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) { // 无需处理该异常
            }
        }
    }

    /**
     * 缩放图片
     *
     * @param image
     *            原图片
     * @param width
     *            缩放目标宽度
     * @return 缩放后得到的图片
     *
     * @author jianglei
     */
    public static BufferedImage zoomImage(final Image image, int width) {
        final int originalWidth = image.getWidth(null);
        if (originalWidth == width) {
            if (image instanceof BufferedImage) {
                return (BufferedImage) image;
            }
        }
        final int originalHeight = image.getHeight(null);
        checkImage(image);

        // 计算等比高宽
        int height = -1;
        final double scaleW = (double) originalWidth / (double) width;
        final double scaleY = (double) originalHeight / (double) height;
        if (scaleW >= 0 && scaleY >= 0) {
            if (scaleW > scaleY) {
                height = -1;
            } else {
                width = -1;
            }
        }

        // 渲染缩略图
        final Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        checkImage(newImage);
        final BufferedImage bi = new BufferedImage(newImage.getWidth(null),
                        newImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = bi.createGraphics();
        graphics.drawImage(newImage, 0, 0, null);
        return bi;
    }

    /**
     * 保存图片
     *
     * @param image
     *            图片
     * @param dirsPath
     *            存储位置
     * @param fileName
     *            文件名
     * @param extension
     *            后缀名
     * @throws IOException
     *             系统没有写入权限
     */
    public static void saveImage(final BufferedImage image, final String dir, final String fileName,
                    final String extension) throws IOException {
        FileOutputStream output = null;
        try {
            final File file = new File(dir);
            if (!file.exists()) { // 因在实例化FileOutputStream时如果Dir未存在会报错,所以先保证dir有效
                file.mkdirs();
            }
            output = new FileOutputStream(
                            dir + IOUtil.FILE_SEPARATOR + fileName + Strings.DOT + extension);
            ImageIO.write(image, extension, output);
            output.flush();
        } finally {
            try {
                if (output != null) { // 关闭输出流
                    output.close();
                }
            } catch (final IOException e) { // 此处异常不需处理
            }
        }
    }

    /**
     * 保存图片
     *
     * @param bytes
     *            图片字节数组
     * @param dirPath
     *            存储位置
     * @param fileName
     *            文件名
     * @param extension
     *            后缀名
     * @throws IOException
     *             系统没有写入权限
     */
    public static void saveImage(final byte[] bytes, final String dirPath, final String fileName,
                    final String extension) throws IOException {
        FileOutputStream output = null;
        try {
            final File file = new File(dirPath);
            if (!file.exists()) { // 因在实例化FileOutputStream时如果Dir未存在会报错,所以先保证dir有效
                file.mkdirs();
            }
            output = new FileOutputStream(
                            dirPath + IOUtil.FILE_SEPARATOR + fileName + Strings.DOT + extension);
            FileCopyUtils.copy(bytes, output);
        } finally {
            try {
                if (output != null) { // 关闭输出流
                    output.close();
                }
            } catch (final IOException e) { // 此处异常不需处理
            }
        }
    }

    public static byte[] imageToBytes(final BufferedImage image, final String formatName)
                    throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, os);
        return os.toByteArray();
    }

    public static BufferedImage cropImage(final BufferedImage image, final String formatName,
                    final int x, final int y, final int width, final int height)
                    throws IOException {
        final Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(formatName);
        if (!readers.hasNext()) {
            return null;
        }
        final ImageReader reader = readers.next();
        final ByteArrayInputStream in = new ByteArrayInputStream(imageToBytes(image, formatName));
        final ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        final ImageReadParam param = reader.getDefaultReadParam();
        final Rectangle rect = new Rectangle(x, y, width, height);
        param.setSourceRegion(rect);
        return reader.read(0, param);
    }
}
