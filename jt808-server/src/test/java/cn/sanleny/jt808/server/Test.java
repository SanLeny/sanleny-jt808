package cn.sanleny.jt808.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: LG
 * @Date: 2019/11/1
 * @Version: 1.0
 **/
public class Test {

    @org.junit.jupiter.api.Test
    public void test(){
        byte PKG_DELIMITER = 0x7e;
        byte a = 0x7E;
        System.out.println(a==PKG_DELIMITER);

        System.out.println( Integer.toHexString(512));
        System.out.println( Integer.toOctalString(512));
        System.out.println( Integer.toBinaryString(512));

        System.out.println( Integer.valueOf("1000000000",2).toString());
        System.out.println( Integer.valueOf("1000",8).toString());
        System.out.println( Integer.valueOf("0200",16).toString());
        System.out.println( Integer.valueOf("0000001111111111",2).toString());
        System.out.println( Integer.toHexString(1023));
        System.out.println( Integer.valueOf("0111111111111111",2).toString());
        System.out.println(3 & 0x0FFFFFFFFL);

//        short b = 512;
//        System.out.println(b >> 4);
//
//        System.out.println(Integer.MAX_VALUE);
//
//        System.out.println(125 & 0x03FF);


    }

    @org.junit.jupiter.api.Test
    public void test1(){
        LinkedBlockingQueue<String> rowKeyList = new LinkedBlockingQueue();
        rowKeyList.add("1");
        rowKeyList.add("2");
        rowKeyList.add("1");
        rowKeyList.add("1");
        rowKeyList.add("2");
        rowKeyList.add("4");
        Integer reduce = rowKeyList.stream().map(x -> Integer.valueOf(x)).reduce(0, Integer::sum);
        System.out.println(reduce);
    }

    @org.junit.jupiter.api.Test
    public void test2(){
        int a = 345;
        System.out.println( Integer.toHexString(a));
        byte[] bytes = intTOBtyes(a);
        System.out.println(HexUtil.encodeHexStr(bytes));

    }

    public static byte[] intTOBtyes(int in) {
        byte[] arr = new byte[4];
        for (int i = 0; i < 4; i++) {
            arr[i] = (byte) ((in >> 8 * i) & 0xff);
        }
        return arr;
    }

    @org.junit.jupiter.api.Test
    public void test3(){
        System.out.println(100_0000);
        System.out.println("aa"+ 0x00 + "aa");
        System.out.println("aa"+ 0b000 + "aa");
    }

    @org.junit.jupiter.api.Test
    public void test4() throws ParseException {
        Date date = new Date();
        System.out.println(date);
        String format = DateUtil.format(date,"yyMMddHHmmss");
        System.out.println(format);

        Date date1 = DateUtil.parse(format, "yyMMddHHmmss");
        System.out.println(date1);

        Date dateTime = DateUtil.parse(format, "yyMMddHHmmss");
        Jt808Message message = new Jt808Message();
        message.setReplayTime(dateTime);
        System.out.println(message.getReplayTime());

    }

}
