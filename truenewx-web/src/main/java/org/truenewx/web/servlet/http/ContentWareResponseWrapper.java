package org.truenewx.web.servlet.http;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 响应内容包装器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class ContentWareResponseWrapper extends HttpServletResponseWrapper {

    private CharArrayWriter output;
    private int statusCode;
    private HttpServletResponse response;

    public ContentWareResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
        this.output = new CharArrayWriter();
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        super.sendError(sc, msg);
        this.statusCode = sc;
        this.response.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        super.sendError(sc);
        this.statusCode = sc;
        this.response.sendError(sc);
    }

    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.statusCode = sc;
        this.response.setStatus(sc);
    }

    public boolean hasError() {
        return this.statusCode >= 400; // 400以上的状态码为错误码
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(this.output);
    }

    public String getContent() {
        return new String(this.output.toCharArray());
    }

    @Override
    protected void finalize() {
        this.output.flush();
        this.output.close();
        this.output = null;
    }

}
