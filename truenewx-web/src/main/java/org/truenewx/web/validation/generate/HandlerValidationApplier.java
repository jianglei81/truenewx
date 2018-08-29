package org.truenewx.web.validation.generate;

import java.util.Locale;

import org.springframework.web.servlet.ModelAndView;
import org.truenewx.data.model.Model;

/**
 * 处理器校验规则填充者
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface HandlerValidationApplier {
    /**
     * 为多个模型类型生成校验规则，并填充至指定模型视图中
     *
     * @param mav
     *            模型视图
     * @param modelClasses
     *            模型类型集
     * @param locale
     *            区域
     */
    void applyValidation(ModelAndView mav, Class<? extends Model>[] modelClasses, Locale locale);

}
