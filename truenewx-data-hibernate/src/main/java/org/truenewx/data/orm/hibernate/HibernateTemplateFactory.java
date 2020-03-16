package org.truenewx.data.orm.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.data.orm.DataQueryTemplate;
import org.truenewx.data.orm.DataQueryTemplateFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Hibernate数据访问模板工厂
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class HibernateTemplateFactory implements DataQueryTemplateFactory, ContextInitializedBean {
    @Autowired
    private LocalSessionFactoryRegistry sessionFactoryRegistry;
    /**
     * Spring容器中已有的Hibernate数据访问模板
     */
    private Collection<HibernateTemplate> templates = new ArrayList<>();
    /**
     * 缓存的模式-Hibernate数据访问模板的映射集
     */
    private Map<String, HibernateTemplate> templateMapping = new HashMap<>();

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        this.templates.addAll(context.getBeansOfType(HibernateTemplate.class).values());
    }

    @Override
    public DataQueryTemplate getDataQueryTemplate(final String schema) {
        if (schema == null) {
            return null;
        }
        HibernateTemplate ht = this.templateMapping.get(schema);
        if (ht == null) {
            final SessionFactory sessionFactory = this.sessionFactoryRegistry
                    .getSessionFactory(schema);
            if (sessionFactory == null) {
                return null;
            }
            // 在已有的Hibernate数据访问模板中查找
            for (final HibernateTemplate template : this.templates) {
                if (template != null && template.getSessionFactory() == sessionFactory) { // 会话工厂相等即为匹配的
                    this.templateMapping.put(schema, template);
                    return template;
                }
            }
            // 非已有的Hibernate数据访问模板，则构建新的，并缓存
            ht = new HibernateTemplate();
            ht.setSessionFactory(sessionFactory);
            this.templateMapping.put(schema, ht);
            this.templates.add(ht);
        }
        return ht;
    }

    /**
     * 获取指定实体对应的Hibernate数据访问模板
     *
     * @param entityName 实体名称
     * @return Hibernate数据访问模板
     */
    public HibernateTemplate getHibernateTemplate(final String entityName) {
        final String schema = this.sessionFactoryRegistry.getSchema(entityName);
        return (HibernateTemplate) getDataQueryTemplate(schema);
    }

}
