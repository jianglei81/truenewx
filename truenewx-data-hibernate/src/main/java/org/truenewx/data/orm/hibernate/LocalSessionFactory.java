package org.truenewx.data.orm.hibernate;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.truenewx.data.orm.OrmConstants;

/**
 * 本地个性化SessionFactory
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LocalSessionFactory implements SessionFactory {

    private static final long serialVersionUID = 4621721836494557915L;

    private String schema;
    private Metadata metadata;
    private SessionFactory sessionFactory;

    public LocalSessionFactory(final String schema, final Metadata metadata,
            final SessionFactory sessionFactory) {
        if (StringUtils.isBlank(schema)) {
            this.schema = OrmConstants.DEFAULT_SCHEMA_NAME;
        } else {
            this.schema = schema;
        }
        this.metadata = metadata;
        this.sessionFactory = sessionFactory;
    }

    public String getSchema() {
        return this.schema;
    }

    public Metadata getMetadata() {
        return this.metadata;
    }

    @Override
    public Reference getReference() throws NamingException {
        return this.sessionFactory.getReference();
    }

    @Override
    public EntityManager createEntityManager() {
        return this.sessionFactory.createEntityManager();
    }

    @Override
    @SuppressWarnings("deprecation")
    public <T> List<EntityGraph<? super T>> findEntityGraphsByType(final Class<T> entityClass) {
        return this.sessionFactory.findEntityGraphsByType(entityClass);
    }

    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return this.sessionFactory.getSessionFactoryOptions();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public EntityManager createEntityManager(final Map map) {
        return this.sessionFactory.createEntityManager(map);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public SessionBuilder withOptions() {
        return this.sessionFactory.withOptions();
    }

    @Override
    public Session openSession() throws HibernateException {
        return this.sessionFactory.openSession();
    }

    @Override
    public EntityManager createEntityManager(final SynchronizationType synchronizationType) {
        return this.sessionFactory.createEntityManager(synchronizationType);
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public EntityManager createEntityManager(final SynchronizationType synchronizationType,
            final Map map) {
        return this.sessionFactory.createEntityManager(synchronizationType, map);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public StatelessSessionBuilder withStatelessOptions() {
        return this.sessionFactory.withStatelessOptions();
    }

    @Override
    public StatelessSession openStatelessSession() {
        return this.sessionFactory.openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(final Connection connection) {
        return this.sessionFactory.openStatelessSession(connection);
    }

    @Override
    public Statistics getStatistics() {
        return this.sessionFactory.getStatistics();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return this.sessionFactory.getCriteriaBuilder();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Metamodel getMetamodel() {
        return this.sessionFactory.getMetamodel();
    }

    @Override
    public boolean isClosed() {
        return this.sessionFactory.isClosed();
    }

    @Override
    public boolean isOpen() {
        return this.sessionFactory.isOpen();
    }

    @Override
    public Cache getCache() {
        return this.sessionFactory.getCache();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Set getDefinedFilterNames() {
        return this.sessionFactory.getDefinedFilterNames();
    }

    @Override
    public void close() {
        this.sessionFactory.close();
    }

    @Override
    public FilterDefinition getFilterDefinition(final String filterName) throws HibernateException {
        return this.sessionFactory.getFilterDefinition(filterName);
    }

    @Override
    public boolean containsFetchProfileDefinition(final String name) {
        return this.sessionFactory.containsFetchProfileDefinition(name);
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.sessionFactory.getProperties();
    }

    @Override
    public TypeHelper getTypeHelper() {
        return this.sessionFactory.getTypeHelper();
    }

    @Override
    @Deprecated
    @SuppressWarnings("rawtypes")
    public ClassMetadata getClassMetadata(final Class entityClass) {
        return this.sessionFactory.getClassMetadata(entityClass);
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this.sessionFactory.getPersistenceUnitUtil();
    }

    @Override
    @Deprecated
    public ClassMetadata getClassMetadata(final String entityName) {
        return this.sessionFactory.getClassMetadata(entityName);
    }

    @Override
    public void addNamedQuery(final String name, final Query query) {
        this.sessionFactory.addNamedQuery(name, query);
    }

    @Override
    @Deprecated
    public CollectionMetadata getCollectionMetadata(final String roleName) {
        return this.sessionFactory.getCollectionMetadata(roleName);
    }

    @Override
    @Deprecated
    public Map<String, ClassMetadata> getAllClassMetadata() {
        return this.sessionFactory.getAllClassMetadata();
    }

    @Override
    public <T> T unwrap(final Class<T> cls) {
        return this.sessionFactory.unwrap(cls);
    }

    @Override
    @Deprecated
    @SuppressWarnings("rawtypes")
    public Map getAllCollectionMetadata() {
        return this.sessionFactory.getAllCollectionMetadata();
    }

    @Override
    public <T> void addNamedEntityGraph(final String graphName, final EntityGraph<T> entityGraph) {
        this.sessionFactory.addNamedEntityGraph(graphName, entityGraph);
    }
}
