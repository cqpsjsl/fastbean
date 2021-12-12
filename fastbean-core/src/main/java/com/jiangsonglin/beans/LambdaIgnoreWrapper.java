package com.jiangsonglin.beans;

import com.jiangsonglin.interfaces.JConsumer;
import com.jiangsonglin.utils.LambdaUtils;

import java.util.HashSet;

/**
 * value is target object filed name.
 * @author jiangsonglin.com
 */
public class LambdaIgnoreWrapper<T>{
     HashSet<String> ignoreSet = new HashSet<>();
     private void handler(JConsumer<T> key) {
        String k = LambdaUtils.doConsumer(key);
        if (k != null) {
            ignoreSet.add(k);
        }
    }

    /**
     * 目标对象中需要忽略setter的属性名。
     * @param key
     * @return
     */
    public LambdaIgnoreWrapper add(JConsumer<T> key){
         handler(key);
         return this;
    }
}
