package com.jiangsonglin.fastbean.interfaces;

/**
 * @author kedron
 * @version 1.0 把object克隆接口暴露出来
 * @©copyright
 */
public interface FastCloneable extends Cloneable{
    /**
     * 克隆接口
     * @return
     */
    Object clone() throws CloneNotSupportedException;
}
