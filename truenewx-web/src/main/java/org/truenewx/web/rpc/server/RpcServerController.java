package org.truenewx.web.rpc.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.web.exception.annotation.HandleableExceptionMessage;
import org.truenewx.web.rpc.serializer.RpcSerializer;

/**
 * 基于Spring MVC的RPC调用代理控制器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/rpc")
public class RpcServerController {

    @Autowired
    private RpcServer server;
    @Autowired
    private RpcSerializer serializer;

    @RequestMapping(value = "/methods/{beanIdString}", method = RequestMethod.GET)
    @HandleableExceptionMessage
    @ResponseBody
    public String methodNames(@PathVariable("beanIdString") String beanIdString) {
        if (beanIdString.contains(Strings.COMMA)) { // 带逗号的beanId分割后作为多个beanId处理
            String[] beanIds = beanIdString.split(Strings.COMMA);
            Map<String, Collection<String>> methodNameMap = new HashMap<>();
            for (String beanId : beanIds) {
                Collection<String> methodNames = this.server.methods(beanId);
                methodNameMap.put(beanId, methodNames);
            }
            return this.serializer.serialize(methodNameMap);
        } else {
            Collection<String> methodNames = this.server.methods(beanIdString);
            return this.serializer.serialize(methodNames);
        }
    }

    @RequestMapping(value = "/invoke/{beanId}/{methodName}", method = { RequestMethod.POST,
            RequestMethod.GET })
    @HandleableExceptionMessage
    @ResponseBody
    public String invoke(@PathVariable("beanId") String beanId,
            @PathVariable("methodName") String methodName,
            @RequestParam(value = "args", required = false) String argString,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Timestamp-Before", String.valueOf(System.currentTimeMillis()));
        try {
            RpcInvokeResult result = this.server.invoke(beanId, methodName, argString, request,
                    response);
            response.setHeader("Timestamp-After", String.valueOf(System.currentTimeMillis()));
            return this.serializer.serializeBean(result.getValue(), result.getFilters());
        } catch (HandleableException e) {
            throw e;
        } catch (Throwable e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(response.getWriter());
            return null;
        }
    }

}
