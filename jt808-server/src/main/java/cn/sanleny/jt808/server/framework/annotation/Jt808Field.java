package cn.sanleny.jt808.server.framework.annotation;


import java.lang.annotation.*;

/**
 * 表示JT808 需要解析的字段
 * 作用于字段上
 * @Author: sanleny
 * @Date: 2019-11-09
 * @Version: 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Jt808Field {

    /**
     * 起始字节位置
     */
    int index();

    /**
     * 字节长度, -1 表示从 index 开始到 data数组的最后 即[index,data.length-index]
     */
    int length();

}
