package com.qis.annotation;

import java.lang.annotation.*;

/**
 * @author qishuo
 * @date 2021/2/14 3:38 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {

    String value() default "";
}
