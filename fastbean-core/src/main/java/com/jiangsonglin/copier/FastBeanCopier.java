package com.jiangsonglin.copier;

import com.jiangsonglin.convert.Converter;
import com.jiangsonglin.convert.ConverterChain;
import com.jiangsonglin.convert.DefaultConverter;
import com.jiangsonglin.convert.DefaultConverterChain;

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

    /**
     * 不带转换器copy
     * @param from
     * @param to
     */
    public void copy(Object from, Object to) {
        copier.copy(from, to, this.converterChain);
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
