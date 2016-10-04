package org.truenewx.data.validation.rule;

import java.util.LinkedHashSet;
import java.util.Set;

import org.truenewx.core.util.CollectionUtil;

/**
 * 不能包含字符串规则
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NotContainsRule extends ValidationRule {
    /**
     * 不能包含的字符串集
     */
    private Set<String> values = new LinkedHashSet<>();
    private boolean notContainsHtmlChars;

    public Iterable<String> getValues() {
        return this.values;
    }

    /**
     * @return 是否具有不能包含的字符串
     */
    public boolean hasValue() {
        return this.values.size() > 0;
    }

    /**
     * 添加不能包含的字符串集
     *
     * @param values
     *            不能包含的字符串集
     */
    public void addValues(final String... values) {
        CollectionUtil.addAll(this.values, values);
    }

    public boolean isNotContainsHtmlChars() {
        return this.notContainsHtmlChars;
    }

    public void setNotContainsHtmlChars(final boolean notContainsHtmlChars) {
        this.notContainsHtmlChars = notContainsHtmlChars;
    }
}
