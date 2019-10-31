package com.brandslink.cloud.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Base64工具类
 */
public class Base64Util {

    public static String encodeBase64(String s){
        Object retObj= null;
        try {
            byte[] input = s.getBytes();
            Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
            Method mainMethod= clazz.getMethod("encode", byte[].class);
            mainMethod.setAccessible(true);
            retObj = mainMethod.invoke(null, new Object[]{input});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return (String)retObj;
    }


    /***
     * decode by Base64
     */
    public static String decodeBase64(String input) {
        Object retObj= null;
        try {
            Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
            Method mainMethod= clazz.getMethod("decode", String.class);
            mainMethod.setAccessible(true);
            retObj = mainMethod.invoke(null, input);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new String((byte[])retObj);
    }

}
