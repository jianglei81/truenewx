package org.truenewx.data.orm.hibernate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.springframework.util.Assert;
import org.truenewx.data.jdbc.datasource.DataSourceLookup;
import org.truenewx.data.orm.OrmConstants;

/**
 * 持久化配置程序
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LocalSessionFactoryRegistry implements DataSourceLookup {
    /**
     * 模式-配置的映射集
     */
    private Map<String, Metadata> metadataMapping = new HashMap<>();
    /**
     * 模式-会话工厂的映射集
     */
    private Map<String, SessionFactory> sessionFactoryMapping = new HashMap<>();
    /**
     * 实体-模式的映射集
     */
    private Map<String, String> entitySchemaMapping = new HashMap<>();
    /**
     * 实体类型-持久化类信息的映射集
     */
    private Map<Class<?>, PersistentClass> persistentClassMapping = new HashMap<>();

    /**
     * 注册会话工厂
     *
     * @param schema
     *            模式名
     * @param metadata
     *            配置
     * @param sessionFactory
     *            会话工厂
     */
    void register(String schema, final Metadata metadata, final SessionFactory sessionFactory) {
        if (StringUtils.isBlank(schema)) {
            schema = OrmConstants.DEFAULT_SCHEMA_NAME;
        }
        // 不能存在重复的模式
        Assert.isTrue(!this.metadataMapping.containsKey(schema), "Duplicate schema: " + schema);
        this.metadataMapping.put(schema, metadata);
        Assert.isTrue(!this.sessionFactoryMapping.containsKey(schema),
                "Duplicate schema: " + schema);
        this.sessionFactoryMapping.put(schema, sessionFactory);
    }

    public String getSchema(final String entityName) {
        String schema = this.entitySchemaMapping.get(entityName);
        if (schema == null) { // 实体所属模式未知，则依次在各模式配置中查找
            for (final Entry<String, Metadata> entry : this.metadataMapping.entrySet()) {
                if (entry.getValue().getEntityBinding(entityName) != null) { // 在某个配置中找到，则缓存实体名称-模式的映射，并返回结果
                    schema = entry.getKey();
                    this.entitySchemaMapping.put(entityName, schema);
                    break;
                }
            }
        }
        return schema;
    }

    public SessionFactory getSessionFactory(final String schema) {
        if (schema != null) {
            return this.sessionFactoryMapping.get(schema);
        }
        return null;
    }

    @Override
    public DataSource getDataSource(final String entityName) {
        final String schema = getSchema(entityName);
        final SessionFactory sessionFactory = getSessionFactory(schema);
        if (sessionFactory instanceof SessionFactoryImplementor) {
            final SessionFactoryImplementor sfi = (SessionFactoryImplementor) sessionFactory;
            return (DataSource) sfi.getProperties().get(AvailableSettings.DATASOURCE);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Iterator<Property> getClassProperties(final Class<?> entityClass) {
        final PersistentClass persistentClass = getClassMapping(entityClass);
        return persistentClass == null ? null : persistentClass.getPropertyIterator();
    }

    private PersistentClass getClassMapping(final Class<?> entityClass) {
        if (this.persistentClassMapping.get(entityClass) == null) { // 如果缓存中没有，则依次遍历配置查找
            for (final Entry<String, Metadata> entry : this.metadataMapping.entrySet()) {
                final Metadata metadata = entry.getValue();
                for (final PersistentClass persistentClass : metadata.getEntityBindings()) {
                    if (persistentClass.getMappedClass() == entityClass) {
                        // 匹配，则缓存并返回
                        this.persistentClassMapping.put(entityClass, persistentClass);
                        return persistentClass;
                    }
                }
            }
        }
        return null;
    }

    private PersistentClass getClassMapping(final String entityName) {
        final String schema = getSchema(entityName);
        if (schema != null) {
            final Metadata metadata = this.metadataMapping.get(schema);
            if (metadata != null) {
                return metadata.getEntityBinding(entityName);
            }
        }
        return null;
    }

    public String getTableName(final String entityName) {
        final PersistentClass persistentClass = getClassMapping(entityName);
        if (persistentClass != null) {
            return persistentClass.getTable().getName();
        }
        return null;
    }

    private Property getProperty(final String entityName, final String propertyName) {
        final PersistentClass persistentClass = getClassMapping(entityName);
        if (persistentClass != null) {
            return persistentClass.getProperty(propertyName);
        }
        return null;
    }

    public Type getPropertyType(final String entityName, final String propertyName) {
        final Property property = getProperty(entityName, propertyName);
        if (property != null) {
            return property.getType();
        }
        return null;
    }

    public Column getColumn(final String entityName, final String propertyName, final int index) {
        final Property property = getProperty(entityName, propertyName);
        if (property != null) {
            @SuppressWarnings("unchecked")
            final Iterator<Column> columns = property.getColumnIterator();
            int i = 0;
            while (columns.hasNext()) {
                final Column column = columns.next();
                if (i++ == index) {
                    return column;
                }
            }
        }
        return null;
    }

    public Column getColumn(final String entityName, final String propertyName) {
        return getColumn(entityName, propertyName, 0);
    }
}
