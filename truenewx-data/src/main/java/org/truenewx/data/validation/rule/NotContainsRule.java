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
    // 尖括号和HTML字符会破坏数据格式，故用特殊的布尔值表示许可，由客户端做特殊判断
    private boolean angleBracket;
    private boolean html;

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
     * @param values 不能包含的字符串集
     */
    public void addValues(String... values) {
        CollectionUtil.addAll(this.values, values);
    }

    public boolean isAngleBracket() {
        return this.angleBracket;
    }

    public void setAngleBracket(boolean angleBracket) {
        this.angleBracket = angleBracket;
    }

    public boolean isHtml() {
        return this.html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }
}
