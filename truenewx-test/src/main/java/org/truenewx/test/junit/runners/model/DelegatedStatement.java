package org.truenewx.test.junit.runners.model;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.truenewx.test.junit.rules.StatementProcedure;

/**
 * 委派给语句过程去执行的语句
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class DelegatedStatement extends Statement {
    private Statement base;
    private Description description;
    private StatementProcedure procedure;

    public DelegatedStatement(final Statement base, final Description description,
            final StatementProcedure procedure) {
        this.base = base;
        this.description = description;
        this.procedure = procedure;
    }

    @Override
    public void evaluate() throws Throwable {
        this.procedure.evaluate(this.base, this.description);
    }

}
