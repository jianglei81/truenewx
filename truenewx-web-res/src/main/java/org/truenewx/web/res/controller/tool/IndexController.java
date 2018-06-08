package org.truenewx.web.res.controller.tool;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.truenewx.web.security.annotation.Accessibility;

/**
 * 工具
 *
 * @author jianglei
 *
 */
@Controller
@RequestMapping("/tool")
public class IndexController {

    @Accessibility(lan = true)
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "/tool/index";
    }

}
