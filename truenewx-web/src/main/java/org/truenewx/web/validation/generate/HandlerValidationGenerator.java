package org.truenewx.web.validation.generate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;
import org.truenewx.data.model.Model;

/**
 * 处理器校验生成器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public interface HandlerValidationGenerator {
    /**
     * 为的多个模型类型生成校验规则至指定模型视图中
     * 
     * @param request
     *            HTTP请求
     * @param modelClasses
     *            模型类型集
     * @param mav
     *            模型视图
     * @author jianglei
     */
    void generate(HttpServletRequest request, Class<? extends Model>[] modelClasses,
                    ModelAndView mav);

}
