package org.truenewx.web.spring.context.request;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

/**
 * 复合的异步Web请求拦截器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class CompositeAsyncWebRequestInterceptor implements AsyncWebRequestInterceptor {

    private List<AsyncWebRequestInterceptor> interceptors = new ArrayList<>();

    public void setInterceptors(final List<AsyncWebRequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public void preHandle(final WebRequest request) throws Exception {
        for (final WebRequestInterceptor interceptor : this.interceptors) {
            interceptor.preHandle(request);
        }
    }

    @Override
    public void postHandle(final WebRequest request, final ModelMap model) throws Exception {
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            this.interceptors.get(i).postHandle(request, model);
        }
    }

    @Override
    public void afterCompletion(final WebRequest request, final Exception ex) throws Exception {
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            this.interceptors.get(i).afterCompletion(request, ex);
        }
    }

    @Override
    public void afterConcurrentHandlingStarted(final WebRequest request) {
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            this.interceptors.get(i).afterConcurrentHandlingStarted(request);
        }
    }

}
