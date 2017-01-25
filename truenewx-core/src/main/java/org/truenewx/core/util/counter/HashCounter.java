package org.truenewx.core.util.counter;

import java.util.HashMap;

/**
 * 基于HashMap的计数器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class HashCounter<K> extends AbstractCounter<K> {

    protected HashCounter() {
        super(new HashMap<>());
    }

}
