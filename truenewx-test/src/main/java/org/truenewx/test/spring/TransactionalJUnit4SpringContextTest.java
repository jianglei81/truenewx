package org.truenewx.test.spring;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.truenewx.core.model.Sliced;
import org.truenewx.data.orm.dao.Dao;
import org.truenewx.data.orm.dao.EntityDao;
import org.truenewx.data.orm.dao.SlicedDao;
import org.truenewx.data.orm.dao.support.DaoFactory;
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

    @Autowired
    private DaoFactory daoFactory;

    protected final <T> List<T> getDataList(final Class<T> entityClass) {
        final EntityDao<T> dao = this.daoFactory.getDaoByEntityClass(entityClass);
        return dao.find((Map<String, Object>) null);
    }

    protected final <T> T getFirstData(final Class<T> entityClass) {
        final Dao<T> dao = this.daoFactory.getDaoByEntityClass(entityClass);
        return dao.first();
    }

    protected final <T extends Sliced<S>, S extends Serializable> T getFirstData(
            final Class<T> entityClass, final S slicer) {
        final SlicedDao<T, S> dao = this.daoFactory.getDaoByEntityClass(entityClass);
        return dao.first(slicer);
    }

}
