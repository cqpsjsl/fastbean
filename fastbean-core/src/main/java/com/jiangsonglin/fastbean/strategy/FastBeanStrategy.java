package com.jiangsonglin.fastbean.strategy;

/**
 * @author kedron
 * @version 2.0.1
 * @©copyright
 */
public final class FastBeanStrategy {

    /**
     * 设置空值策略
     */
    public Integer setNullStrategy = StrategyConstant.NOT_CAN_SET_NULL;
    /**
     * 设置覆盖策略
     */
    public Integer coverStrategy = StrategyConstant.NOT_CAN_SET_COVER;

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

    public Object deepCopy(Object obj) {
        System.out.println("进入深拷贝");
        return obj;
    }
}

