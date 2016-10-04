package org.truenewx.core.tuple;

import org.truenewx.core.functor.impl.FuncHashCode;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * 对称二元体，其左右元顺序不敏感，(a,b)等同于(b,a)
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            元素类型
 */
public class SymplexBinary<T> extends Binary<T, T> {

    /**
     * @param left
     *            左元
     * @param right
     *            右元
     */
    public SymplexBinary(final T left, final T right) {
        super(left, right);
    }

    /**
     * 反转左右元
     * 
     * @author jianglei
     */
    public void reverse() {
        final T left = getLeft();
        setLeft(getRight());
        setRight(left);
    }

    @Override
    public int hashCode() {
        return FuncHashCode.INSTANCE.apply(getLeft()) * FuncHashCode.INSTANCE.apply(getRight());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SymplexBinary<T> other = (SymplexBinary<T>) obj;
        return (PredEqual.INSTANCE.apply(getLeft(), other.getLeft())
                        && PredEqual.INSTANCE.apply(getRight(), other.getRight()))
                        || (PredEqual.INSTANCE.apply(getLeft(), other.getRight())
                                        && PredEqual.INSTANCE.apply(getRight(), other.getLeft()));
    }

    @Override
    public SymplexBinary<T> clone() {
        return new SymplexBinary<>(getLeft(), getRight());
    }

}
