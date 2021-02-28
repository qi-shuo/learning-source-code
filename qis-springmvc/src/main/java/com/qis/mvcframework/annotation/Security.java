package com.qis.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @author qishuo
 * @date 2021/2/28 10:16 下午
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Security {

    String[] values();
}
