package org.truenewx.data.orm.hibernate;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.dialect.Dialect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.truenewx.data.orm.OrmConstants;
import org.truenewx.hibernate.cfg.MultiTableNamingStrategy;
import org.truenewx.hibernate.functor.TableExistsPredicate;

/**
 * 对 {@link org.springframework.orm.hibernate4.LocalSessionFactoryBean} 的扩展
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LocalSessionFactoryBean
                extends org.springframework.orm.hibernate4.LocalSessionFactoryBean
                implements ApplicationContextAware {
    /**
     * 模式
     */
    private String schema = OrmConstants.DEFAULT_SCHEMA_NAME;
    private LocalSessionFactoryRegistry sessionFactoryRegistry;
    private ApplicationContext context;
    private boolean defaultNamingStrategy = true;

    /**
     * @param schema
     *            模式
     */
    public void setSchema(final String schema) {
        this.schema = schema;
    }

    @Autowired
    public void setSessionFactoryRegistry(
                    final LocalSessionFactoryRegistry sessionFactoryRegistry) {
        this.sessionFactoryRegistry = sessionFactoryRegistry;
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void setNamingStrategy(final NamingStrategy namingStrategy) {
        super.setNamingStrategy(namingStrategy);
        this.defaultNamingStrategy = false; // 标记未使用默认的命名策略
    }

    @Override
    protected SessionFactory buildSessionFactory(final LocalSessionFactoryBuilder sfb) {
        if (this.defaultNamingStrategy) { // 如果使用的是默认的命名策略，则添加多表名支持的命名策略
            final Properties properties = sfb.getProperties();
            final Dialect dialect = Dialect.getDialect(properties);
            final TableExistsPredicate predicate = getTableExistsPredicate(dialect);
            final DataSource dataSource = (DataSource) properties.get(AvailableSettings.DATASOURCE);
            sfb.setNamingStrategy(new MultiTableNamingStrategy(dataSource, predicate));
        }
        return super.buildSessionFactory(sfb);
    }

    private TableExistsPredicate getTableExistsPredicate(final Dialect dialect) {
        final Map<String, TableExistsPredicate> predicates = this.context
                        .getBeansOfType(TableExistsPredicate.class);
        for (final TableExistsPredicate predicate : predicates.values()) {
            if (predicate.getDialectClass().isAssignableFrom(dialect.getClass())) {
                return predicate;
            }
        }
        return null;
    }

    @Override
    public SessionFactory getObject() {
        final SessionFactory sessionFactory = super.getObject();
        if (sessionFactory != null) {
            this.sessionFactoryRegistry.register(this.schema, getConfiguration(), sessionFactory);
        }
        return sessionFactory;
    }

}
