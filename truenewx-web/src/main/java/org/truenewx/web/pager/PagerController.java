package org.truenewx.web.pager;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.web.pager.functor.AlgoPagerOutput;
import org.truenewx.web.spring.servlet.mvc.SimpleController;

@Controller
public class PagerController extends SimpleController {

    @RequestMapping(value = "/pager")
    public ModelAndView index(final HttpServletRequest request,
                    final HttpServletResponse response) {
        try {
            final Map<String, Object> params = new HashMap<>();
            final Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                params.put(name, request.getParameter(name));
            }
            AlgoPagerOutput.visit(request, response.getWriter(), params);
            return null;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
