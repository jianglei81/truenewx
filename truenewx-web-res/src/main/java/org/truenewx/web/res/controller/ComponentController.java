package org.truenewx.web.res.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 组件
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/component")
public class ComponentController {

    @RequestMapping(value = "/basic", method = RequestMethod.GET)
    public String basic() {
        return "/component/basic/index";
    }

    @RequestMapping(value = "/bs2", method = RequestMethod.GET)
    public String bs2() {
        return "/component/bs2/index";
    }

    @RequestMapping(value = "/bs3", method = RequestMethod.GET)
    public String bs3() {
        return "/component/bs3/index";
    }

}
