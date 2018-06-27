package org.truenewx.web.rpc.server.meta;

/**
 * RPC变量（包括方法参数和类的属性）元数据
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcVariableMeta {

    private RpcTypeMeta type;
    private String name;
    private String caption;

    public RpcVariableMeta(final Class<?> type) {
        this.type = new RpcTypeMeta(type);
    }

    public RpcTypeMeta getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCaption() {
        return this.caption == null ? this.type.getCaption() : this.caption;
    }

    public void setCaption(final String caption) {
        this.caption = caption;
    }

}
