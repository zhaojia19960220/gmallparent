package com.atguigu.gmall.common.cache;


import java.lang.annotation.*;

//aop+分布式锁会用到自定义注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
    String prefix() default "cache";
}
