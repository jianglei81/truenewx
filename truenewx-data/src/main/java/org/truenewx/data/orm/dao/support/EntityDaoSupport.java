package org.truenewx.data.orm.dao.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.data.orm.DataAccessTemplate;
import org.truenewx.data.orm.dao.EntityDao;
import org.truenewx.data.query.Comparison;
import org.truenewx.data.query.Paging;
import org.truenewx.data.query.QueryParameter;
import org.truenewx.data.query.QueryResult;

/**
 * 实体DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class EntityDaoSupport<T> implements EntityDao<T> {

    /**
     * 获取实体类型<br/>
     * 默认实现通过反射机制获取，子类可覆写直接返回具体实体的类型以优化性能
     *
     * @return 实体类型
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getEntityClass() {
        return (Class<T>) ClassUtil.getActualGenericType(getClass(), 0);
    }

    protected abstract DataAccessTemplate getDataAccessTemplate(String entityName);

    protected List<T> find(final String entityName, final Map<String, ?> params,
            final String... fuzzyNames) {
        final StringBuffer hql = new StringBuffer("from ").append(entityName);
        Map<String, Object> qp = null;
        if (params != null && !params.isEmpty()) {
            hql.append(" where ");
            final String junction = " and ";
            qp = new HashMap<>();
            for (final Entry<String, ?> entry : params.entrySet()) {
                final String field = entry.getKey();
                final boolean fuzzy = ArrayUtils.contains(fuzzyNames, field);
                Object paramValue = entry.getValue();
                if (fuzzy && paramValue instanceof String
                        && StringUtils.isBlank((String) paramValue)) {
                    paramValue = null;
                }
                if (paramValue != null) {
                    final Comparison comparison = fuzzy ? Comparison.LIKE : Comparison.EQUAL;
                    final String paramName = field.replace('.', '_'); // 含有.的字段名用_替换形成绑定参数名
                    hql.append(field).append(comparison.toQlString()).append(Strings.COLON)
                            .append(paramName).append(junction);
                    if (fuzzy && paramValue instanceof String) {
                        paramValue = StringUtils.join(Strings.PERCENT, paramValue, Strings.PERCENT);
                    }
                    qp.put(paramName, paramValue);
                }
            }
            if (qp.size() > 0) {
                hql.delete(hql.length() - junction.length(), hql.length());
            }
        }
        return getDataAccessTemplate(entityName).list(hql, qp);
    }

    protected QueryResult<T> pagingQuery(final String entityName, CharSequence ql,
            final Map<String, Object> params, final QueryParameter parameter) {
        final int pageSize = parameter == null ? 0 : parameter.getPageSize();
        final boolean totalable = parameter == null ? true : parameter.isTotalable();
        int total;
        if (pageSize > 0 && totalable) { // 分页查询时需要获取总数才获取总数
            total = getDataAccessTemplate(entityName).count("select count(*) " + ql, params);
        } else { // 不分页查询无需获取总数
            total = Paging.UNKNOWN_TOTAL;
        }

        final int pageNo = parameter == null ? 0 : parameter.getPageNo();
        final boolean listable = parameter == null ? true : parameter.isListable();
        List<T> dataList;
        if (total != 0 && listable) { // 已知总数为0或无需查询记录清单，则不查询记录清单
            final String orderString = OqlUtil.buildOrderString(parameter);
            if (StringUtils.isNotBlank(orderString)) {
                if (ql instanceof StringBuffer) {
                    ((StringBuffer) ql).append(orderString);
                } else {
                    ql = ql.toString() + orderString;
                }
            }
            dataList = getDataAccessTemplate(entityName).list(ql, params, pageSize, pageNo);
            if (pageSize <= 0) { // 非分页查询，总数为结果记录条数
                total = dataList.size();
            }
        } else {
            dataList = new ArrayList<>();
        }
        return new QueryResult<>(dataList, pageSize, pageNo, total);
    }

    protected int countAll(final String entityName) {
        final String hql = "select count(*) from " + entityName;
        return getDataAccessTemplate(entityName).count(hql, (Map<String, ?>) null);
    }

    // 以下是对DependentDao的支持

    /**
     * 获取所有依赖实体对应的标识属性集合，为null表示没有依赖任何实体
     *
     * @return 依赖实体对应的标识属性集合，key - 依赖实体类型，value - 标识属性的访问路径，如：user.id
     */
    protected Map<Class<?>, String> getDependedKeyProperties() {
        return null;
    }

    public Class<?>[] getDependedClasses() {
        final Map<Class<?>, String> keyProperties = getDependedKeyProperties();
        if (keyProperties == null) {
            return null;
        }
        return keyProperties.keySet().toArray(new Class<?>[keyProperties.size()]);
    }

    protected String getDependedKeyProperty(final Class<?> dependedClass) {
        final Map<Class<?>, String> keyProperties = getDependedKeyProperties();
        if (keyProperties != null) {
            return keyProperties.get(dependedClass);
        }
        return null;
    }

    protected QueryResult<T> find(final String entityName, final Class<?> dependedClass,
            final Serializable dependedKey, final QueryParameter parameter) {
        final String keyProperty = getDependedKeyProperty(dependedClass);
        if (keyProperty == null) {
            return new QueryResult<>(new ArrayList<T>(), parameter.getPageSize(),
                    parameter.getPageNo());
        }
        final StringBuffer hql = new StringBuffer("select count(*) from ").append(entityName)
                .append(" where ").append(keyProperty).append("=:dependedKey");
        final Map<String, Object> params = new HashMap<>();
        params.put("dependedKey", dependedKey);

        return pagingQuery(entityName, hql, params, parameter);
    }

    protected int delete(final String entityName, final Class<?> dependedClass,
            final Serializable dependedKey) {
        final StringBuffer hql = new StringBuffer("delete from ").append(entityName)
                .append(" where ");
        final String keyProperty = getDependedKeyProperty(dependedClass);
        if (keyProperty == null) {
            return Paging.UNKNOWN_TOTAL;
        }
        hql.append(keyProperty).append("=:dependedKey");
        return getDataAccessTemplate(entityName).update(hql, "dependedKey", dependedKey);
    }
}
