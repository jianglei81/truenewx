package org.truenewx.web.rpc.server.meta;

import java.util.Collection;
import java.util.Map;

import org.truenewx.core.enums.support.EnumItem;

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
    private Map<String, Object> validation;
    private Collection<EnumItem> items;

    public RpcVariableMeta(Class<?> type) {
        this.type = new RpcTypeMeta(type);
    }

    public RpcTypeMeta getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return this.caption == null ? this.type.getCaption() : this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Map<String, Object> getValidation() {
        return this.validation;
    }

    public void setValidation(Map<String, Object> validation) {
        this.validation = validation;
    }

    public Collection<EnumItem> getItems() {
        return this.items;
    }

    public void setItems(Collection<EnumItem> items) {
        this.items = items;
    }

}
