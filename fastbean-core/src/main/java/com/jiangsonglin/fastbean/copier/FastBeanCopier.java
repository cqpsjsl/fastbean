package com.jiangsonglin.fastbean.copier;

import com.jiangsonglin.fastbean.convert.Converter;
import com.jiangsonglin.fastbean.convert.ConverterChain;
import com.jiangsonglin.fastbean.convert.DefaultConverter;
import com.jiangsonglin.fastbean.convert.DefaultConverterChain;

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
    private ConverterChain converterChain = new DefaultConverterChain().add(new DefaultConverter());

     protected FastBeanCopier() {
    }

    /**
     * 默认转换器copy
     * @param from
     * @param to
     */
    public void copy(Object from, Object to) {
        copier.copy(from, to, this.converterChain);
    }
    /**
     * 默认转换器copy
     * @param from
     * @param toClazz
     */
    public <T> T copy(Object from, Class<T> toClazz) {
        try {
            T t = toClazz.newInstance();
            copier.copy(from, t, this.converterChain);
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 在默认转换器链上新增转换器
     * @param from
     * @param to
     * @param converters
     */
    public void copy(Object from, Object to, Converter... converters) {
        for (Converter converter : converters) {
            this.converterChain.add(converter);
        }
        copier.copy(from, to, this.converterChain);
    }

    /**
     *  定义自己的转换器, 默认转换器将会失效
     * @param converterChain
     */
    public void setConverterChain(ConverterChain converterChain) {
        this.converterChain = converterChain;
    }
}
