package org.truenewx.test.spring;

import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
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
    @Rule
    public LogCaption logCaption = LogCaption.DEFAULT;

    @Rule
    public ExpectedBusinessException expectedBusinessException = new ExpectedBusinessException();

}
