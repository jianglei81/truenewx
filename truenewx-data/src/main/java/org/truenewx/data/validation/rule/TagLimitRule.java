package org.truenewx.data.validation.rule;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 标签限定规则
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TagLimitRule extends ValidationRule {

    private Set<String> allowed = new LinkedHashSet<>();
    private Set<String> forbidden = new LinkedHashSet<>();

    public Set<String> getAllowed() {
        return Collections.unmodifiableSet(this.allowed);
    }

    public Set<String> getForbidden() {
        return Collections.unmodifiableSet(this.forbidden);
    }

    public void addAllowed(final String... allowed) {
        for (final String tag : allowed) {
            this.allowed.add(tag.toLowerCase());
        }
    }

    public void addForbidden(final String... forbidden) {
        for (final String tag : forbidden) {
            this.forbidden.add(tag.toLowerCase());
        }
    }

}
