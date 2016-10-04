package org.truenewx.web.tag;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.servlet.jsp.JspException;

import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.StringUtil;
import org.truenewx.web.tagext.SimpleDynamicAttributeTagSupport;

/**
 * 对象方法调用标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class InvokeTag extends SimpleDynamicAttributeTagSupport {

    private Object object;
    private String methodName;
    private Object arg;

    public void setObject(final Object object) {
        this.object = object;
    }

    public void setMethod(final String method) {
        this.methodName = method;
    }

    public void setArg(final Object arg) {
        this.arg = arg;
    }

    @Override
    public void doTag() throws JspException, IOException {
        final Class<? extends Object> type = this.object.getClass();
        final Collection<Method> methods = ClassUtil.findPublicMethods(type, this.methodName, -1);
        if (methods.isEmpty()) {
            throw new RuntimeException(
                            new NoSuchMethodException(type.getName() + "." + this.methodName));
        }
        if (methods.size() > 1) {
            throw new RuntimeException(type.getName() + " has " + methods.size() + "methods: "
                            + this.methodName);
        }
        final Method method = methods.iterator().next();
        final Object[] args = getArgs(method.getParameterTypes());
        try {
            final Object result = method.invoke(this.object, args);
            if (result != null) {
                print(result);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] getArgs(final Class<?>[] argTypes) {
        final Object[] args = new Object[argTypes.length];
        args[0] = this.arg;
        for (int i = 1; i < args.length; i++) {
            Object arg = this.attributes.get("arg" + i);
            if (arg instanceof String) {
                arg = StringUtil.parse((String) arg, argTypes[i]);
            }
            args[i] = arg;
        }
        return args;
    }
}
