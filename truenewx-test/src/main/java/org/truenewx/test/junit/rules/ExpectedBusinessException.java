package org.truenewx.test.junit.rules;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.MultiException;
import org.truenewx.core.exception.SingleException;
import org.truenewx.test.annotation.TestBusinessException;

/**
 * 期望业务异常的单元测试规则
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ExpectedBusinessException extends TestRuleAdapter {

    @Override
    public void evaluate(final Statement base, final Description description) throws Throwable {
        try {
            base.evaluate();
        } catch (final BusinessException e) {
            final TestBusinessException tbe = description
                            .getAnnotation(TestBusinessException.class);
            if (tbe != null) {
                final String[] expectedCodes = tbe.value();
                Assert.assertEquals(1, expectedCodes.length);
                Assert.assertEquals(expectedCodes[0], e.getCode());
            } else { // 没有@TestBusinessException注解，则抛给上层处理
                throw e;
            }
        } catch (final MultiException me) {
            final TestBusinessException tbe = description
                            .getAnnotation(TestBusinessException.class);
            if (tbe != null) {
                final String[] expectedCodes = tbe.value();
                Assert.assertEquals(expectedCodes.length, me.getTotal());
                for (final SingleException se : me) {
                    if (se instanceof BusinessException) {
                        final BusinessException be = (BusinessException) se;
                        Assert.assertTrue(ArrayUtils.contains(expectedCodes, be.getCode()));
                    }
                }
            } else { // 没有@TestBusinessException注解，则抛给上层处理
                throw me;
            }
        }
    }
}
