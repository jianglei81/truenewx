package org.truenewx.data.orm;

import java.util.Map;

/**
 * 数据访问模板，除查询语句外，还能执行更新语句
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class DataAccessTemplate extends DataQueryTemplate {

    public abstract int update(CharSequence ul, String paramName, Object paramValue);

    public abstract int update(CharSequence ul, Map<String, ?> params);

}
