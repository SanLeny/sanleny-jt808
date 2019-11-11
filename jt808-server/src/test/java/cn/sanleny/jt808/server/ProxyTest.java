package cn.sanleny.jt808.server;

import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.protocol.entity.LocationInfo;
import cn.sanleny.jt808.server.framework.annotation.Jt808Field;
import cn.sanleny.jt808.server.framework.annotation.Jt808Math;
import cn.sanleny.jt808.server.framework.annotation.Jt808Resolver;
import cn.sanleny.jt808.server.protocol.entity.Register;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: sanleny
 * @Date: 2019-11-10
 * @Version: 1.0
 */
public class ProxyTest {


    @Test
    public void test() throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        Jt808Message message = new Jt808Message();
        Register res = new Register(message);
        Class<?> aClass = res.getClass();
        boolean annotationPresent = aClass.isAnnotationPresent(Jt808Resolver.class);
        System.out.println(annotationPresent);
        Field[] declaredFields = aClass.getDeclaredFields();
        byte[] data = message.getMsgBodyBytes();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
//            System.out.println(declaredFields[i].getType());
            Jt808Field annotation = declaredFields[i].getAnnotation(Jt808Field.class);
//            System.out.println(annotation);
            Class<?> typeClass = field.getType();
            if(typeClass.equals(String.class)){
                System.out.println(1);
            }else if(typeClass.equals(int.class) || typeClass.equals(Integer.class)){
                System.out.println(2);
            }else if(typeClass.equals(Double.class) || typeClass.equals(double.class)){
                System.out.println(3);
            }

//            ReflectUtil.setFieldValue(res,field,2);
//            System.out.println(ReflectUtil.getFieldValue(res,field));
//            declaredFields[i].setAccessible(true);
//            Method declaredMethod = aClass.getDeclaredMethod(declaredFields[i].getName(), declaredFields[i].getType());
//            System.out.println(declaredMethod.getName());
        }
        System.out.println(res);

    }

    @Test
    public void test1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Jt808Message message = new Jt808Message();
        LocationInfo res = new LocationInfo(message);
        Class<?> aClass = res.getClass();
        boolean annotationPresent = aClass.isAnnotationPresent(Jt808Resolver.class);
        System.out.println(annotationPresent);
        Field[] declaredFields = aClass.getDeclaredFields();
        byte[] data = message.getMsgBodyBytes();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
//            System.out.println(declaredFields[i].getType());
            Jt808Field annotation = field.getAnnotation(Jt808Field.class);
//            System.out.println(annotation);
            Class<?> typeClass = field.getType();
            if(typeClass.equals(String.class)){
//                System.out.println(1);
            }else if(typeClass.equals(int.class) || typeClass.equals(Integer.class)){
//                System.out.println(2);
            }else if(typeClass.equals(Double.class) || typeClass.equals(double.class)){

                if(field.isAnnotationPresent(Jt808Math.class)) {
                    Jt808Math math = field.getAnnotation(Jt808Math.class);
                    Class<?> mathClass = math.aClass();
                    Method method = mathClass.getMethod(math.method(),math.args());
                    System.out.println(method.getName());
                    Object invoke = method.invoke(method.getName(), 100, 200);
                    System.out.println(invoke);
                }

            }

//            ReflectUtil.setFieldValue(res,field,2);
//            System.out.println(ReflectUtil.getFieldValue(res,field));
//            declaredFields[i].setAccessible(true);
//            Method declaredMethod = aClass.getDeclaredMethod(declaredFields[i].getName(), declaredFields[i].getType());
//            System.out.println(declaredMethod.getName());
        }
        System.out.println(res);
    }
}
