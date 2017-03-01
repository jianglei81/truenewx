package org.truenewx.core.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 复合输出流<br/>
 * 同时往多个输出流中输出内容
 *
 * @author jianglei
 *
 */
public class CompositeOutputStream extends OutputStream {

    private OutputStream[] outs;

    public CompositeOutputStream(final OutputStream... outs) {
        this.outs = outs;
    }

    @Override
    public void write(final int b) throws IOException {
        for (final OutputStream out : this.outs) {
            out.write(b);
        }
    }

    @Override
    public void flush() throws IOException {
        for (final OutputStream out : this.outs) {
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (final OutputStream out : this.outs) {
            out.close();
        }
    }

}
