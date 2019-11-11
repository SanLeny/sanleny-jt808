package cn.sanleny.jt808.server.framework.annotation;

import java.lang.annotation.*;

/**
 * 两位 简单数学算术
 * @Author: sanleny
 * @Date: 2019-11-10
 * @Version: 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Jt808Math {

    /**
     * 数学计算类
     */
    Class<?> aClass();


    /**
     * 数学计算方法
     * @return
     */
    String method();

    /**
     * 数学计算方法参数
     * @return
     */
    Class<?>[] args() default {Number.class,Number.class};


    /**
     * 参与数学计算参数值位置
     * @return
     */
    int index() default 1;

    /**
     * 参与注解数学计算的参数值
     * @return
     */
    int number() default 10;

}
