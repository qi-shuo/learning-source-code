package com.qis.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @author qishuo
 * @date 2021/2/28 2:15 下午
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "";
}
