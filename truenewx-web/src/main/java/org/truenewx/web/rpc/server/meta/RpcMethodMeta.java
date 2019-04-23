package org.truenewx.web.rpc.server.meta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.truenewx.core.util.ArrayUtil;
import org.truenewx.web.rpc.server.annotation.RpcArg;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResult;
import org.truenewx.web.security.annotation.Accessibility;

/**
 * RPC方法元数据
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcMethodMeta implements Comparable<RpcMethodMeta> {

    private Method method;
    private List<RpcVariableMeta> argMetas;

    public RpcMethodMeta(Method method) {
        Assert.notNull(method, "method must be not null");
        this.method = method;
    }

    public Method getMethod() {
        return this.method;
    }

    public String getName() {
        return this.method.getName();
    }

    @Override
    public int compareTo(RpcMethodMeta other) {
        return this.method.getName().compareTo(other.method.getName());
    }

    public List<RpcVariableMeta> getArgMetas() {
        if (this.argMetas == null) {
            synchronized (this.method) {
                if (this.argMetas == null) {
                    RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
                    if (rpcMethod != null) {
                        this.argMetas = new ArrayList<>();
                        Class<?>[] argTypes = this.method.getParameterTypes();
                        RpcArg[] rpcArgs = rpcMethod.args();
                        for (int i = 0; i < argTypes.length; i++) {
                            RpcVariableMeta argMeta = new RpcVariableMeta(argTypes[i]);
                            RpcArg rpcArg = ArrayUtil.get(rpcArgs, i);
                            if (rpcArg != null) {
                                argMeta.setName(rpcArg.name());
                                argMeta.setCaption(rpcArg.caption());
                                RpcTypeMeta argTypeMeta = argMeta.getType();
                                argTypeMeta.setIncludes(rpcArg.includes());
                                argTypeMeta.setExcludes(rpcArg.excludes());
                                argTypeMeta.setComponentType(rpcArg.componentType());
                            }
                            this.argMetas.add(argMeta);
                        }
                    }
                }
            }
        }
        return this.argMetas;
    }

    public RpcTypeMeta getResultType() {
        RpcTypeMeta typeMeta = new RpcTypeMeta(this.method.getReturnType());
        RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
        if (rpcMethod != null) {
            RpcResult rpcResult = rpcMethod.result();
            typeMeta.setComponentType(rpcResult.componentType());
            typeMeta.setCaption(rpcResult.caption());
        }
        return typeMeta;
    }

    public boolean isAnonymous() {
        Accessibility accessibility = this.method.getAnnotation(Accessibility.class);
        return accessibility != null && accessibility.anonymous();
    }

    public boolean isLan() {
        Accessibility accessibility = this.method.getAnnotation(Accessibility.class);
        return accessibility != null && accessibility.lan();
    }

    public String getCaption() {
        RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
        if (rpcMethod != null) {
            return rpcMethod.caption();
        }
        return null;
    }

    /**
     *
     * @return 是否已不推荐使用
     */
    public boolean isDeprecated() {
        return this.method.getAnnotation(Deprecated.class) != null;
    }

    public String getVersion() {
        RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
        if (rpcMethod != null) {
            return rpcMethod.version();
        }
        return null;
    }

    @Override
    public int hashCode() {
        return this.method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RpcMethodMeta other = (RpcMethodMeta) obj;
        return this.method.equals(other.method);
    }

}
