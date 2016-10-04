package org.truenewx.web.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.truenewx.core.util.BeanUtil;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.web.tagext.SimpleDynamicAttributeTagSupport;

/**
 * 将值转换为JSON字符串的标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ToJsonTag extends SimpleDynamicAttributeTagSupport {
    private Object value;
    private boolean toSingleQuote = true;
    private Map<String, Object> defaultValues;
    private Map<String, Object> extendValues;

    public void setValue(final Object value) {
        this.value = value;
    }

    public void setToSingleQuote(final boolean toSingleQuote) {
        this.toSingleQuote = toSingleQuote;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parse(final Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        } else if (value instanceof String) {
            return JsonUtil.json2Map((String) value);
        } else if (value != null && ClassUtil.isComplex(value.getClass())) {
            return BeanUtil.toMap(value);
        }
        return null;
    }

    public void setDefault(final Object defaultValue) {
        this.defaultValues = parse(defaultValue);
    }

    public void setExtend(final Object extend) {
        this.extendValues = parse(extend);
    }

    @Override
    public void doTag() throws JspException, IOException {
        if ((this.defaultValues == null || this.defaultValues.isEmpty())
                        && (this.extendValues == null || this.extendValues.isEmpty())) { // 无默认值和扩展值，则仅序列化取值
            if (this.value != null) {
                try {
                    String json = JsonUtil.toJson(this.value);
                    if (this.toSingleQuote) {
                        // 转换双引号为单引号，使在页面字符串中不与双引号冲突
                        json = json.replace('\"', '\'');
                    }
                    print(json);
                } catch (final Exception e) {
                    e.printStackTrace();
                    // 出现异常不打印任何字符
                }
            }
        } else { // 有默认值或扩展值，则先后叠加后再序列化
            final Map<String, Object> map = new HashMap<>();
            if (this.defaultValues != null) {
                map.putAll(this.defaultValues);
            }
            if (this.value != null) {
                final Map<String, Object> values = parse(this.value);
                if (values != null) {
                    map.putAll(values);
                }
            }
            if (this.extendValues != null) {
                map.putAll(this.extendValues);
            }
            try {
                String json = JsonUtil.map2Json(map);
                if (this.toSingleQuote) {
                    // 转换双引号为单引号，使在页面字符串中不与双引号冲突
                    json = json.replace('\"', '\'');
                }
                print(json);
            } catch (final Exception e) {
                e.printStackTrace();
                // 出现异常不打印任何字符
            }
        }
    }

}
