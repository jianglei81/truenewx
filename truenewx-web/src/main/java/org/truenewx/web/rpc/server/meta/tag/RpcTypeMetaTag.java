package org.truenewx.web.rpc.server.meta.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.web.rpc.server.meta.RpcTypeMeta;
import org.truenewx.web.tagext.DynamicAttributeTagSupport;

/**
 * RPC类型元数据标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcTypeMetaTag extends DynamicAttributeTagSupport {

    private static final long serialVersionUID = -173515157538791990L;

    private RpcTypeMeta value;
    private String emptyHtml = Strings.EMPTY;

    public void setValue(final RpcTypeMeta value) {
        this.value = value;
    }

    public void setEmptyHtml(final String emptyHtml) {
        this.emptyHtml = emptyHtml;
    }

    @Override
    public void doTag() throws JspException, IOException {
        printRpcTypeMeta(this.value);
    }

    private void printRpcTypeMeta(final RpcTypeMeta type) throws IOException {
        if (type != null) {
            String simpleName = type.getSimpleName();
            if (type.getType().isPrimitive()) { // 原生类型
                if (StringUtils.isBlank(simpleName)) {
                    print(this.emptyHtml);
                } else {
                    print(simpleName);
                }
            } else {
                final String fullName = type.isArray() ? type.getComponentType().getFullName()
                                : type.getFullName();
                print("<span title=\"", fullName, Strings.DOUBLE_QUOTES);
                if (type.isComplex()) {
                    print(" expandable=\"true\"");
                }
                if (type.getType().isEnum()) {
                    print(" enum=\"true\"");
                }
                print(joinDynamicAttributes());
                print(">");
                if (type.isArray()) {
                    simpleName = type.getComponentType().getSimpleName();
                }
                print(simpleName);
                print("</span>");
                if (type.isArray()) {
                    print(RpcTypeMeta.ARRAY_TYPE_SUFFIX);
                }
                if (!type.isArray() && type.getComponentType() != null) {
                    print("&lt;");
                    if (type.isMap()) { // Map的话先生成key
                        print(String.class.getSimpleName(), Strings.COMMA, Strings.SPACE);
                    }
                    printRpcTypeMeta(type.getComponentType());
                    print("&gt;");
                }
            }
        }
    }

}
