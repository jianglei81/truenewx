package org.truenewx.data.orm.dao.support;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.data.query.Comparison;

/**
 * 对象查询语言(OQL)工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class OqlUtil {

    private OqlUtil() {
    }

    /**
     * 根据指定查询排序序列构建order by子句<br/>
     * 如果无排序设置，则返回""，否则返回以空格开头的形如：" order by ..."的order by子句
     *
     * @param orders
     *            查询排序序列
     * @return order by子句
     */
    public static String buildOrderString(final Iterable<Entry<String, Boolean>> orders) {
        final StringBuffer orderBy = new StringBuffer();
        if (orders != null) {
            for (final Entry<String, Boolean> entry : orders) {
                orderBy.append(Strings.COMMA).append(entry.getKey());
                if (entry.getValue() == Boolean.TRUE) {
                    orderBy.append(" desc");
                }
            }
        }
        if (orderBy.length() > 0) {
            orderBy.replace(0, Strings.COMMA.length(), Strings.SPACE); // 用空格替代第一个逗号
            orderBy.insert(0, " order by"); // 前面加order by
        }
        return orderBy.toString();
    }

    /**
     * 构建OR条件子句
     *
     * @param params
     *            查询参数映射集，相关查询参数会写入该映射集中
     * @param fieldName
     *            字段名
     * @param fieldParamValues
     *            字段参数值
     * @param comparison
     *            条件比较符
     * @return OR条件子句
     *
     * @author jianglei
     */
    public static String buildOrConditionString(final Map<String, Object> params,
            final String fieldName, final Collection<?> fieldParamValues, Comparison comparison) {
        final StringBuffer condition = new StringBuffer();
        if (fieldParamValues != null && fieldParamValues.size() > 0) {
            if (comparison == null) { // 默认为等于比较符
                comparison = Comparison.EQUAL;
            }
            // 等于和不等于在参数个数大于5后使用IN/NOT IN代替
            if ((comparison != Comparison.EQUAL || comparison != Comparison.NOT_EQUAL)
                    && fieldParamValues.size() > 5) {
                condition.append(fieldName);
                if (comparison == Comparison.EQUAL) {
                    condition.append(Comparison.IN.toQlString());
                } else {
                    condition.append(Comparison.NOT_IN.toQlString());
                }
                final String paramName = fieldName.replaceAll("\\.", Strings.UNDERLINE);
                condition.append(Strings.LEFT_BRACKET).append(Strings.COLON).append(paramName)
                        .append(Strings.RIGHT_BRACKET);
                params.put(paramName, fieldParamValues);
            } else {
                final String junction = " or ";
                int i = 0;
                for (final Object fieldParamValue : fieldParamValues) {
                    condition.append(junction).append(fieldName);
                    if (fieldParamValue != null) { // 忽略为null的参数值
                        final String paramName = fieldName.replaceAll("\\.", Strings.UNDERLINE)
                                + (i++);
                        condition.append(comparison.toQlString()).append(Strings.COLON)
                                .append(paramName);
                        if (comparison == Comparison.LIKE || comparison == Comparison.NOT_LIKE) {
                            params.put(paramName, StringUtils.join(Strings.PERCENT,
                                    fieldParamValue.toString(), Strings.PERCENT));
                        } else {
                            params.put(paramName, fieldParamValue);
                        }
                    }
                }
                if (fieldParamValues.size() == 1) { // 一个字段参数不需要添加括号
                    condition.delete(0, junction.length());
                } else {
                    condition.replace(0, junction.length(), Strings.LEFT_BRACKET)
                            .append(Strings.RIGHT_BRACKET); // 去掉多余的or后添加括号
                }
            }
        }
        return condition.toString();
    }

    /**
     * 构建OR条件子句
     *
     * @param params
     *            查询参数映射集，相关查询参数会写入该映射集中
     * @param fieldName
     *            字段名
     * @param fieldParamValues
     *            字段参数值
     * @param comparison
     *            条件比较符
     * @return OR条件子句
     *
     * @author jianglei
     */
    public static String buildOrConditionString(final Map<String, Object> params,
            final String fieldName, final Object[] fieldParamValues, Comparison comparison) {
        final StringBuffer condition = new StringBuffer();
        if (fieldParamValues != null && fieldParamValues.length > 0) {
            if (comparison == null) { // 默认为等于比较符
                comparison = Comparison.EQUAL;
            }
            // 等于和不等于在参数个数大于5后使用IN/NOT IN代替
            if ((comparison == Comparison.EQUAL || comparison == Comparison.NOT_EQUAL)
                    && fieldParamValues.length > 5) {
                condition.append(fieldName);
                if (comparison == Comparison.EQUAL) {
                    condition.append(Comparison.IN.toQlString());
                } else {
                    condition.append(Comparison.NOT_IN.toQlString());
                }
                final String paramName = fieldName.replaceAll("\\.", Strings.UNDERLINE);
                condition.append(Strings.LEFT_BRACKET).append(Strings.COLON).append(paramName)
                        .append(Strings.RIGHT_BRACKET);
                params.put(paramName, fieldParamValues);
            } else {
                final String junction = " or ";
                int i = 0;
                for (final Object fieldParamValue : fieldParamValues) {
                    condition.append(junction).append(fieldName);
                    if (fieldParamValue != null) { // 忽略为null的参数值
                        final String paramName = fieldName.replaceAll("\\.", Strings.UNDERLINE)
                                + (i++);
                        condition.append(comparison.toQlString()).append(Strings.COLON)
                                .append(paramName);
                        if (comparison == Comparison.LIKE || comparison == Comparison.NOT_LIKE) {
                            params.put(paramName, StringUtils.join(Strings.PERCENT,
                                    fieldParamValue.toString(), Strings.PERCENT));
                        } else {
                            params.put(paramName, fieldParamValue);
                        }
                    }
                }
                if (fieldParamValues.length == 1) { // 一个字段参数不需要添加括号
                    condition.delete(0, junction.length());
                } else {
                    condition.replace(0, junction.length(), Strings.LEFT_BRACKET)
                            .append(Strings.RIGHT_BRACKET); // 去掉多余的or后添加括号
                }
            }
        }
        return condition.toString();
    }
}
