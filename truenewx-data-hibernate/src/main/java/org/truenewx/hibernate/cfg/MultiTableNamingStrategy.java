package org.truenewx.hibernate.cfg;

import javax.sql.DataSource;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.util.Assert;
import org.truenewx.core.Strings;
import org.truenewx.hibernate.functor.TableExistsPredicate;

/**
 * 多表名映射命名策略
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MultiTableNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private static final long serialVersionUID = -5851271869133476909L;
    private DataSource dataSource;
    private TableExistsPredicate predicate;

    public MultiTableNamingStrategy(final DataSource dataSource,
            final TableExistsPredicate predicate) {
        Assert.notNull(dataSource);
        this.dataSource = dataSource;
        Assert.notNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public Identifier toPhysicalTableName(final Identifier name, final JdbcEnvironment context) {
        final String[] names = name.getText().split(Strings.COMMA);
        if (names.length > 1) {
            for (String n : names) {
                n = n.trim();
                if (this.predicate.exists(this.dataSource, n)) {
                    return new Identifier(n, name.isQuoted());
                }
            }
        }
        return super.toPhysicalTableName(name, context);
    }
}
