package org.truenewx.test.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.truenewx.core.annotation.Caption;

/**
 * 输出@Caption注解日志的单元测试规则
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LogCaption extends TestRuleAdapter {
    /**
     * 默认实例
     */
    public static LogCaption DEFAULT = new LogCaption(true);
    /**
     * 日志输出是否附加测试类名称
     */
    private boolean appendTestClassName;

    public LogCaption(final boolean appendTestClassName) {
        this.appendTestClassName = appendTestClassName;
    }

    @Override
    public void evaluate(final Statement base, final Description description) throws Throwable {
        final Caption caption = description.getAnnotation(Caption.class);
        if (caption != null) {
            final Class<?> testClass = description.getTestClass();
            final Logger logger = LoggerFactory.getLogger(getClass());
            if (logger.isInfoEnabled()) {
                final String info;
                if (this.appendTestClassName) {
                    info = new StringBuffer("[").append(testClass.getSimpleName()).append("]")
                                    .append(caption.value()).toString();
                } else {
                    info = caption.value();
                }
                logger.info(info);
            }
        }
        base.evaluate();
    }

}
