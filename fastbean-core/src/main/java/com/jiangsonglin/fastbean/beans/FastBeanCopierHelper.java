package com.jiangsonglin.fastbean.beans;

import com.jiangsonglin.fastbean.copier.BeanUtilsCopier;
import com.jiangsonglin.fastbean.copier.FastBeanCopier;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/11
 */
public class FastBeanCopierHelper {
    private final static Map<String, SoftReference<FastBeanCopier>> BEAN_COPY_CACHE = new ConcurrentHashMap<>();

    /**
     * 创建一个copier
     *
     * @param srcClass    源class对象
     * @param targetClass 目标class对象
     * @return
     */
    public static FastBeanCopier create(Class srcClass, Class targetClass) {
        return creates(srcClass, targetClass, null, null);
    }

    /**
     * 创建一个copier,支持属性映射和字段忽略
     *
     * @param srcClass    源class对象
     * @param targetClass 目标class对象
     * @param nameMapping 属性映射对象
     * @param ignoreSet   字段忽略集合
     * @return
     */
    public static FastBeanCopier create(Class srcClass, Class targetClass, HashMap<String, String> nameMapping, Set<String> ignoreSet) {
        return creates(srcClass, targetClass, nameMapping, ignoreSet);
    }

    /**
     * 创建一个copier,支持lambda进行属性映射和字段忽略
     * 相对比较耗时
     *
     * @param srcClass           源class对象
     * @param targetClass        属性映射对象
     * @param nameMappingWrapper 属性映射wrapper对象
     * @param ignoreWrapper      字段忽略wrapper对象
     * @return
     */
    public static FastBeanCopier create(Class srcClass, Class targetClass, LambdaNameMappingWrapper nameMappingWrapper
            , LambdaIgnoreWrapper ignoreWrapper) {
        return creates(srcClass, targetClass, nameMappingWrapper == null ? null : nameMappingWrapper.nameMap, ignoreWrapper == null ? null : ignoreWrapper.ignoreSet);
    }

    private static FastBeanCopier creates(Class srcClass, Class targetClass, HashMap<String, String> nameMapping, Set<String> ignoreSet) {
        String key = srcClass.getName() + "$" + targetClass.getName();
        if (nameMapping != null && !nameMapping.isEmpty()) {
            key = key + "$" + nameMapping.hashCode();
        }
        if (ignoreSet != null && !ignoreSet.isEmpty()) {
            key = key + "$" + ignoreSet.hashCode();
        }

        SoftReference<FastBeanCopier> softReference = BEAN_COPY_CACHE.get(key);
        if (softReference == null) {
            FastBeanCopier copier = BeanUtilsCopier.create(srcClass, targetClass, nameMapping, ignoreSet);
            BEAN_COPY_CACHE.put(key, new SoftReference<>(copier));
            return copier;
        }
        return softReference.get();
    }
}
