package com.jiangsonglin.fastbean.strategy;

import com.jiangsonglin.fastbean.interfaces.FastCloneable;
import com.jiangsonglin.fastbean.utils.BeanCopyUtil;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author kedron
 * @version 2.0.1
 * @©copyright
 */
public final class FastBeanStrategy {

    /**
     * 设置空值策略
     */
    private Integer setNullStrategy = StrategyConstant.NOT_CAN_SET_NULL;
    /**
     * 设置覆盖策略
     */
    private Integer coverStrategy = StrategyConstant.NOT_CAN_SET_COVER;
    /**
     * 拷贝策略
     */
    private Integer copyStrategy = StrategyConstant.SHALLOW_COPY;

    /**
     *
     * @param srcValue
     * @param targetValue
     * @return null 不设置值
     */
    public Boolean handleStrategy(Object srcValue, Object targetValue) {
        // 有值不允许覆盖
        if (targetValue != null && coverStrategy.equals(StrategyConstant.NOT_CAN_SET_COVER)) {
            return null;
        }

        // 为空不允许设置null值
        if (srcValue == null && setNullStrategy.equals(StrategyConstant.NOT_CAN_SET_NULL)) {
            return null;
        }
        // 允许set值
        return true;
    }

    /**
     * 深拷贝
     * @param obj
     * @return
     */
    public Object deepCopy(Object obj) {
        // 浅拷贝 或者不需要额外操作的拷贝
        if (obj == null ||
                copyStrategy.equals(StrategyConstant.SHALLOW_COPY) ||
                boxingClazz(obj.getClass())) {
            return obj;
        }
        // 是数组 https://www.cnblogs.com/zc22/p/3484981.html
        if (obj.getClass().isArray())
        {
            int length = Array.getLength(obj);
            Object clone = Array.newInstance(obj.getClass().getComponentType(), length);
            for (int i = 0; i < length; i++)
            {
                Array.set(clone, i, deepCopy(Array.get(obj, i)));
            }
            return clone;
        }
        // 实现了FastCloneable 默认是浅拷贝，需要自己实现
        if (obj instanceof FastCloneable) {
            try {
                return ((FastCloneable) obj).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return obj;
            }
        }

        // 深拷贝
        if (obj instanceof Collection || obj instanceof Map) {
            // 暂时只支持ArrayList的深度复制
            if (obj instanceof ArrayList) {
                return ((ArrayList<?>) obj).stream() .map((Function<Object, Object>) this::deepCopy).collect(Collectors.toList());
            }
            // 集合类处理
            // 尝试序列化
            if (obj instanceof Serializable) {
                try {
                    return serializableClone(obj);
                } catch (IOException | ClassNotFoundException ioException) {
//                        ioException.printStackTrace();
                }
            }
        }else {
            try {
                Object o = obj.getClass().newInstance();
                return BeanCopyUtil.chain(obj, o)
                        .strategy(this)
                        .copy();
            } catch (InstantiationException | IllegalAccessException e) {
                // 尝试序列化
                if (obj instanceof Serializable) {
                    try {
                        return serializableClone(obj);
                    } catch (IOException | ClassNotFoundException ioException) {
//                        ioException.printStackTrace();
                    }
                }
                // 失败返回原对象
                return obj;
            }
        }
        return obj;
    }

    public Integer getSetNullStrategy() {
        return setNullStrategy;
    }

    public void setSetNullStrategy(Integer setNullStrategy) {
        this.setNullStrategy = setNullStrategy;
    }

    public Integer getCoverStrategy() {
        return coverStrategy;
    }

    public void setCoverStrategy(Integer coverStrategy) {
        this.coverStrategy = coverStrategy;
    }

    public Integer getCopyStrategy() {
        return copyStrategy;
    }

    public void setCopyStrategy(Integer copyStrategy) {
        this.copyStrategy = copyStrategy;
    }

    /**
     * 判断是否是基本类型的包装类和String
     * @param className
     * @return
     */
    private boolean boxingClazz(Class className) {
        if (className.equals(java.lang.Integer.class) ||
                className.equals(java.lang.Byte.class)||
                className.equals(java.lang.Long.class)||
                className.equals(java.lang.Double.class)||
                className.equals(java.lang.Float.class)||
                className.equals(java.lang.Character.class)||
                className.equals(java.lang.Short.class)||
                className.equals(java.lang.String.class)||
                className.equals(java.lang.Boolean.class)) {
            return true;
        }
        return false;
    }

    /**
     * 序列化 https://www.cnblogs.com/ysocean/p/8482979.html
     * @param obj
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Object serializableClone(Object obj) throws IOException, ClassNotFoundException {
        // 序列化
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(obj);

        // 反序列化
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);

        return ois.readObject();
    }
}

