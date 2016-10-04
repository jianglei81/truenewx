package org.truenewx.core.exception;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 包含多个异常信息的多异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MultiException extends HandleableException implements Iterable<SingleException> {

    private static final long serialVersionUID = -6695356656790082971L;
    /**
     * 全局异常集
     */
    // 由于多异常包含的异常数一般很少，极少超过10个，故用简单的数据结构，对性能的影响不大
    private Set<SingleException> exceptions = new LinkedHashSet<>();

    /**
     * @param exceptions
     *            异常清单
     */
    public MultiException(final SingleException... exceptions) {
        add(exceptions);
    }

    /**
     * @param exceptions
     *            异常清单
     */
    public MultiException(final Iterable<? extends SingleException> exceptions) {
        for (final SingleException exception : exceptions) {
            this.exceptions.add(exception);
        }
    }

    /**
     * 添加指定异常集
     *
     * @param exceptions
     *            异常集
     * @return 当前多异常对象
     */
    public MultiException add(final SingleException... exceptions) {
        for (final SingleException exception : exceptions) {
            this.exceptions.add(exception);
        }
        return this;
    }

    /**
     * 将指定可处理异常合并至当前对象中
     *
     * @param exception
     *            可处理异常
     * @return 当前多异常对象
     */
    public MultiException merge(final HandleableException exception) {
        if (exception instanceof SingleException) {
            this.exceptions.add((SingleException) exception);
        } else if (exception instanceof MultiException) {
            final MultiException me = (MultiException) exception;
            for (final SingleException se : me.exceptions) {
                this.exceptions.add(se);
            }
        }
        return this;
    }

    @Override
    public Iterator<SingleException> iterator() {
        return this.exceptions.iterator();
    }

    /**
     * 判断是否包含绑定了指定属性的异常
     *
     * @param property
     *            属性
     * @return 是否包含绑定了指定属性的异常
     */
    public boolean containsPropertyException(final String property) {
        for (final SingleException se : this.exceptions) {
            if (se.matches(property)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取异常总数
     *
     * @return 异常总数
     */
    public int getTotal() {
        return this.exceptions.size();
    }

    public boolean isNotEmpty() {
        return this.exceptions.size() > 0;
    }

}
