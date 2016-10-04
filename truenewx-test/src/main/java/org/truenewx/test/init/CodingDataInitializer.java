package org.truenewx.test.init;

import java.util.List;

import org.truenewx.data.model.Entity;

/**
 * 通过编码初始化数据的数据初始化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class CodingDataInitializer<T extends Entity> extends DataInitializer<T> {

    @Override
    protected boolean create() {
        if (!isInitialized()) {
            final List<T> dataList = createDataList();
            setDataList(dataList);
            for (final T data : dataList) {
                getDao().save(data);
                if (afterSaved(data)) {
                    getDao().save(data);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 创建初始化数据清单
     *
     * @return 初始化数据清单，不能为null
     *
     * @author jianglei
     */
    protected abstract List<T> createDataList();

    /**
     * 每条数据保存后调用，用于处理id生成后才能进行的动作
     *
     * @param data
     *            每一条数据
     * @return 数据是否有改动
     */
    protected boolean afterSaved(final T data) {
        return false;
    }
}
