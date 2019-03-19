package org.truenewx.data.orm.hibernate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

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

/**
 * 持久化配置程序
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LocalSessionFactoryRegistry implements DataSourceLookup {
    /**
     * 模式-会话工厂的映射集
     */
    private Map<String, LocalSessionFactory> sessionFactoryMapping = new HashMap<>();
    /**
     * 实体-模式的映射集
     */
    private Map<String, String> entitySchemaMapping = new HashMap<>();
    /**
     * 实体类型-持久化类信息的映射集
     */
    private Map<Class<?>, PersistentClass> persistentClassMapping = new HashMap<>();

    /**
     * 注册本地会话工厂
     *
     * @param sessionFactory
     *            本地会话工厂
     */
    void register(LocalSessionFactory sessionFactory) {
        String schema = sessionFactory.getSchema();
        // 不能存在重复的模式
        Assert.isTrue(!this.sessionFactoryMapping.containsKey(schema),
                "Duplicate schema: " + schema);
        this.sessionFactoryMapping.put(schema, sessionFactory);
    }

    public String getSchema(String entityName) {
        String schema = this.entitySchemaMapping.get(entityName);
        if (schema == null) { // 实体所属模式未知，则依次在各模式配置中查找
            for (Entry<String, LocalSessionFactory> entry : this.sessionFactoryMapping.entrySet()) {
                Metadata metadata = entry.getValue().getMetadata();
                if (metadata.getEntityBinding(entityName) != null) { // 在某个配置中找到，则缓存实体名称-模式的映射，并返回结果
                    schema = entry.getKey();
                    this.entitySchemaMapping.put(entityName, schema);
                    break;
                }
            }
        }
        return schema;
    }

    public SessionFactory getSessionFactory(String schema) {
        if (schema != null) {
            return this.sessionFactoryMapping.get(schema);
        }
        return null;
    }

    @Override
    public DataSource getDataSource(String entityName) {
        String schema = getSchema(entityName);
        SessionFactory sessionFactory = getSessionFactory(schema);
        if (sessionFactory instanceof SessionFactoryImplementor) {
            SessionFactoryImplementor sfi = (SessionFactoryImplementor) sessionFactory;
            return (DataSource) sfi.getProperties().get(AvailableSettings.DATASOURCE);
        }
        return null;
    }

    public PersistentClass getPersistentClass(Class<?> entityClass) {
        if (this.persistentClassMapping.get(entityClass) == null) { // 如果缓存中没有，则依次遍历配置查找
            for (Entry<String, LocalSessionFactory> entry : this.sessionFactoryMapping.entrySet()) {
                Metadata metadata = entry.getValue().getMetadata();
                for (PersistentClass persistentClass : metadata.getEntityBindings()) {
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

    private PersistentClass getPersistentClass(String entityName) {
        String schema = getSchema(entityName);
        if (schema != null) {
            LocalSessionFactory sessionFactory = this.sessionFactoryMapping.get(schema);
            if (sessionFactory != null) {
                return sessionFactory.getMetadata().getEntityBinding(entityName);
            }
        }
        return null;
    }

    public String getTableName(String entityName) {
        PersistentClass persistentClass = getPersistentClass(entityName);
        if (persistentClass != null) {
            return persistentClass.getTable().getName();
        }
        return null;
    }

    private Property getProperty(String entityName, String propertyName) {
        PersistentClass persistentClass = getPersistentClass(entityName);
        if (persistentClass != null) {
            return persistentClass.getProperty(propertyName);
        }
        return null;
    }

    public Type getPropertyType(String entityName, String propertyName) {
        Property property = getProperty(entityName, propertyName);
        if (property != null) {
            return property.getType();
        }
        return null;
    }

    public Column getColumn(String entityName, String propertyName, int index) {
        Property property = getProperty(entityName, propertyName);
        if (property != null) {
            @SuppressWarnings("unchecked")
            Iterator<Column> columns = property.getColumnIterator();
            int i = 0;
            while (columns.hasNext()) {
                Column column = columns.next();
                if (i++ == index) {
                    return column;
                }
            }
        }
        return null;
    }

    public Column getColumn(String entityName, String propertyName) {
        return getColumn(entityName, propertyName, 0);
    }
}
