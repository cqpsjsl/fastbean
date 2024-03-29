package com.jiangsonglin.fastbean.copier;

import com.jiangsonglin.fastbean.convert.Converter;
import com.jiangsonglin.fastbean.convert.ConverterChain;
import com.jiangsonglin.fastbean.convert.DefaultConverterChain;
import com.jiangsonglin.fastbean.strategy.FastBeanStrategy;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/13
 */
public class FastBeanCopier {
    BeanUtilsCopier copier;
    /**
     * 转换器链，默认是一个空的链，可参考@{@link DefaultConverterChain} 实现, 替换全局的转换器链使用@{@link #setGlobalConverterChain(ConverterChain)}
     */
     private static ConverterChain globalConverterChain = new DefaultConverterChain();

    private static FastBeanStrategy globalFastBeanStrategy = new FastBeanStrategy();
     protected FastBeanCopier() {
    }

    /**
     * 默认转换器copy
     * @param from
     * @param to
     */
    public void copy(Object from, Object to, FastBeanStrategy partStrategy) {
        copier.copy(from, to, FastBeanCopier.globalConverterChain, partStrategy == null ?
                FastBeanCopier.globalFastBeanStrategy
                : partStrategy);
    }
    /**
     * 默认转换器copy
     * @param from
     * @param toClazz
     */
    public <T> T copy(Object from, Class<T> toClazz) {
        try {
            T t = toClazz.newInstance();
            copier.copy(from, t, FastBeanCopier.globalConverterChain, FastBeanCopier.globalFastBeanStrategy);
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 在默认转换器链上新增转换器(不影响到全局)
     * @param from
     * @param to
     * @param converters 局部的转换器，不会影响到全局
     */
    public void copy(Object from, Object to, FastBeanStrategy partStrategy, Converter... converters) {
        ConverterChain copy = FastBeanCopier.globalConverterChain.copy();
        for (Converter converter : converters) {
            copy.add(converter);
        }
        copier.copy(from, to, copy, partStrategy == null ?
                FastBeanCopier.globalFastBeanStrategy
                : partStrategy);
    }
    /**
     * 单次转换器链路，全局转换链会失效
     * @param from
     * @param to
     * @param partConverterChain 局部转换器链
     */
    public void copy(Object from, Object to, FastBeanStrategy partStrategy, ConverterChain partConverterChain) {
        copier.copy(from, to, partConverterChain, partStrategy == null ?
                FastBeanCopier.globalFastBeanStrategy
                : partStrategy);
    }

    /**
     *  定义自己的全局转换器, 默认转换器将会失效，对该对象全局生效
     * @param globalConverterChain
     */
    public static synchronized void setGlobalConverterChain(ConverterChain globalConverterChain) {
        FastBeanCopier.globalConverterChain = globalConverterChain;
    }

    /**
     * 自定义全局策略
     * @param globalFastBeanStrategy
     */
    public static synchronized void setGlobalFastBeanStrategy(FastBeanStrategy globalFastBeanStrategy) {
        FastBeanCopier.globalFastBeanStrategy = globalFastBeanStrategy;
    }
}
