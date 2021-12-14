package com.jiangsonglin.fastbean.convert;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/10
 */
public interface Converter {
    /**
     * 转换器, 将getter的值经过转换返回
     * @param value getter的值
     * @param target setter中参数的类型
     * @return 经过转换后的值, 类型应与setter的类型一样
     */
    Object convert(Object value, Class target);
}
