package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Component;
import org.truenewx.data.validation.rule.DecimalRule;

/**
 * 数值规则的构建器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class DecimalRuleBuilder implements ValidationRuleBuilder<DecimalRule> {

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[] { Range.class, Digits.class, Min.class, Max.class, DecimalMin.class,
                        DecimalMax.class };
    }

    @Override
    public void update(final Annotation annotation, final DecimalRule rule) {
        final Class<? extends Annotation> annoClass = annotation.annotationType();
        if (annoClass == Range.class) {
            updateRule(rule, (Range) annotation);
        } else if (annoClass == Digits.class) {
            updateRule(rule, (Digits) annotation);
        } else if (annoClass == Min.class) {
            updateRule(rule, (Min) annotation);
        } else if (annoClass == Max.class) {
            updateRule(rule, (Max) annotation);
        } else if (annoClass == DecimalMin.class) {
            updateRule(rule, (DecimalMin) annotation);
        } else if (annoClass == DecimalMax.class) {
            updateRule(rule, (DecimalMax) annotation);
        }
    }

    private void updateRule(final DecimalRule rule, final DecimalMax dm) {
        try {
            final BigDecimal max = new BigDecimal(dm.value());
            if (max.compareTo(rule.getMax()) < 0) {
                rule.setMax(max);
            }
        } catch (final NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateRule(final DecimalRule rule, final DecimalMin dm) {
        try {
            final BigDecimal min = new BigDecimal(dm.value());
            if (min.compareTo(rule.getMin()) > 0) {
                rule.setMin(min);
            }
        } catch (final NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateRule(final DecimalRule rule, final Max max) {
        final BigDecimal maxValue = BigDecimal.valueOf(max.value());
        if (maxValue.compareTo(rule.getMax()) < 0) {
            rule.setMax(maxValue);
        }
    }

    private void updateRule(final DecimalRule rule, final Min min) {
        final BigDecimal minValue = BigDecimal.valueOf(min.value());
        if (minValue.compareTo(rule.getMin()) > 0) {
            rule.setMin(minValue);
        }
    }

    private void updateRule(final DecimalRule rule, final Digits digits) {
        int scale = digits.fraction();
        if (scale < rule.getScale()) {
            rule.setScale(scale);
        }
        scale = rule.getScale();
        final int precision = digits.integer() + (scale < 0 ? 0 : scale);
        if (precision < rule.getPrecision()) {
            rule.setPrecision(precision);
        }
    }

    private void updateRule(final DecimalRule rule, final Range range) {
        final BigDecimal min = BigDecimal.valueOf(range.min());
        if (min.compareTo(rule.getMin()) > 0) {
            rule.setMin(min);
        }
        final BigDecimal max = BigDecimal.valueOf(range.max());
        if (max.compareTo(rule.getMax()) < 0) {
            rule.setMax(max);
        }
    }

    @Override
    public DecimalRule create(final Annotation annotation) {
        final Class<? extends Annotation> annoClass = annotation.annotationType();
        if (annoClass == Range.class) {
            return createRule((Range) annotation);
        } else if (annoClass == Digits.class) {
            return createRule((Digits) annotation);
        } else if (annoClass == Min.class) {
            return createRule((Min) annotation);
        } else if (annoClass == Max.class) {
            return createRule((Max) annotation);
        } else if (annoClass == DecimalMin.class) {
            return createRule((DecimalMin) annotation);
        } else if (annoClass == DecimalMax.class) {
            return createRule((DecimalMax) annotation);
        }
        return null;
    }

    private DecimalRule createRule(final DecimalMax dm) {
        final DecimalRule rule = new DecimalRule();
        try {
            final BigDecimal max = new BigDecimal(dm.value());
            if (max.compareTo(rule.getMax()) < 0) {
                rule.setMax(max);
            }
            return rule;
        } catch (final NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DecimalRule createRule(final DecimalMin dm) {
        final DecimalRule rule = new DecimalRule();
        try {
            final BigDecimal min = new BigDecimal(dm.value());
            if (min.compareTo(rule.getMin()) > 0) {
                rule.setMin(min);
            }
            return rule;
        } catch (final NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DecimalRule createRule(final Max max) {
        final DecimalRule rule = new DecimalRule();
        updateRule(rule, max);
        return rule;
    }

    private DecimalRule createRule(final Min min) {
        final DecimalRule rule = new DecimalRule();
        updateRule(rule, min);
        return rule;
    }

    private DecimalRule createRule(final Digits digits) {
        final DecimalRule rule = new DecimalRule();
        updateRule(rule, digits);
        return rule;
    }

    private DecimalRule createRule(final Range range) {
        final DecimalRule rule = new DecimalRule();
        updateRule(rule, range);
        return rule;
    }

}
