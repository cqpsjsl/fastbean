package com.jiangsonglin.fastbean.utils;

import com.jiangsonglin.fastbean.beans.FastBeanCopierChain;
import com.jiangsonglin.fastbean.beans.FastBeanCopierHelper;
import com.jiangsonglin.fastbean.copier.FastBeanCopier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangsonglin.com
 * @version 1.0
 * @description 封装一些常用的方法, 灵活diy@{@link com.jiangsonglin.fastbean.copier.FastBeanCopier}
 */
public class BeanCopyUtil {
    /**
     * 复制一个对象
     *
     * @param form        从from对象中复制到target中
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T copy(Object form, Class<T> targetClass) {
        return FastBeanCopierHelper.create(form.getClass(), targetClass).copy(form, targetClass);
    }

    /**
     * 复制一个对象
     *
     * @param form 从from对象中复制到target中
     * @param
     * @param <T>
     */
    public static <T> T copy(Object form, T targetObj) {
        FastBeanCopierHelper.create(form.getClass(), targetObj.getClass()).copy(form, targetObj, null);
        return targetObj;
    }

    /**
     * 链式配置
     *
     * @param form      从from对象中复制到target中
     * @param targetObj
     * @param <T>
     */
    public static <T, V> FastBeanCopierChain<T, V> chain(T form, V targetObj) {
        return new FastBeanCopierChain<T, V>(form, targetObj);
    }

    /**
     * 链式配置
     *
     * @param form        从from对象中复制到target中
     * @param targetClazz
     * @param <T>
     */
    public static <T, V> FastBeanCopierChain<T, V> chain(T form, Class<V> targetClazz) {
        V v = null;
        try {
            v = targetClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new FastBeanCopierChain<T, V>(form, v);
    }

    /**
     * 复制成list，从srcList出一个List
     *
     * @param srcList
     * @param srcClass
     * @param targetClass
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> List<T> copyList(List<V> srcList, Class<V> srcClass, Class<T> targetClass) {
        FastBeanCopier copier = FastBeanCopierHelper.create(srcClass, targetClass);
        List<T> list = new ArrayList<>(srcList.size());
        srcList.forEach(item -> list.add(copier.copy(item, targetClass)));
        return list;
    }
}
