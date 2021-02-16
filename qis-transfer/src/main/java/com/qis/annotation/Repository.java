package com.qis.annotation;

import java.lang.annotation.*;

/**
 * @author qishuo
 * @date 2021/2/14 3:39 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {

    String value() default "";
}
