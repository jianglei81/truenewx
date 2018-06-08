package org.truenewx.web.res.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.truenewx.web.security.annotation.Accessibility;

/**
 * 组件
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/component")
public class ComponentController {

    @Accessibility(lan = true)
    @RequestMapping(value = "/basic", method = RequestMethod.GET)
    public String basic() {
        return "/component/basic/index";
    }

    @Accessibility(lan = true)
    @RequestMapping(value = "/bs2", method = RequestMethod.GET)
    public String bs2() {
        return "/component/bs2/index";
    }

    @Accessibility(lan = true)
    @RequestMapping(value = "/bs3", method = RequestMethod.GET)
    public String bs3() {
        return "/component/bs3/index";
    }

}
