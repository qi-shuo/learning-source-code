package com.qis.annotation;

import java.lang.annotation.*;

/**
 * @author qishuo
 * @date 2021/2/14 3:39 下午
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {
}
