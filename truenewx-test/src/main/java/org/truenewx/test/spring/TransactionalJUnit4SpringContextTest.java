package org.truenewx.test.spring;

import java.util.List;

import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.truenewx.core.util.CollectionUtil;
import org.truenewx.test.init.DataInitFactory;
import org.truenewx.test.junit.rules.ExpectedBusinessException;
import org.truenewx.test.junit.rules.LogCaption;

/**
 * 带自动事务的JUnit4+Spring环境测试
 *
 * @author jianglei
 * @since JDK 1.8
 */
@ContextConfiguration({ "/META-INF/spring/truenewx-core.xml", "/META-INF/spring/truenewx-data.xml",
        "/META-INF/spring/truenewx-data-hibernate.xml", "/META-INF/spring/truenewx-test.xml" })
public abstract class TransactionalJUnit4SpringContextTest
        extends AbstractTransactionalJUnit4SpringContextTests {
    /**
     * 日志
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 数据初始化工厂
     */
    @Autowired
    protected DataInitFactory dataInitFactory;
    @Rule
    public LogCaption logCaption = LogCaption.DEFAULT;

    @Rule
    public ExpectedBusinessException expectedBusinessException = new ExpectedBusinessException();

    @SuppressWarnings("unchecked")
    protected final <T, C extends T> C getData(final Class<T> modelClass, final int index) {
        final List<T> list = this.dataInitFactory.getDataList(modelClass);
        return (C) CollectionUtil.get(list, index);
    }

    @SuppressWarnings("unchecked")
    protected final <T, C extends T> C getData(final Class<T> modelClass, final int batch,
            final int index) {
        final List<T> list = this.dataInitFactory.getDataList(modelClass, batch);
        return (C) CollectionUtil.get(list, index);
    }

}
