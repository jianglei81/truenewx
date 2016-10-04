package org.truenewx.core.event;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.exception.MultiException;

/**
 * 订阅者异常集合
 *
 * @author jianglei
 * @since JDK 1.8
 */
class SubscriberExceptionCollection {
    private HandleableException handleableException;
    private List<Throwable> throwables;

    public void add(final Throwable exception) {
        if (exception instanceof HandleableException) { // 可处理异常特殊处理
            final HandleableException he = (HandleableException) exception;
            if (this.handleableException == null) { // 原本没保存异常，则直接保存异常
                this.handleableException = he;
            } else if (this.handleableException instanceof BusinessException) { // 原本为单个业务异常，则重组为业务异常集
                final MultiException bes = new MultiException(
                                (BusinessException) this.handleableException);
                bes.merge(he);
                this.handleableException = bes;
            } else if (this.handleableException instanceof MultiException) { // 原本为业务异常集，则直接加入新的异常
                ((MultiException) this.handleableException).merge(he);
            }
        } else { // 非可处理异常保存到throwables中
            if (this.throwables == null) {
                this.throwables = new ArrayList<>();
            }
            this.throwables.add(exception);
        }
    }

    public HandleableException getHandleableException() {
        return this.handleableException;
    }

    public Iterable<Throwable> getThrowables() {
        return this.throwables;
    }
}
