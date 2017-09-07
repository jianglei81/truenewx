package org.truenewx.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * Bean工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class BeanUtil {
    /**
     * 检查指定的2个bean是否相等，只根据id属性进行判断，没有id属性或id属性值都为null，则直接用bean的原始equals方法进行比较
     *
     * @param bean
     *            比较的bean
     * @param otherBean
     *            另一个比较的bean
     * @return true if 指定的2个bean相等, otherwise false
     */
    public static boolean equalsById(final Object bean, final Object otherBean) {
        if (bean == null || otherBean == null) {
            throw new NullPointerException();
        }
        final Class<?> class1 = bean.getClass();
        final Class<?> class2 = otherBean.getClass();
        if (!class1.isAssignableFrom(class2) || !class2.isAssignableFrom(class1)) { // 比较的两个bean的类型必须相同
            return false;
        }
        final Long id1 = (Long) getPropertyValue(bean, "id");
        final Long id2 = (Long) getPropertyValue(otherBean, "id");
        if (id1 == null && id2 == null) {
            return bean.equals(otherBean);
        }
        return id1 != null && id2 != null && id1.equals(id2);
    }

    /**
     * 获取指定bean对象的指定属性值。若该bean不存在指定属性，则返回null。属性名支持形如a.name的复杂对象属性名
     *
     * @param bean
     *            bean对象
     * @param propertyName
     *            属性名称
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPropertyValue(final Object bean, final String propertyName) {
        try {
            final String[] names = propertyName.split("\\.");
            return (T) getRefPropertyValue(bean, names);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * 获取指定bean的指定逐级引用属性的值，如：bean.a.b.c
     *
     * @param bean
     *            对象
     * @param propertyNames
     *            逐级引用属性
     * @return 值
     * @throws IllegalAccessException
     *             如果有一个属性的获取方法不可访问
     * @throws InvocationTargetException
     *             如果有一个属性不存在
     * @throws NullPointerException
     *             如果中间有一个属性的值为null
     */
    private static Object getRefPropertyValue(final Object bean, final String... propertyNames)
            throws IllegalAccessException, InvocationTargetException, NullPointerException {
        Object value = bean;
        for (final String name : propertyNames) {
            final PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(value.getClass(), name);
            value = pd.getReadMethod().invoke(value);
        }
        return value;
    }

    /**
     * 设置指定bean对象的指定属性的值，忽略属性设置错误，不能设置则不设置
     *
     * @param bean
     *            对象
     * @param propertyName
     *            属性名
     * @param value
     *            属性值
     * @return 是否设置成功，当指定属性不存在或无法设置值时返回false，否则返回true
     */
    public static boolean setPropertyValue(@Nullable
    Object bean, String propertyName, @Nullable final Object value) {
        if (bean != null) {
            final String[] names = propertyName.split("\\.");
            if (names.length > 1) {
                try {
                    bean = getRefPropertyValue(bean,
                            ArrayUtils.subarray(names, 0, names.length - 1));
                    propertyName = names[names.length - 1];
                } catch (final Exception e) {
                    return false; // 忽略属性设置错误，不能设置则不设置
                }
            }
            final PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(bean.getClass(),
                    propertyName);
            if (pd != null) {
                final Method writeMethod = pd.getWriteMethod();
                if (writeMethod != null) {
                    try {
                        writeMethod.invoke(bean, value);
                        return true;
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                        return false; // 忽略属性设置错误，不能设置则不设置
                    }
                }
            }
        }
        return false;
    }

    /**
     * 直接设置指定bean的指定字段值
     *
     * @param bean
     *            bean
     * @param name
     *            字段名
     * @param value
     *            字段值
     */
    public static void setFieldValue(final Object bean, final String name, final Object value) {
        if (bean != null) {
            final Class<?> type = bean.getClass();
            try {
                final Field field = ClassUtil.findField(type, name);
                if (field != null) {
                    final boolean accessible = field.isAccessible();
                    if (!accessible) {
                        field.setAccessible(true);
                    }
                    field.set(bean, value);
                    if (!accessible) {
                        field.setAccessible(accessible);
                    }
                }
            } catch (final Exception e) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(final Object bean, final String name) {
        if (bean != null) {
            final Class<?> type = bean.getClass();
            try {
                final Field field = ClassUtil.findField(type, name);
                if (field != null) {
                    final boolean accessible = field.isAccessible();
                    if (!accessible) {
                        field.setAccessible(true);
                    }
                    final Object value = field.get(bean);
                    if (!accessible) {
                        field.setAccessible(accessible);
                    }
                    return (T) value;
                }
            } catch (final Exception e) {
            }
        }
        return null;
    }

    /**
     * 判断指定bean集合中是否包含指定bean，包含关系根据两个bean的id属性值是否相等进行判断
     *
     * @param collection
     *            bean集合
     * @param bean
     *            比较bean
     * @return true if 指定集合中包含指定bean, otherwise false
     */
    public static boolean containsById(final Collection<?> collection, final Object bean) {
        for (final Object o : collection) {
            if (equalsById(bean, o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除bean集合中的比较bean，如果指定bean集合中包含指定bean，则删除；
     *
     * @param collection
     *            bean集合
     * @param bean
     *            比较bean
     */
    public static void removeById(final Collection<?> collection, final Object bean) {
        for (final Object o : collection) {
            if (equalsById(bean, o)) {
                collection.remove(o);
                return;
            }
        }
    }

    /**
     * 获取指定bean集合中id属性值等于指定id的bean
     *
     * @param collection
     *            bean集合
     * @param id
     *            id属性值
     * @return true if 指定bean集合中id属性值等于指定id的bean, otherwise false
     */
    public static Object getById(final Collection<?> collection, final long id) {
        try {
            for (final Object o : collection) {
                final Long oid = (Long) getPropertyValue(o, "id");
                if (oid != null && oid.longValue() == id) {
                    return o;
                }
            }
            return null;
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * 将指定bean对象中的属性转换到指定Map中
     *
     * @param map
     *            Map
     * @param bean
     *            bean
     * @param excludedProperties
     *            排除的属性集
     */
    public static void fromBean(final Map<String, Object> map, final Object bean,
            final String... excludedProperties) {
        final PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(bean.getClass());
        for (final PropertyDescriptor pd : pds) {
            try {
                final String name = pd.getName();
                if (!"class".equals(name) && !ArrayUtils.contains(excludedProperties, name)) {
                    final Method readMethod = pd.getReadMethod();
                    if (readMethod != null) {
                        final Object value = readMethod.invoke(bean);
                        if (value != null) {
                            map.put(name, value);
                        }
                    }
                }
            } catch (final Exception e) { // 出现任何异常不做任何处理
            }
        }
    }

    /**
     * 将指定bean对象转换为Map，其条目关键字为bean对象的属性名，条目取值为该属性的值
     *
     * @param bean
     *            bean对象
     * @param excludedProperties
     *            排除的属性
     * @return Map结果对象
     */
    public static Map<String, Object> toMap(final Object bean, final String... excludedProperties) {
        final Map<String, Object> map = new HashMap<>();
        fromBean(map, bean, excludedProperties);
        return map;
    }

    /**
     * 从指定Map中取值设置到指定bean对象中
     *
     * @param bean
     *            bean对象
     * @param map
     *            Map
     * @param excludedKeys
     *            排除的关键字
     */
    public static void fromMap(final Object bean, final Map<String, Object> map,
            final String... excludedKeys) {
        for (final Entry<String, Object> entry : map.entrySet()) {
            try {
                final String key = entry.getKey();
                if (!"class".equals(key) && !ArrayUtils.contains(excludedKeys, key)) {
                    final Object value = entry.getValue();
                    final PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(bean.getClass(),
                            key);
                    if (pd != null) {
                        final Method writeMethod = pd.getWriteMethod();
                        if (writeMethod != null) {
                            writeMethod.invoke(bean, value);
                        }
                    }
                }
            } catch (final Exception e) { // 出现任何异常不做任何处理
            }
        }
    }

    /**
     * 用指定Map生成指定类型的bean对象
     *
     * @param map
     *            Map
     * @param clazz
     *            bean类型
     * @param excludedKeys
     *            排除的关键字
     * @return bean对象
     * @throws InstantiationException
     *             如果没有无参构造函数
     * @throws IllegalAccessException
     *             如果构造函数无法访问
     */
    public static <T> T toBean(final Map<String, Object> map, final Class<T> clazz,
            final String... excludedKeys) throws InstantiationException, IllegalAccessException {
        final T bean = clazz.newInstance();
        fromMap(bean, map, excludedKeys);
        return bean;
    }

    /**
     * 获取静态属性表达式所表示的静态属性值，静态属性表达式形如：@org.truenewx.core.util.
     * DateUtil@SHORT_DATE_PATTERN <br/>
     * 如果表达式错误或所表示的属性为非静态或不可访问 ，则返回null
     *
     * @param propertyExpression
     *            静态属性表达式
     * @return 静态属性值
     */
    public static Object getStaticPropertyExpressionValue(final String propertyExpression) {
        final String[] names = propertyExpression.split("@");
        if (names.length != 3 || names[0].length() > 0) {
            return null;
        }
        try {
            final Class<?> clazz = Class.forName(names[1]);
            final Field field = clazz.getField(names[2]);
            return field.get(null);
        } catch (final Exception e) {
        }
        return null;
    }

    /**
     * 判断指定的bean对象是否具有指定属性的写方法
     *
     * @param bean
     *            对象
     * @param propertyName
     *            属性名
     * @param propertyClass
     *            属性类型
     * @return true if 指定的bean对象具有指定属性的写方法, otherwise false
     */
    public static boolean hasWritableProperty(final Object bean, final String propertyName,
            final Class<?> propertyClass) {
        try {
            final String methodName = "set" + StringUtil.firstToUpperCase(propertyName);
            bean.getClass().getMethod(methodName, propertyClass);
            return true;
        } catch (final SecurityException e) {
        } catch (final NoSuchMethodException e) {
        }
        return false;
    }

    /**
     * 将指定源对象中的简单属性的值复制到指定目标对象中，如果目标对象中无相应属性则忽略。
     * 简单属性包括：原始类型，字符串，数字，日期，URI，URL，Locale
     *
     * @param source
     *            源对象
     * @param target
     *            目标对象
     */
    public static void copySimpleProperties(final Object source, final Object target) {
        final PropertyDescriptor[] propertyDescriptors = BeanUtils
                .getPropertyDescriptors(source.getClass());
        final Class<?> targetClass = target.getClass();
        for (final PropertyDescriptor pd : propertyDescriptors) {
            try {
                if (BeanUtils.isSimpleValueType(pd.getPropertyType())) {
                    final String name = pd.getDisplayName();
                    if (!"class".equals(name)) {
                        final PropertyDescriptor writePd = BeanUtils
                                .getPropertyDescriptor(targetClass, name);
                        if (writePd != null) {
                            final Method writeMethod = writePd.getWriteMethod();
                            if (writeMethod != null) {
                                final Object value = pd.getReadMethod().invoke(source);
                                writeMethod.invoke(target, value);
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                LoggerFactory.getLogger(BeanUtil.class).error(e.getMessage(), e);
            }
        }
    }
}
