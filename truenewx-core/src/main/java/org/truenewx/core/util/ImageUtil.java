package org.truenewx.core.util;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * 图片工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ImageUtil {

    private ImageUtil() {
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

    public static BufferedImage cropImage(final BufferedImage image, final String formatName,
            final int x, final int y, final int width, final int height) throws IOException {
        final Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(formatName);
        if (!readers.hasNext()) {
            return null;
        }
        final ImageReader reader = readers.next();
        final ByteArrayInputStream in = new ByteArrayInputStream(IOUtil.imageToBytes(image, formatName));
        final ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        final ImageReadParam param = reader.getDefaultReadParam();
        final Rectangle rect = new Rectangle(x, y, width, height);
        param.setSourceRegion(rect);
        return reader.read(0, param);
    }

}
