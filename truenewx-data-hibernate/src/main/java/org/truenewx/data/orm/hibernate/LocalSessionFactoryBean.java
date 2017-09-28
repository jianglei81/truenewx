package org.truenewx.data.orm.hibernate;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.truenewx.data.orm.OrmConstants;
import org.truenewx.hibernate.cfg.MultiTableNamingStrategy;
import org.truenewx.hibernate.functor.TableExistsPredicate;

/**
 * 对 {@link org.springframework.orm.hibernate5.LocalSessionFactoryBean} 的扩展
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LocalSessionFactoryBean
        extends org.springframework.orm.hibernate5.LocalSessionFactoryBean
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
    public void setPhysicalNamingStrategy(final PhysicalNamingStrategy physicalNamingStrategy) {
        super.setPhysicalNamingStrategy(physicalNamingStrategy);
        this.defaultNamingStrategy = false;
    }

    @Override
    protected SessionFactory buildSessionFactory(final LocalSessionFactoryBuilder sfb) {
        if (this.defaultNamingStrategy) { // 默认添加多表名映射命名策略
            final Properties properties = sfb.getProperties();
            final Dialect dialect = Dialect.getDialect(properties);
            final TableExistsPredicate predicate = getTableExistsPredicate(dialect);
            final DataSource dataSource = (DataSource) properties.get(AvailableSettings.DATASOURCE);
            sfb.setPhysicalNamingStrategy(new MultiTableNamingStrategy(dataSource, predicate));
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
            final ServiceRegistry sr = ((SessionFactoryImplementor) sessionFactory)
                    .getServiceRegistry();
            final StandardServiceRegistry ssr = new StandardServiceRegistryAdapter(sr);
            final Metadata metadata = getMetadataSources().getMetadataBuilder(ssr).build();
            this.sessionFactoryRegistry.register(this.schema, metadata, sessionFactory);
        }
        return sessionFactory;
    }

}
