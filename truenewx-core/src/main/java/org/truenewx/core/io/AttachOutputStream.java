package org.truenewx.core.io;

import java.io.IOException;
import java.io.OutputStream;

import org.truenewx.core.Strings;
import org.truenewx.core.util.MathUtil;

/**
 * 带有附加信息的输出流，配套使用 {@link AttachInputStream} 读取附加信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AttachOutputStream extends OutputStream {

    private OutputStream out;

    public AttachOutputStream(final OutputStream out, final String attachment) throws IOException {
        this.out = out;
        final byte[] bytes = attachment == null ? new byte[0]
                : attachment.getBytes(Strings.ENCODING_UTF8);
        final int length = bytes.length;
        this.out.write(MathUtil.int2Bytes(length)); // 先写入4个字节的附加信息长度
        this.out.write(bytes); // 再写入附加信息
    }

    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }

}
