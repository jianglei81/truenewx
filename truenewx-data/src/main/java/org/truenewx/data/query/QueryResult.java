package org.truenewx.data.query;

import java.util.List;

/**
 * 查询结果
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <R>
 *            结果记录类型
 */
public interface QueryResult<R> {
    /**
     * @return 结果记录集
     */
    List<R> getRecords();

    /**
     * @return 分页信息
     */
    Paging getPaging();

    /**
     *
     * @return 是否为空，true - 既没有总数也没有记录
     */
    boolean isEmpty();
}
