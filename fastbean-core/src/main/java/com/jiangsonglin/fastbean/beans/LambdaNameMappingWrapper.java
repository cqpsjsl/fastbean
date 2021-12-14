package com.jiangsonglin.fastbean.beans;

import com.jiangsonglin.fastbean.interfaces.JConsumer;
import com.jiangsonglin.fastbean.utils.LambdaUtils;

import java.util.HashMap;

/**
 * 属性映射支持lambda的包装器
 * @param <K> key is target filed name.
 * @param <V> value is source filed name.
 * @author jiangsonglin.com
 */
public class LambdaNameMappingWrapper<K, V> {
    HashMap<String, String> nameMap = new HashMap<>();

    private void handler(JConsumer<K> key, JConsumer<V> value) {
        long l = System.currentTimeMillis();
        String k = LambdaUtils.doConsumer(key);
        String v = LambdaUtils.doConsumer(value);
        if (k != null && v != null) {
            nameMap.put(k, v);
        }
    }

    /**
     * 添加一对属性映射
     * @param key key is target filed name.
     * @param value value is source filed name.
     * @return @this
     */
    public LambdaNameMappingWrapper<K, V> add(JConsumer<K> key, JConsumer<V> value) {
        handler(key, value);
        return this;
    }
}
