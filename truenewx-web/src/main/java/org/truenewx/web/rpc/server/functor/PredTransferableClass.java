package org.truenewx.web.rpc.server.functor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.truenewx.core.functor.BinatePredicate;

/**
 * 断言：可转换的类型
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class PredTransferableClass extends BinatePredicate<Class<?>, Class<?>> {
    /**
     * 单例
     */
    public static final PredTransferableClass INSTANCE = new PredTransferableClass();

    /**
     * 可转换的类型集
     */
    private Map<Class<?>, List<Class<?>>> transferables = new HashMap<>();

    private PredTransferableClass() {
        putTransferable(byte.class, char.class, short.class, int.class, long.class,
                        BigDecimal.class);
        putTransferable(char.class, short.class, int.class, long.class, BigDecimal.class);
        putTransferable(short.class, int.class, long.class, BigDecimal.class);
        putTransferable(int.class, long.class, BigDecimal.class);
        putTransferable(long.class, BigDecimal.class);
        putTransferable(float.class, double.class, BigDecimal.class);
        putTransferable(double.class, BigDecimal.class);
    }

    private void putTransferable(final Class<?> clazz, final Class<?>... otherClasses) {
        final List<Class<?>> transferableClasses = new ArrayList<>();
        for (final Class<?> otherClass : otherClasses) {
            transferableClasses.add(otherClass);
            final Class<?> otherEquiavlentClass = PredEquivalentClass.INSTANCE
                            .getEquivalentClass(otherClass);
            if (otherEquiavlentClass != null) {
                transferableClasses.add(otherEquiavlentClass);
            }
        }
        this.transferables.put(clazz, transferableClasses);
        final Class<?> equivalentClass = PredEquivalentClass.INSTANCE.getEquivalentClass(clazz);
        if (equivalentClass != null) {
            this.transferables.put(equivalentClass, transferableClasses);
        }
    }

    @Override
    public boolean apply(final Class<?> expected, final Class<?> actual) {
        final List<Class<?>> transferableClasses = this.transferables.get(actual);
        return transferableClasses != null && transferableClasses.contains(expected);
    }

}
