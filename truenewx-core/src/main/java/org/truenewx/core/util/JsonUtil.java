package org.truenewx.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.truenewx.core.util.json.MultiPropertyPreFilter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * JSON工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JsonUtil {

    /**
     * 将JSON标准形式的字符串转换为对象
     *
     * @param json
     *            JSON标准形式的字符串
     * @return 转换形成的对象
     */
    public static Object json2Bean(final String json) {
        return JSON.parseObject(json);
    }

    /**
     * 将JSON标准形式的字符串转换为指定类型的对象
     *
     * @param json
     *            JSON标准形式的字符串
     * @param clazz
     *            要转换的类型
     * @return 转换形成的对象
     */
    public static <T> T json2Bean(final String json, final Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 将JSON标准形式的字符串转换生成的对象的所有属性值覆盖指定对象的相应属性值
     *
     * @param json
     *            JSON标准形式的字符串
     * @param bean
     *            要覆盖的对象
     */
    public static void jsonCoverBean(final String json, final Object bean) {
        final JSONObject jsonObj = JSON.parseObject(json);
        for (final Map.Entry<String, Object> entry : jsonObj.entrySet()) {
            try {
                BeanUtils.setProperty(bean, entry.getKey(), entry.getValue());
            } catch (final IllegalAccessException e) {
            } catch (final InvocationTargetException e) {
            } // 出现异常则忽略该属性的覆盖操作
        }
    }

    /**
     * 将bean转换为JSON标准格式的字符串
     *
     * @param bean
     *            需转换的bean
     * @return JSON格式的字符串
     */
    public static String bean2Json(final Object bean) {
        return JSON.toJSONString(bean);
    }

    /**
     * 将bean转换为JSON标准格式的字符串
     *
     * @param bean
     *            需转换的bean
     * @param clazz
     *            需进行排除的类
     * @param excludeProperties
     *            需排除的属性
     * @return JSON格式的字符串
     */
    public static String bean2Json(final Object bean, final Class<?> clazz,
                    final String... excludeProperties) {
        return JSON.toJSONString(bean, getFilterInstance(clazz, excludeProperties));
    }

    /**
     * 获取JSON过滤器实例
     *
     * @param clazz
     *            需进行属性排除的类
     * @param excludeProperties
     *            需排除的属性
     * @return JSON过滤器实例
     */
    private static PropertyPreFilter getFilterInstance(final Class<?> clazz,
                    final String... excludeProperties) {
        final SimplePropertyPreFilter filter = new SimplePropertyPreFilter(clazz);
        final Set<String> excludeList = filter.getExcludes();
        for (final String exclude : excludeProperties) {
            excludeList.add(exclude);
        }
        return filter;
    }

    /**
     * 将bean转换为JSON标准格式的字符串
     *
     * @param bean
     *            需转换的bean
     * @param filteredPropertiesMap
     *            过滤属性映射集
     * @return JSON格式的字符串
     */
    public static String bean2Json(final Object bean,
                    final Map<Class<?>, FilteredTokens> filteredPropertiesMap) {
        return JSON.toJSONString(bean, getFilterInstance(filteredPropertiesMap));
    }

    /**
     * 获取JSON过滤器实例
     *
     * @param filteredPropertiesMap
     *            过滤属性映射集
     * @return JSON过滤器实例
     */
    private static PropertyPreFilter getFilterInstance(
                    final Map<Class<?>, FilteredTokens> filteredPropertiesMap) {
        final MultiPropertyPreFilter filter = new MultiPropertyPreFilter();
        for (final Entry<Class<?>, FilteredTokens> entry : filteredPropertiesMap.entrySet()) {
            final FilteredTokens value = entry.getValue();
            filter.addFilteredProperties(entry.getKey(), value.getIncludes(), value.getExcludes());
        }
        return filter;
    }

    /**
     * 将Map转换为JSON标准格式的字符串
     *
     * @param map
     *            需转换的Map
     * @return JSON格式的字符串
     */
    public static String map2Json(final Map<String, Object> map) {
        final JSONObject jsonObj = new JSONObject(map);
        return jsonObj.toJSONString();
    }

    /**
     * 将Map转换为JSON标准格式的字符串
     *
     * @param map
     *            需转换的Map
     * @param clazz
     *            需进行属性排除的类
     * @param excludeProperties
     *            需排除的属性
     * @return JSON格式的字符串
     */
    public static String map2Json(final Map<String, Object> map, final Class<?> clazz,
                    final String... excludeProperties) {
        final JSONObject jsonObj = new JSONObject(map);
        return JSON.toJSONString(jsonObj, getFilterInstance(clazz, excludeProperties));
    }

    /**
     * 将Map转换为JSON标准格式的字符串
     *
     * @param map
     *            需转换的Map
     * @param filteredPropertiesMap
     *            过滤属性映射集
     * @return JSON格式的字符串
     */
    public static String map2Json(final Map<String, Object> map,
                    final Map<Class<?>, FilteredTokens> filteredPropertiesMap) {
        final JSONObject jsonObj = new JSONObject(map);
        return JSON.toJSONString(jsonObj, getFilterInstance(filteredPropertiesMap));
    }

    /**
     * 将指定任意类型的对象转换为JSON标准格式的字符串
     *
     * @param obj
     *            对象
     * @param clazz
     *            需进行属性排除的类
     * @param excludeProperties
     *            需排除的属性
     * @return JSON格式的字符串
     */
    @SuppressWarnings("unchecked")
    public static String toJson(final Object obj, final Class<?> clazz,
                    final String... excludeProperties) {
        if (obj instanceof Map) {
            return map2Json((Map<String, Object>) obj, clazz, excludeProperties);
        }
        return bean2Json(obj, clazz, excludeProperties);
    }

    /**
     * 将指定任意类型的对象转换为JSON标准格式的字符串
     *
     * @param obj
     *            对象
     * @param filteredPropertiesMap
     *            过滤属性映射集
     * @return JSON格式的字符串
     */
    @SuppressWarnings("unchecked")
    public static String toJson(final Object obj,
                    final Map<Class<?>, FilteredTokens> filteredPropertiesMap) {
        if (obj instanceof Map) {
            return map2Json((Map<String, Object>) obj, filteredPropertiesMap);
        }
        return bean2Json(obj, filteredPropertiesMap);
    }

    /**
     * 将指定任意类型的对象转换为JSON标准格式的字符串
     *
     * @param obj
     *            对象
     * @return JSON格式的字符串
     */
    @SuppressWarnings("unchecked")
    public static String toJson(final Object obj) {
        if (obj instanceof Map) {
            return map2Json((Map<String, Object>) obj);
        }
        return bean2Json(obj);
    }

    /**
     * 将JSON标准形式的字符串转换为具体类型不确定的List
     *
     * @param json
     *            JSON标准形式的字符串
     * @return 转换形成的对象List
     */
    public static List<Object> json2List(final String json) {
        return JSON.parseArray(json);
    }

    /**
     * 将JSON标准形式的字符串转换为指定类型的对象List
     *
     * @param json
     *            JSON标准形式的字符串
     * @param clazz
     *            元素类型
     * @return 转换形成的对象List
     */
    public static <T> List<T> json2List(final String json, final Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * 将JSON标准形式的字符串转换为Map
     *
     * @param json
     *            JSON标准形式的字符串
     * @return 转换形成的Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(final String json) {
        return JSON.parseObject(json, LinkedHashMap.class);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> json2Map(final String json, final Class<K> keyClass,
                    final Class<V> valueClass) {
        final Map<K, V> result = new HashMap<>();
        final Map<String, Object> map = JSON.parseObject(json);
        for (final Entry<String, Object> entry : map.entrySet()) {
            final K key;
            if (keyClass != String.class) {
                key = JSON.parseObject(entry.getKey(), keyClass);
            } else {
                key = (K) entry.getKey();
            }
            final V value = (V) entry.getValue();
            result.put(key, value);
        }
        return result;
    }

    /**
     * 将JSON标准形式的字符串转换为Properties
     *
     * @param json
     *            JSON标准形式的字符串
     * @return 转换形成的Properties
     */
    public static Properties json2Properties(final String json) {
        return JSON.parseObject(json, Properties.class);
    }

    /**
     * 将JSON标准形式的字符串转换为数组
     *
     * @param json
     *            JSON标准形式的字符串
     * @return 转换形成的数组
     */
    public static Object[] json2Array(final String json) {
        final JSONArray array = JSON.parseArray(json);
        return array == null ? null : array.toArray(new Object[array.size()]);
    }

    /**
     * 将JSON标准形式的字符串转换为数组
     *
     * @param json
     *            JSON标准形式的字符串
     * @param clazz
     *            元素类型
     * @return 转换形成的数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] json2Array(final String json, final Class<T> clazz) {
        final List<T> list = JSON.parseArray(json, clazz);
        final T[] array = (T[]) Array.newInstance(clazz, list.size());
        int i = 0;
        for (final T obj : list) {
            array[i++] = obj;
        }
        return array;
    }

}
