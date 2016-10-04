package org.truenewx.core.serializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;

/**
 * JSON字符串序列化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JsonSerializer implements StringSerializer {

    public JsonSerializer() {
        final JsonDateCodec dateCodec = new JsonDateCodec();
        SerializeConfig.getGlobalInstance().put(Date.class, dateCodec);
        SerializeConfig.getGlobalInstance().put(java.sql.Date.class, dateCodec);
        SerializeConfig.getGlobalInstance().put(Timestamp.class, dateCodec);
        ParserConfig.getGlobalInstance().putDeserializer(Date.class, dateCodec);
        ParserConfig.getGlobalInstance().putDeserializer(java.sql.Date.class, dateCodec);
        ParserConfig.getGlobalInstance().putDeserializer(Timestamp.class, dateCodec);
    }

    @Override
    public String serializeArray(final Object[] array) {
        return JSON.toJSONString(array);
    }

    @Override
    public String serializeBean(final Object bean) {
        return JSON.toJSONString(bean);
    }

    @Override
    public String serializeCollection(final Collection<?> collection) {
        return JSON.toJSONString(collection);
    }

    @Override
    public Object[] deserializeArray(final String s, final Type... elementTypes) {
        final List<Object> list;
        if (elementTypes != null && elementTypes.length > 0) {
            list = JSON.parseArray(s, elementTypes);
        } else {
            list = JSON.parseArray(s);
        }
        if (list != null) {
            return list.toArray(new Object[list.size()]);
        }
        return new Object[0];
    }

    @Override
    public <T> T deserializeBean(final String s, final Class<T> type) {
        return JSON.parseObject(s, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> deserializeList(final String s, final Class<T> elementType) {
        final List<T> list = JSON.parseArray(s, elementType);
        if (list != null) {
            // 类型反序列化时需针对void进行特殊处理，这是fastjson的一个bug："void"被解析为null
            if (elementType == Class.class && s.contains("\"void\"") && s.indexOf("null") < 0) { // 包含void但不包含null时才处理
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == null) { // 反序列化为null的视为void类型
                        list.set(i, (T) void.class);
                    }
                }
            }
        }
        return list;
    }

}
