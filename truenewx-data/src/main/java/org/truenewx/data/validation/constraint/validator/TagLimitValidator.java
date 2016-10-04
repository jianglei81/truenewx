package org.truenewx.data.validation.constraint.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.util.ArrayUtil;
import org.truenewx.core.util.StringUtil;
import org.truenewx.data.validation.constraint.TagLimit;

/**
 * 标签限定校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TagLimitValidator implements ConstraintValidator<TagLimit, CharSequence> {

    private String[] allowed;

    private String[] forbidden;

    @Override
    public void initialize(final TagLimit annotation) {
        this.allowed = annotation.allowed();
        ArrayUtil.toLowerCase(this.allowed);
        this.forbidden = annotation.forbidden();
        ArrayUtil.toLowerCase(this.forbidden);
    }

    @Override
    public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
        String s = value == null ? null : value.toString();
        if (StringUtils.isNotBlank(s) && s.contains("<") && s.contains(">")) {
            s = s.trim();
            if (this.allowed.length == 0 && this.forbidden.length == 0) { // 限制所有标签
                return !StringUtil.regexMatch(s, ".*<(?i)[a-z]+.*>.*");
            }
            if (this.allowed.length > 0) { // 仅允许的标签，禁止其它标签
                // 正则表达式写不出，只得用笨办法
                int leftIndex = s.indexOf("<");
                int rightIndex = leftIndex >= 0 ? s.indexOf(">", leftIndex) : -1;
                while (leftIndex >= 0 && rightIndex >= 0) {
                    final String sub = s.substring(leftIndex + 1, rightIndex); // <>中间的部分
                    final int spaceIndex = sub.indexOf(Strings.SPACE);
                    String tag = spaceIndex >= 0 ? sub.substring(0, spaceIndex) : sub;
                    if (tag.startsWith(Strings.SLASH)) { // 标签结束处
                        tag = tag.substring(Strings.SLASH.length());
                    }
                    if (!ArrayUtils.contains(this.allowed, tag.toLowerCase())) {
                        return false; // 存在不允许的标签，则直接返回false
                    }
                    leftIndex = s.indexOf("<", rightIndex);
                    rightIndex = leftIndex >= 0 ? s.indexOf(">", leftIndex) : -1;
                }
            }
            if (this.forbidden.length > 0) { // 禁止的标签
                // 无漏洞的正则表达式难以理解，还是以字符串操作进行判断
                s = s.toLowerCase();
                for (final String tag : this.forbidden) {
                    if (s.contains("<" + tag + ">") || s.contains("<" + tag + Strings.SPACE)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

}
