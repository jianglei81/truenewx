package org.truenewx.data.query;

/**
 * 分页信息
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class Paging {
    /**
     * 未知总数
     */
    public static final int UNKNOWN_TOTAL = -1;

    private int pageSize;
    private int pageNo;
    private int total = Paging.UNKNOWN_TOTAL;
    private Boolean morePage;

    /**
     * 构建分页信息
     * 
     * @param pageSize
     *            页大小
     * @param pageNo
     *            页码
     * @param total
     *            总数
     */
    public Paging(final int pageSize, final int pageNo, final int total) {
        this.pageSize = pageSize;
        this.pageNo = pageNo <= 0 ? 1 : pageNo;
        this.total = total;
    }

    /**
     * 构建未知总数的分页信息
     * 
     * @param pageSize
     *            页大小
     * @param pageNo
     *            页码
     * @param morePage
     *            是否有更多页
     */
    public Paging(final int pageSize, final int pageNo, final boolean morePage) {
        this(pageSize, pageNo, Paging.UNKNOWN_TOTAL);
        this.morePage = morePage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageNo() {
        final int pageCount = getPageCount();
        if (this.total >= 0 && this.pageNo >= pageCount) {
            return pageCount == 0 ? 1 : pageCount;
        }
        return this.pageNo;
    }

    public int getTotal() {
        return this.total;
    }

    public int getPageCount() {
        if (isPageable()) {
            if (isCountable()) {
                if (this.total % this.pageSize != 0) {
                    return (this.total / this.pageSize) + 1;
                } else {
                    return this.total / this.pageSize;
                }
            } else if (this.morePage == Boolean.FALSE) { // 无总数但没有更多页时，当前页码即为总页数
                return this.pageNo;
            }
        }
        return 0;
    }

    public int getPreviousPage() {
        return this.pageNo <= 1 ? 1 : this.pageNo - 1;
    }

    public int getNextPage() {
        final int pageCount = getPageCount();
        return this.total >= 0 && this.pageNo >= pageCount ? pageCount : this.pageNo + 1;
    }

    public boolean isMorePage() {
        if (this.morePage != null) {
            return this.morePage;
        } else {
            return this.pageNo < getPageCount();
        }
    }

    public boolean isPageable() {
        return this.pageSize > 0;
    }

    public boolean isCountable() {
        return this.total >= 0;
    }

}
