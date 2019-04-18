package org.truenewx.core.spring.core.env.functor;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.core.Strings;
import org.truenewx.core.functor.NullaryFunction;
import org.truenewx.core.spring.beans.ContextInitializedBean;

/**
 * 函数：获取当前profile
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class FuncProfile extends NullaryFunction<String> implements ContextInitializedBean {
    /**
     * 当前profile
     */
    private static String PROFILE = Strings.EMPTY; // 默认为空，表示无profile区分
    /**
     * 实例
     */
    public static FuncProfile INSTANCE = null;

    public static String getProfile(ApplicationContext context) {
        String[] profiles = context.getEnvironment().getActiveProfiles();
        if (profiles.length > 0) {
            return profiles[0];
        }
        return Strings.EMPTY;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        if (INSTANCE == null) {
            PROFILE = getProfile(context);
            INSTANCE = context.getBean(FuncProfile.class);
        }
    }

    @Override
    public String apply() {
        return PROFILE;
    }

}
