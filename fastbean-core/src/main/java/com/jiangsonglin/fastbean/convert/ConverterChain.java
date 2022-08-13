package com.jiangsonglin.fastbean.convert;

/**
 * <p>
 * 执行链
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/11
 */
public interface ConverterChain {
    /**
     * 转换器, 将getter的值经过转换返回
     * @param value getter的值
     * @param target setter中参数的类型
     * @return 经过转换后的值, 类型应与setter的类型一样
     */
    Object convert(Object value, Class target);

    /**
     *  给ConvertChain添加一个Converter
     * @param converter
     */
    ConverterChain add(Converter converter);

    /**
     * 返回一个副本，不会影响到当前的
     * @return
     */
    ConverterChain copy();
}
