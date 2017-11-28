package org.truenewx.web.res.controller;

import java.util.List;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.web.res.model.Image;
import org.truenewx.web.rpc.server.annotation.RpcArg;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.security.annotation.Accessibility;

/**
 * TestController
 *
 * @author jianglei
 * @since JDK 1.8
 */
@RpcController
public class TestController {

    @RpcMethod
    @Accessibility(anonymous = true)
    public String append(final String s, final String other) throws BusinessException {
        final StringBuffer sb = new StringBuffer(s);
        if ("be".equals(other)) {
            throw new BusinessException("some_business_exception");
        }
        sb.append(Integer.parseInt(other));
        return sb.toString();
    }

    @RpcMethod
    @Accessibility(anonymous = true)
    public String appendArray(final String s, final String[] others) throws BusinessException {
        final StringBuffer sb = new StringBuffer(s);
        for (final String other : others) {
            if ("be".equals(other)) {
                throw new BusinessException("some_business_exception");
            }
            sb.append(Integer.parseInt(other));
        }
        return sb.toString();
    }

    @RpcMethod
    @Accessibility(anonymous = true)
    public String appendList(final String s, final List<String> others) throws BusinessException {
        final StringBuffer sb = new StringBuffer(s);
        for (final String other : others) {
            if ("be".equals(other)) {
                throw new BusinessException("some_business_exception");
            }
            sb.append(Integer.parseInt(other));
        }
        return sb.toString();
    }

    @RpcMethod
    @Accessibility(anonymous = true)
    public String appendSyntheticArray(final String s, final Image[] images)
            throws BusinessException {
        final StringBuffer sb = new StringBuffer(s);
        for (final Image image : images) {
            final String extension = image.getExtension();
            if ("be".equals(extension)) {
                throw new BusinessException("some_business_exception");
            }
            sb.append(extension);
        }
        return sb.toString();
    }

    @RpcMethod(
            args = { @RpcArg(name = "s"), @RpcArg(name = "images", componentType = Image.class) })
    @Accessibility(anonymous = true)
    public String appendSyntheticList(final String s, final List<Image> images)
            throws BusinessException {
        final StringBuffer sb = new StringBuffer(s);
        for (final Image image : images) {
            final String extension = image.getExtension();
            if ("be".equals(extension)) {
                throw new BusinessException("some_business_exception");
            }
            sb.append(extension);
        }
        return sb.toString();
    }

    @RpcMethod
    @Accessibility(anonymous = true)
    public String[] split(final String s, final String regex) {
        return s.split(regex);
    }

}
