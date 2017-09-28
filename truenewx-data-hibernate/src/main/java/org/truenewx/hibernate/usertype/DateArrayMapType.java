package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.truenewx.core.Strings;
import org.truenewx.core.functor.algorithm.impl.AlgoJoin;
import org.truenewx.core.functor.algorithm.impl.AlgoSplit;
import org.truenewx.core.functor.impl.FuncFormatDate;
import org.truenewx.core.functor.impl.FuncParseDate;
import org.truenewx.core.util.DateUtil;

/**
 * 原始整型数组映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DateArrayMapType extends ArrayMapType {

    private FuncParseDate funcParseDate;
    private FuncFormatDate funcFormatDate;

    @Override
    public void setParameterValues(final Properties parameters) {
        super.setParameterValues(parameters);
        final String pattern = parameters.getProperty("pattern", DateUtil.LONG_DATE_PATTERN);
        this.funcParseDate = new FuncParseDate(pattern);
        this.funcFormatDate = new FuncFormatDate(pattern);
    }

    @Override
    public Class<?> returnedClass() {
        return Date[].class;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
            final SharedSessionContractImplementor session, final Object owner)
            throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (value != null) {
            final String[] array = value.split(Strings.COMMA);
            final Date[] result = new Date[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = DateUtil.parse(array[i], this.funcFormatDate.getPattern());
            }
            return AlgoSplit.visit(value, Strings.COMMA, this.funcParseDate, Date.class);
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
            final SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value != null) {
            final Date[] array = (Date[]) value;
            if (this.size > 0 && array.length > this.size) {
                throw getSizeException();
            }
            st.setString(index, AlgoJoin.visit(array, Strings.COMMA, this.funcFormatDate));
        } else {
            st.setString(index, null);
        }
    }

}
