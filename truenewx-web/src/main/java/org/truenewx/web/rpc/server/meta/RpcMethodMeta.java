package org.truenewx.web.rpc.server.meta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.truenewx.core.util.ArrayUtil;
import org.truenewx.web.rpc.server.annotation.RpcArg;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResult;

/**
 * RPC方法元数据
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcMethodMeta implements Comparable<RpcMethodMeta> {

    private Method method;
    private List<RpcVariableMeta> argMetas;

    public RpcMethodMeta(final Method method) {
        Assert.notNull(method);
        this.method = method;
    }

    public Method getMethod() {
        return this.method;
    }

    public String getName() {
        return this.method.getName();
    }

    @Override
    public int compareTo(final RpcMethodMeta other) {
        return this.method.getName().compareTo(other.method.getName());
    }

    public List<RpcVariableMeta> getArgMetas() {
        if (this.argMetas == null) {
            synchronized (this.method) {
                if (this.argMetas == null) {
                    final RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
                    if (rpcMethod != null) {
                        this.argMetas = new ArrayList<>();
                        final Class<?>[] argTypes = this.method.getParameterTypes();
                        final RpcArg[] rpcArgs = rpcMethod.args();
                        for (int i = 0; i < argTypes.length; i++) {
                            final RpcVariableMeta argMeta = new RpcVariableMeta(argTypes[i]);
                            final RpcArg rpcArg = ArrayUtil.get(rpcArgs, i);
                            if (rpcArg != null) {
                                argMeta.setName(rpcArg.name());
                                argMeta.setCaption(rpcArg.caption());
                                final RpcTypeMeta argTypeMeta = argMeta.getType();
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

    public RpcTypeMeta getReturnType() {
        final RpcTypeMeta returnTypeMeta = new RpcTypeMeta(this.method.getReturnType());
        final RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
        if (rpcMethod != null) {
            final RpcResult rpcResult = rpcMethod.result();
            returnTypeMeta.setComponentType(rpcResult.componentType());
            returnTypeMeta.setCaption(rpcResult.caption());
        }
        return returnTypeMeta;
    }

    public boolean isLogined() {
        final RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
        return rpcMethod == null ? false : rpcMethod.logined();
    }

    public boolean isLan() {
        final RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
        return rpcMethod == null ? false : rpcMethod.lan();
    }

    public String getCaption() {
        final RpcMethod rpcMethod = this.method.getAnnotation(RpcMethod.class);
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

}
