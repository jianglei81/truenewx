package org.truenewx.web.security.mgt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.web.security.subject.Subject;

/**
 * Subject管理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SubjectManager {

    Subject getSubject(HttpServletRequest request, HttpServletResponse response);

    Subject getSubject(HttpServletRequest request, HttpServletResponse response,
            Class<?> userClass);

}
