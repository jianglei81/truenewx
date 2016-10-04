package org.truenewx.data.query;

import java.util.Map;

/**
 * 查询参数接口
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface QueryParameter extends QueryOrders {

    /**
     * @return 页大小
     */
    int getPageSize();

    /**
     * @return 页码
     */
    int getPageNo();

    /**
     *
     * @return 所有参数集
     */
    Map<String, Object> getAll();

    /**
     *
     * @return 是否获取记录总数
     */
    boolean isTotalable();

    /**
     *
     * @return 是否查询记录清单
     */
    boolean isListable();

}
