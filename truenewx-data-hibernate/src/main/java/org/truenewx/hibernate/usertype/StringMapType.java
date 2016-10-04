package org.truenewx.hibernate.usertype;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.springframework.util.CollectionUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.CollectionUtil;
import org.truenewx.core.util.FilteredTokens;
import org.truenewx.data.validation.constraint.NotContains;
import org.truenewx.data.validation.constraint.NotContainsAngleBracket;
import org.truenewx.data.validation.constraint.NotContainsHtmlChars;
import org.truenewx.data.validation.constraint.NotContainsSpecialChars;
import org.truenewx.data.validation.constraint.NotContainsSqlChars;
import org.truenewx.data.validation.constraint.TagLimit;

/**
 * 字符串映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class StringMapType extends AbstractUserType implements ParameterizedType {

    private String propertyName;
    private String[] notContains;
    private FilteredTokens filteredTags;

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return String.class;
    }

    @Override
    public void setParameterValues(final Properties parameters) {
        if (parameters != null) {
            this.propertyName = parameters.getProperty("property");
        }
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
                    final SessionImplementor session, final Object owner)
                    throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (value != null && owner != null) {
            final String[] notContains = getNotContains(owner.getClass());
            if (notContains != null) {
                for (final String s : notContains) {
                    // 替换不能包含的字符串为空格，以解决数据中残留数据带有禁用字符串的问题
                    value = value.replace(s, Strings.SPACE);
                }
            }

            final FilteredTokens filteredTags = getFilteredTags(owner.getClass());
            if (filteredTags != null && !filteredTags.isEmpty()) {
                // 先替换掉禁止使用的标签
                final Set<String> excludedTags = filteredTags.getExcludes();
                if (excludedTags != null) {
                    for (final String tag : excludedTags) {
                        value = replaceTag(value, tag, Strings.SPACE);
                    }
                }
                // 再替换掉非允许使用的标签
                final Set<String> includes = filteredTags.getIncludes();
                if (!CollectionUtils.isEmpty(includes)) {
                    final StringBuffer sb = new StringBuffer(value);

                    int leftIndex = sb.indexOf("<");
                    int rightIndex = leftIndex >= 0 ? sb.indexOf(">", leftIndex) : -1;
                    while (leftIndex >= 0 && rightIndex >= 0) {
                        final String tagToken = sb.substring(leftIndex + 1, rightIndex); // <>中间的部分
                        final int spaceIndex = tagToken.indexOf(Strings.SPACE);
                        String tag = spaceIndex >= 0 ? tagToken.substring(0, spaceIndex) : tagToken;
                        if (tag.startsWith(Strings.SLASH)) { // 标签结束处
                            tag = tag.substring(Strings.SLASH.length());
                        }
                        if (!includes.contains(tag.toLowerCase())) { // 存在不允许的标签，则替换
                            sb.replace(leftIndex, rightIndex + 1, Strings.SPACE);
                        }
                        leftIndex = sb.indexOf("<", rightIndex);
                        rightIndex = leftIndex >= 0 ? sb.indexOf(">", leftIndex) : -1;
                    }

                    value = sb.toString();
                }
            }
        }
        return value;
    }

    private String[] getNotContains(final Class<?> type) {
        if (StringUtils.isNotBlank(this.propertyName) && this.notContains == null) {
            final Field field = ClassUtil.findField(type, this.propertyName);
            this.notContains = getNotContains(field);
        }
        return this.notContains;
    }

    private String[] getNotContains(final Field field) {
        if (field != null) {
            final NotContainsSpecialChars notContainsSpecialChars = field
                            .getAnnotation(NotContainsSpecialChars.class);
            if (notContainsSpecialChars != null) {
                final String[] htmlChars = notContainsSpecialChars.annotationType()
                                .getAnnotation(NotContainsHtmlChars.class).annotationType()
                                .getAnnotation(NotContains.class).value();
                final String[] sqlChars = notContainsSpecialChars.annotationType()
                                .getAnnotation(NotContainsSqlChars.class).annotationType()
                                .getAnnotation(NotContains.class).value();
                return ArrayUtils.addAll(htmlChars, sqlChars);
            }

            final Set<String> set = new HashSet<>();
            final NotContainsHtmlChars notContainsHtmlChars = field
                            .getAnnotation(NotContainsHtmlChars.class);
            if (notContainsHtmlChars != null) {
                final String[] htmlChars = notContainsHtmlChars.annotationType()
                                .getAnnotation(NotContains.class).value();
                CollectionUtil.addAll(set, htmlChars);
            } else { // 没有@NotContainsHtmlChars注解，@NotContainsAngleBracket注解才有意义
                final NotContainsAngleBracket notContainsAngleBracket = field
                                .getAnnotation(NotContainsAngleBracket.class);
                if (notContainsAngleBracket != null) {
                    final String[] angleBrackets = notContainsAngleBracket.annotationType()
                                    .getAnnotation(NotContains.class).value();
                    CollectionUtil.addAll(set, angleBrackets);
                }
            }
            final NotContainsSqlChars notContainsSqlChars = field
                            .getAnnotation(NotContainsSqlChars.class);
            if (notContainsSqlChars != null) {
                final String[] sqlChars = notContainsSqlChars.annotationType()
                                .getAnnotation(NotContains.class).value();
                CollectionUtil.addAll(set, sqlChars);
            }
            final NotContains notContains = field.getAnnotation(NotContains.class);
            if (notContains != null) {
                CollectionUtil.addAll(set, notContains.value());
            }
            return set.toArray(new String[set.size()]);
        }
        return new String[0];
    }

    private FilteredTokens getFilteredTags(final Class<?> type) {
        if (StringUtils.isNotBlank(this.propertyName) && this.filteredTags == null) {
            final Field field = ClassUtil.findField(type, this.propertyName);
            this.filteredTags = getFilteredTags(field);
        }
        return this.filteredTags;
    }

    private FilteredTokens getFilteredTags(final Field field) {
        final FilteredTokens tags = new FilteredTokens();
        if (field != null) {
            final TagLimit tagLimit = field.getAnnotation(TagLimit.class);
            if (tagLimit != null) {
                tags.addIncluded(tagLimit.allowed());
                tags.addExcluded(tagLimit.forbidden());
            }
        }
        return tags;
    }

    private String replaceTag(String value, String tag, final String target) {
        String s = value.toLowerCase();
        tag = tag.toLowerCase();

        // 替换形如<tag ***>的标签头
        final StringBuffer sb = new StringBuffer(value);
        final String tagToken = "<" + tag + Strings.SPACE;
        int leftIndex = s.indexOf(tagToken);
        while (leftIndex >= 0) {
            final int rightIndex = s.indexOf(">", leftIndex);
            if (rightIndex > leftIndex) {
                sb.replace(leftIndex, rightIndex + 1, target);
                value = sb.toString().toLowerCase();
                s = value.toLowerCase();
                leftIndex = s.indexOf(tagToken);
            }
        }
        // 替换形如<tag>的标签头
        value = value.replace("<" + tag + ">", target);
        // 替换形如</tag>的标签尾
        value = value.replace("</" + tag + ">", target);
        return value;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
                    final SessionImplementor session) throws HibernateException, SQLException {
        st.setObject(index, value);
    }

}
