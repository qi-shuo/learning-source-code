package com.qis.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @author qishuo
 * @date 2021/2/28 10:32 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
