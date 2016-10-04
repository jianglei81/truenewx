package org.truenewx.hibernate.functor;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;

/**
 * 表存在断言
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface TableExistsPredicate {

    Class<? extends Dialect> getDialectClass();

    boolean exists(DataSource dataSource, String tableName);

}
