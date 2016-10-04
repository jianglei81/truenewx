package org.truenewx.web.util;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.truenewx.core.spring.beans.ContextInitializedBean;

import com.extjs.JSBuilder;

/**
 * WEB资源压缩合并器<br/>
 * 针对CSS/JS文件进行压缩合并处理
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class WebResourceConsolidator implements ContextInitializedBean {

    /**
     * 配置文件路径
     */
    private String configFileName = "JSBuilder.jsb2";

    /**
     * @param configFileName
     *            配置文件路径
     *
     * @author jianglei
     */
    public void setConfigFileName(final String configFileName) {
        this.configFileName = configFileName;
    }

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Resource resource = context.getResource(this.configFileName);
        if (resource.exists()) {
            final File file = resource.getFile();
            final String projectFile = file.getAbsolutePath();
            final String homeDir = file.getParentFile().getAbsolutePath();
            JSBuilder.build(homeDir, projectFile);
        }
    }
}
