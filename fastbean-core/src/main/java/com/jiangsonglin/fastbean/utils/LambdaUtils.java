package com.jiangsonglin.fastbean.utils;

import com.jiangsonglin.fastbean.interfaces.JConsumer;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jiangsonglin.com
 */
public class LambdaUtils {
//    private final static Map<String, WeakReference<SerializedLambda>> CONSUMER_FUC_CACHE = new ConcurrentHashMap<>();

    /**
     * reference mybatis-plus
     */
    public static <T> String doConsumer(JConsumer<T> func){
        return getFiledName(getSlLambda(func));

    }
    public static String getFiledName(SerializedLambda serializedLambda){
        if (serializedLambda == null) return null;
        String implMethodName = serializedLambda.getImplMethodName();
        // 判断方法名开头 is or get
        String filedName = null;
        if (implMethodName.startsWith("is")) {
            filedName = implMethodName.substring(2);
        } else {
            filedName = implMethodName.substring(3);
        }
        return lowerFirst(filedName);
    }

    /**
     *
     * reference baidu.com
     */
    public static String lowerFirst(String fromStr) {
        char[] chars = fromStr.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
    /**
     *
     * reference baidu.com
     */
    private static <T> java.lang.invoke.SerializedLambda getSlLambda(JConsumer<T> func) {
        // 直接调用writeReplace
        Method writeReplace = null;
        try {
            writeReplace = func.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            Object sl = null;
            sl = writeReplace.invoke(func);
            java.lang.invoke.SerializedLambda serializedLambda = (java.lang.invoke.SerializedLambda) sl;
            return serializedLambda;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
