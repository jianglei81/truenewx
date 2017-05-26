package org.truenewx.data.orm;

import java.util.List;
import java.util.Map;

import org.truenewx.core.functor.algorithm.impl.AlgoFirst;

/**
 * 数据查询模板
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class DataQueryTemplate {

    /**
     * 非分页查询
     */
    public final <T> List<T> list(final CharSequence ql, final String paramName,
            final Object paramValue) {
        return list(ql, paramName, paramValue, 0, 0);
    }

    /**
     * 非分页查询
     */
    public final <T> List<T> list(final CharSequence ql, final Map<String, ?> params) {
        return list(ql, params, 0, 0);
    }

    /**
     * 非分页查询
     */
    public final <T> List<T> list(final CharSequence ql, final List<?> params) {
        return list(ql, params, 0, 0);
    }

    public final <T> T first(final CharSequence ql, final String paramName,
            final Object paramValue) {
        final List<T> list = list(ql, paramName, paramValue, 1, 1);
        return AlgoFirst.visit(list, null);
    }

    public final <T> T first(final CharSequence ql, final Map<String, ?> params) {
        final List<T> list = list(ql, params, 1, 1);
        return AlgoFirst.visit(list, null);
    }

    public final <T> T first(final CharSequence ql, final List<?> params) {
        final List<T> list = list(ql, params, 1, 1);
        return AlgoFirst.visit(list, null);
    }

    public final int count(final CharSequence ql, final String paramName, final Object paramValue) {
        final Number value = first(ql, paramName, paramValue);
        return value == null ? 0 : value.intValue();
    }

    public final int count(final CharSequence ql, final Map<String, ?> params) {
        final Number value = first(ql, params);
        return value == null ? 0 : value.intValue();
    }

    public final int count(final CharSequence ql, final List<?> params) {
        final Number value = first(ql, params);
        return value == null ? 0 : value.intValue();
    }

    /**
     * 分页查询
     */
    public abstract <T> List<T> list(CharSequence ql, String paramName, Object paramValue,
            int pageSize, int pageNo);

    /**
     * 分页查询
     */
    public abstract <T> List<T> list(CharSequence ql, Map<String, ?> params, int pageSize,
            int pageNo);

    /**
     * 分页查询
     */
    public abstract <T> List<T> list(CharSequence ql, List<?> params, int pageSize, int pageNo);

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql
     *            查询语句
     * @param paramName
     *            参数名
     * @param paramValue
     *            参数值
     * @param pageSize
     *            页大小
     * @param pageNo
     *            页码
     * @return 查询结果
     */
    public abstract <T> List<T> listWithOneMore(CharSequence ql, String paramName,
            Object paramValue, int pageSize, int pageNo);

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql
     *            查询语句
     * @param params
     *            参数映射集
     * @param pageSize
     *            页大小
     * @param pageNo
     *            页码
     * @return 查询结果
     */
    public abstract <T> List<T> listWithOneMore(CharSequence ql, Map<String, ?> params,
            int pageSize, int pageNo);

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql
     *            查询语句
     * @param params
     *            参数集
     * @param pageSize
     *            页大小
     * @param pageNo
     *            页码
     * @return 查询结果
     */
    public abstract <T> List<T> listWithOneMore(CharSequence ql, List<?> params, int pageSize,
            int pageNo);

}
