package org.truenewx.core.tuple;

import java.util.Objects;

/**
 * 三元体
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <L>
 *            左元类型
 * @param <M>
 *            中元类型
 * @param <R>
 *            右元类型
 * @see Triple
 */
public class Triplet<L, M, R> extends Binary<L, R> implements Triple<L, M, R> {
    private M middle;

    public Triplet(final L left, final M middle, final R right) {
        super(left, right);
        this.middle = middle;
    }

    @Override
    public M getMiddle() {
        return this.middle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int code = super.hashCode();
        code += code * prime + (this.middle == null ? 0 : this.middle.hashCode());
        return code;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Triple)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final Triple<L, M, R> other = (Triple<L, M, R>) obj;
        return Objects.equals(getLeft(), other.getLeft())
                        && Objects.equals(this.middle, other.getMiddle())
                        && Objects.equals(getRight(), other.getRight());
    }

    @Override
    public Triplet<L, M, R> clone() {
        return new Triplet<L, M, R>(getLeft(), this.middle, getRight());
    }

    @Override
    public String toString() {
        return "(" + getLeft() + "," + this.middle + "," + getRight() + ")";
    }

}
