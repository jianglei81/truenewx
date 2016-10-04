package org.truenewx.test.spring;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.truenewx.core.functor.algorithm.impl.AlgoDefaultValue;
import org.truenewx.test.junit.rules.ExpectedBusinessException;
import org.truenewx.test.junit.rules.LogCaption;

/**
 * JUnit4+Spring环境测试
 *
 * @author jianglei
 * @since JDK 1.8
 */
@ContextConfiguration({ "/META-INF/spring/truenewx-core.xml" })
public abstract class JUnit4SpringContextTest extends AbstractJUnit4SpringContextTests {

    /**
     * 日志
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Rule
    public LogCaption logCaption = LogCaption.DEFAULT;

    @Rule
    public ExpectedBusinessException expectedBusinessException = new ExpectedBusinessException();

    /**
     * 给指定对象填充默认属性值（除id之外）.
     *
     * @param obj
     *            对象
     * @return 原对象
     */
    protected final <T> T fillDefaultPropertyValue(final T obj) {
        for (final PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(obj.getClass())) {
            try {
                final Method writeMethod = pd.getWriteMethod();
                if (writeMethod != null && !"setId".equals(writeMethod.getName())) { // 忽略id字段
                    final Class<?> clazz = pd.getPropertyType();
                    writeMethod.invoke(obj, AlgoDefaultValue.visit(clazz));
                }
            } catch (final Exception e) {
                // 忽略异常
            }
        }
        return obj;
    }
}
