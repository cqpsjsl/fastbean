package com.jiangsonglin.fastbean.strategy;

/**
 * @author kedron
 * @version 1.0
 * @©copyright
 * @since 2.0.1
 */
public final class StrategyConstant {
    /**
     * 不允许设置NULL值,表现出来的代码
     * if(a.get()!=null) b.set(a.get())
     */
    public static final Integer NOT_CAN_SET_NULL = 0;

    /**
     * 允许设置NULL值,表现出来的代码
     * b.set(a.get())
     */
    public static final Integer CAN_SET_NULL = 1;

    /**
     * 不允许覆盖 if(a.get()==null) a.set(b.get())
     */
    public static final Integer NOT_CAN_SET_COVER = 0;
    /**
     * 允许覆盖 a.set(b.get())
     */
    public static final Integer CAN_SET_COVER = 1;
    /**
     * 浅拷贝
     */
    public static final Integer SHALLOW_COPY = 0;
    /**
     * 深拷贝
     */
    public static final Integer DEEP_COPY = 1;


}
