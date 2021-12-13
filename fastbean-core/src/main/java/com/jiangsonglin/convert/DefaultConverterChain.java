package com.jiangsonglin.convert;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 默认convert执行链
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/11
 */
public class DefaultConverterChain implements ConverterChain{
    private List<Converter> convertChain = new ArrayList<>();
    /**
     * 转换器, 将getter的值经过转换返回
     *
     * @param value  getter的值
     * @param target setter中参数的类型
     * @return 经过转换后的值, 类型应与setter的类型一样
     */
    @Override
    public Object convert(Object value, Class target) {
        Object o = value;
        for (Converter converter : convertChain) {
            o = converter.convert(o, target);
        }
        if (o == null) return o;
        if (o == value) return null;
        return target.isAssignableFrom(o.getClass()) ? o : null;
    }
    @Override
    public ConverterChain add(Converter converter) {
        convertChain.add(converter);
        return this;
    }
}
