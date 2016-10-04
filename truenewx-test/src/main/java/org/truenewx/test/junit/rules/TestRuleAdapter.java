package org.truenewx.test.junit.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.truenewx.test.junit.runners.model.DelegatedStatement;

/**
 * 单元测试扩展规则适配器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class TestRuleAdapter implements TestRule, StatementProcedure {

    @Override
    public final Statement apply(final Statement base, final Description description) {
        return new DelegatedStatement(base, description, this);
    }

}
