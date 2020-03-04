package cn.sanleny.jt808.server.framework.aop;


import cn.hutool.core.util.ReflectUtil;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import cn.sanleny.jt808.server.framework.annotation.Jt808Field;
import cn.sanleny.jt808.server.framework.annotation.Jt808Math;
import cn.sanleny.jt808.server.framework.annotation.Jt808Resolver;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.exception.GlobalFallbackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * @Author: sanleny
 * @Date: 2019-11-10
 * @Version: 1.0
 */
@Slf4j
public class ResolveMethodInterceptor implements MethodInterceptor {

    Jt808Message message;
    public ResolveMethodInterceptor(Jt808Message message) {
        this.message = message;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        try {
            if (this.message != null) {
                Object res = methodProxy.invokeSuper(o, objects);
                Class<?> aClass = res.getClass();
                if(aClass.isAnnotationPresent(Jt808Resolver.class)){
                    byte[] data = ((Jt808Message)res).getMsgBodyBytes();

                    Field[] declaredFields = aClass.getDeclaredFields();
                    for (int i = 0; i < declaredFields.length; i++) {
                        Field field = declaredFields[i];
                        if(field.isAnnotationPresent(Jt808Field.class)){
                            Jt808Field jt808Field = field.getAnnotation(Jt808Field.class);
                            int index = jt808Field.index();
                            int length = jt808Field.length()==-1?(data.length-index):(jt808Field.length());
                            Class<?> typeClass = field.getType();
                            Object value;
                            if(typeClass.equals(String.class)){
                                value = Jt808Utils.parseStringFromBytes(data, index, length);
                            }else if(typeClass.equals(int.class) || typeClass.equals(Integer.class)
                                    || typeClass.equals(Double.class) || typeClass.equals(double.class)){
                                value = Jt808Utils.parseIntFromBytes(data, index, length);
                                if(field.isAnnotationPresent(Jt808Math.class)) {
                                    Jt808Math jt808Math = field.getAnnotation(Jt808Math.class);
                                    Class<?> mathClass = jt808Math.aClass();
                                    Method mathClassMethod = mathClass.getMethod(jt808Math.method(),jt808Math.args());
                                    if(jt808Math.index() ==1 ){
                                        value = mathClassMethod.invoke(mathClassMethod.getName(), value, jt808Math.number());
                                    }else {
                                        value = mathClassMethod.invoke(mathClassMethod.getName(),jt808Math.number(),value);
                                    }
                                }
                            }else if(typeClass.equals(Date.class)){
                                value = Jt808Utils.generateDate(data,index,length);
                            }
                            else {
                                throw new GlobalFallbackException("不支持的数据类型！" + typeClass);
                            }
                            ReflectUtil.setFieldValue(res,field,value);
                        }
                    }
                }
                return res;
            }
        }catch (Exception e){
            log.error(">>> 解析失败,header：{}, error:{}",message.getHeader().getMessageType(),e.toString(),e);
            message.setReplyCode(Jt808Constants.RESP_MSG_ERROR);
        }
        return message;
    }
}
