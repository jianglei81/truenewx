package org.truenewx.web.res.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.truenewx.web.res.model.ValidationImage;
import org.truenewx.web.security.annotation.Accessibility;
import org.truenewx.web.validation.generate.annotation.ValidationGeneratable;

/**
 * 字段校验测试用控制器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/component/validate")
public class ValidateController {

    @Accessibility(lan = true)
    @RequestMapping(method = RequestMethod.GET)
    @ValidationGeneratable(ValidationImage.class)
    public String get() {
        return "/component/validate";
    }

    @Accessibility(lan = true)
    @RequestMapping(method = RequestMethod.POST)
    public String post() {
        return "/component/validate";
    }
}
