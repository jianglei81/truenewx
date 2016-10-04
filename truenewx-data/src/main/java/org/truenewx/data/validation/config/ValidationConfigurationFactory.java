package org.truenewx.data.validation.config;

import org.truenewx.data.model.Model;

/**
 * 校验配置工厂
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public interface ValidationConfigurationFactory {
    /**
     * 获取指定模型类的校验配置
     * 
     * @param modelClass
     *            模型类
     * @return 指定模型类的校验配置
     */
    ValidationConfiguration getConfiguration(Class<? extends Model> modelClass);
}
