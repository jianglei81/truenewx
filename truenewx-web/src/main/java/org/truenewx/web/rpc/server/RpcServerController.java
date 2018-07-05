package org.truenewx.web.rpc.server;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping(value = "/methods/{beanId}", method = RequestMethod.GET)
    @HandleableExceptionMessage
    @ResponseBody
    public String methodNames(@PathVariable("beanId") String beanId) throws Exception {
        Collection<String> methodNames = this.server.methods(beanId);
        return this.serializer.serialize(methodNames);
    }

    @RequestMapping(value = "/invoke/{beanId}/{methodName}",
            method = { RequestMethod.POST, RequestMethod.GET })
    @HandleableExceptionMessage
    @ResponseBody
    public String invoke(@PathVariable("beanId") String beanId,
            @PathVariable("methodName") String methodName,
            @RequestParam(value = "args", required = false) String argString,
            HttpServletRequest request, HttpServletResponse response) throws Throwable {
        response.setHeader("Timestamp-Before", String.valueOf(System.currentTimeMillis()));
        RpcInvokeResult result = this.server.invoke(beanId, methodName, argString, request,
                response);
        response.setHeader("Timestamp-After", String.valueOf(System.currentTimeMillis()));
        return this.serializer.serializeBean(result.getValue(), result.getFilters());
    }

}
