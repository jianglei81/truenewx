package org.truenewx.web.rpc.server.meta;

import java.util.Date;
import java.util.Map;

import org.springframework.util.Assert;
import org.truenewx.core.util.ClassUtil;

/**
 * RPC类型元数据
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcTypeMeta {

    public static final String ARRAY_TYPE_SUFFIX = "[]";

    private String caption;
    private Class<?> type;
    /**
     * 包含的属性集
     */
    private String[] includes;
    /**
     * 排除的属性集
     */
    private String[] excludes;
    private RpcTypeMeta componentType;

    public RpcTypeMeta(Class<?> type) {
        Assert.notNull(type, "type must be not null");
        if (Date.class.isAssignableFrom(type)) {
            this.type = Long.class;
        } else {
            this.type = type;
            if (this.type.isArray()) {
                this.componentType = new RpcTypeMeta(this.type.getComponentType());
            }
        }
    }

    public Class<?> getType() {
        return this.type;
    }

    public boolean isArray() {
        return this.type.isArray();
    }

    public boolean isIterable() {
        return Iterable.class.isAssignableFrom(this.type);
    }

    public boolean isMap() {
        return Map.class.isAssignableFrom(this.type);
    }

    public boolean isEnum() {
        return this.type.isEnum();
    }

    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String[] getIncludes() {
        return this.includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return this.excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public RpcTypeMeta getComponentType() {
        return this.componentType;
    }

    public void setComponentType(Class<?> componentType) {
        if (isIterable() || isMap()) { // 主类型为集合或Map时，元素类型才可设置
            this.componentType = new RpcTypeMeta(componentType);
        }
    }

    public String getFullName() {
        if (this.type.isArray()) {
            return this.type.getComponentType().getName() + ARRAY_TYPE_SUFFIX;
        }
        return this.type.getName();
    }

    public String getPackageName() {
        if (this.type == void.class) {
            return null;
        }
        Package pack;
        if (this.type.isArray()) {
            pack = this.type.getComponentType().getPackage();
        } else {
            pack = this.type.getPackage();
        }
        return pack == null ? null : pack.getName();
    }

    public String getSimpleName() {
        if (this.type == void.class) {
            return null;
        }
        if (this.type.isArray()) {
            return this.type.getComponentType().getSimpleName() + ARRAY_TYPE_SUFFIX;
        }
        return this.type.getSimpleName();
    }

    public boolean isComplex() {
        // JDK中的类型一律视为非复合类型
        Class<?> clazz = this.type.isArray() ? this.type.getComponentType() : this.type;
        Package pack = clazz.getPackage();
        if (pack != null && pack.getName().startsWith("java.")) {
            return false;
        }
        return ClassUtil.isComplex(clazz);
    }

}
