package org.truenewx.web.res.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 首页
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("/index", "showMenu", Boolean.FALSE);
    }

    @RequestMapping(value = "/common", method = RequestMethod.GET)
    public String common() {
        return "/index-common";
    }

    @RequestMapping(value = "/bs2", method = RequestMethod.GET)
    public String bs2() {
        return "/index-bs2";
    }

    @RequestMapping(value = "/bs3", method = RequestMethod.GET)
    public String bs3() {
        return "/index-bs3";
    }

}
