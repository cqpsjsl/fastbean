package com.jiangsonglin.fastbean.convert;

/**
 * <p>
 * 默认的类型转换器
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/10
 */
public class EnumConverter implements Converter {
    @Override
    public Object convert(Object value, Class target) {
        if (value == null) return null;
        if (value instanceof Enum && target.equals(String.class)) {
            return ((Enum<?>) value).name();
        }
        return value;
    }
}
