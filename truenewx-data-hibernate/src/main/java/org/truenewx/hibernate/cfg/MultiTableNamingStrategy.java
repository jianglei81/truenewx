package org.truenewx.hibernate.cfg;

import javax.sql.DataSource;

import org.hibernate.cfg.EJB3NamingStrategy;
import org.springframework.util.Assert;
import org.truenewx.core.Strings;
import org.truenewx.hibernate.functor.TableExistsPredicate;

/**
 * 多表名映射命名策略
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MultiTableNamingStrategy extends EJB3NamingStrategy {

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
    public String tableName(final String tableName) {
        final String[] names = tableName.split(Strings.COMMA);
        if (names.length > 1) {
            for (String name : names) {
                name = name.trim();
                if (this.predicate.exists(this.dataSource, name)) {
                    return name;
                }
            }
        }
        return super.tableName(tableName);
    }
}
