package com.qis.annotation;

import java.lang.annotation.*;

/**
 * @author qishuo
 * @date 2021/2/14 4:48 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scan {
    String value();
}
