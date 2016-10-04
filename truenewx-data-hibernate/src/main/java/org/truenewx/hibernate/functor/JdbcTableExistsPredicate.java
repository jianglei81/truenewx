package org.truenewx.hibernate.functor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 基于JDBC查询的表存在断言
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class JdbcTableExistsPredicate implements TableExistsPredicate {

    @Override
    public boolean exists(final DataSource dataSource, final String tableName) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            final PreparedStatement statement = connection.prepareStatement(getQuerySql());
            statement.setString(1, tableName);
            final ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    protected abstract String getQuerySql();

}
