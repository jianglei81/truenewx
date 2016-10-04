package org.truenewx.data.query;

import java.util.Collections;
import java.util.List;

/**
 * 查询结果实现
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <R>
 *            结果记录类型
 */
public class QueryResultImpl<R> implements QueryResult<R> {
    /**
     * 结果记录集
     */
    private List<R> records;
    /**
     * 分页信息
     */
    private Paging paging;

    private QueryResultImpl(final List<R> dataList) {
        if (dataList == null) {
            this.records = Collections.emptyList();
        } else {
            this.records = dataList;
        }
    }

    public QueryResultImpl(final List<R> dataList, final Paging paging) {
        this(dataList);
        this.paging = paging;
    }

    public QueryResultImpl(final List<R> dataList, final int pageSize, final int pageNo,
                    final int total) {
        this(dataList, new Paging(pageSize, pageNo, total));
    }

    /**
     * 构建未知总数的查询结果
     *
     * @param dataList
     *            结果记录清单
     * @param pageSize
     *            页大小
     * @param pageNo
     *            页码
     */
    public QueryResultImpl(final List<R> dataList, int pageSize, int pageNo) {
        this(dataList);
        if (pageSize <= 0) {
            pageSize = this.records.size();
            pageNo = 1;
        }
        this.paging = new Paging(pageSize, pageNo, this.records.size() > pageSize);
        while (this.records.size() > pageSize) { // 确保结果数据数目不大于页大小
            this.records.remove(this.records.size() - 1);
        }
    }

    @Override
    public List<R> getRecords() {
        return this.records;
    }

    @Override
    public Paging getPaging() {
        return this.paging;
    }

    @Override
    public boolean isEmpty() {
        return this.paging.getTotal() < 0 && this.records.isEmpty();
    }

}
