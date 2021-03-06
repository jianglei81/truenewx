package org.truenewx.core.io;

import java.io.IOException;
import java.io.InputStream;

import org.truenewx.core.Strings;
import org.truenewx.core.util.MathUtil;

/**
 * 带有附加信息的输入流，配合读取 {@link AttachOutputStream} 写入的附加信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AttachInputStream extends InputStream {

    private InputStream in;
    private Boolean readAttachement = Boolean.FALSE;

    public AttachInputStream(final InputStream in) {
        this.in = in;
    }

    public String readAttachement() throws IOException {
        byte[] bytes;
        synchronized (this.readAttachement) {
            if (this.readAttachement) { // 已经读取附加信息，则不能再次读取
                return null;
            }
            // 先读取附加信息长度
            final int length = readAttachmentLength();
            // 再读取附加信息
            bytes = new byte[length];
            this.in.read(bytes);
            this.readAttachement = Boolean.TRUE;
        }
        return new String(bytes, Strings.ENCODING_UTF8);
    }

    private int readAttachmentLength() throws IOException {
        final byte[] bytes = new byte[4];
        this.in.read(bytes);
        return MathUtil.bytes2Int(bytes, 0);
    }

    @Override
    public int read() throws IOException {
        skipAttachment();
        return this.in.read();
    }

    private void skipAttachment() throws IOException {
        synchronized (this.readAttachement) {
            if (!this.readAttachement) { // 如果此时还没读取头部附加信息，则跳过附加信息
                this.in.skip(readAttachmentLength());
                this.readAttachement = true;
            }
        }
    }

    @Override
    public long skip(final long n) throws IOException {
        skipAttachment();
        return this.in.skip(n);
    }

    @Override
    public int available() throws IOException {
        skipAttachment();
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public synchronized void mark(final int readlimit) {
        try {
            skipAttachment();
            this.in.mark(readlimit);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

}
