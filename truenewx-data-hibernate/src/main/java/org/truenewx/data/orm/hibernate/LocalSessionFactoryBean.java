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
     * @param schema 模式
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Autowired
    public void setSessionFactoryRegistry(LocalSessionFactoryRegistry sessionFactoryRegistry) {
        this.sessionFactoryRegistry = sessionFactoryRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void setPhysicalNamingStrategy(PhysicalNamingStrategy physicalNamingStrategy) {
        super.setPhysicalNamingStrategy(physicalNamingStrategy);
        this.defaultNamingStrategy = false;
    }

    @Override
    protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
        if (this.defaultNamingStrategy) { // 默认添加多表名映射命名策略
            Properties properties = sfb.getProperties();
            Dialect dialect = Dialect.getDialect(properties);
            TableExistsPredicate predicate = getTableExistsPredicate(dialect);
            DataSource dataSource = (DataSource) properties.get(AvailableSettings.DATASOURCE);
            sfb.setPhysicalNamingStrategy(new MultiTableNamingStrategy(dataSource, predicate));
        }

        return super.buildSessionFactory(sfb);
    }

    private TableExistsPredicate getTableExistsPredicate(Dialect dialect) {
        Map<String, TableExistsPredicate> predicates = this.context
                .getBeansOfType(TableExistsPredicate.class);
        for (TableExistsPredicate predicate : predicates.values()) {
            if (predicate.getDialectClass().isAssignableFrom(dialect.getClass())) {
                return predicate;
            }
        }
        return null;
    }

    @Override
    public SessionFactory getObject() {
        SessionFactory sessionFactory = super.getObject();
        if (sessionFactory != null) {
            ServiceRegistry sr = ((SessionFactoryImplementor) sessionFactory).getServiceRegistry();
            StandardServiceRegistry ssr = new StandardServiceRegistryAdapter(sr);
            @SuppressWarnings("deprecation")
            Metadata metadata = getMetadataSources().getMetadataBuilder(ssr).build();
            LocalSessionFactory lsf = new LocalSessionFactory(this.schema, metadata,
                    sessionFactory);
            this.sessionFactoryRegistry.register(lsf);
        }
        return sessionFactory;
    }

}
