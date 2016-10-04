package org.truenewx.web.servlet.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.truenewx.Framework;
import org.truenewx.core.Strings;
import org.truenewx.web.util.WebUtil;

/**
 * 内部WEB资源访问过滤器<br/>
 * 将进入本过滤器的资源文件访问引导至类路径下的/web/目录中
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class InternalWebAccessFilter implements Filter {
    /**
     * 内部WEB资源根目录
     */
    private static final String INTERNAL_WEB_ROOT = "/web";
    /**
     * 访问路径前缀
     */
    private String prefix = Strings.SLASH + Framework.NAME + INTERNAL_WEB_ROOT;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String prefix = filterConfig.getInitParameter("prefix");
        if (prefix != null) {
            this.prefix = prefix;
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                    final FilterChain chain) throws IOException, ServletException {
        String path = WebUtil.getRelativeRequestUrl((HttpServletRequest) request);
        if (path.startsWith(this.prefix)) {
            path = path.substring(this.prefix.length());

            final Resource resource = new ClassPathResource(INTERNAL_WEB_ROOT + path);
            if (resource.exists()) {
                final String extension = FilenameUtils.getExtension(path);
                if ("css".equalsIgnoreCase(extension)) {
                    response.setContentType("text/css");
                }
                final InputStream in = resource.getInputStream();
                final OutputStream out = response.getOutputStream();
                IOUtils.copy(in, out);
                in.close();
                out.close();
            } else {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return;
        }
        chain.doFilter(request, response);
    }

}
