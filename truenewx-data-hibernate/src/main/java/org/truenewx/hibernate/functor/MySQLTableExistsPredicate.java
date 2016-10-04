package org.truenewx.hibernate.functor;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.stereotype.Repository;

/**
 * MySQL的表存在断言
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Repository
public class MySQLTableExistsPredicate extends JdbcTableExistsPredicate {

    @Override
    public Class<? extends Dialect> getDialectClass() {
        return MySQLDialect.class;
    }

    @Override
    protected String getQuerySql() {
        return "show tables like ?";
    }

}
