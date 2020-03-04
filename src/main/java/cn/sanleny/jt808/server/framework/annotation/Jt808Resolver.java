package cn.sanleny.jt808.server.framework.annotation;


import java.lang.annotation.*;

/**
 * 表示为 JT808需要解析的实体类
 * 作用于类上
 * @Author: sanleny
 * @Date: 2019-11-10
 * @Version: 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Jt808Resolver {

}
