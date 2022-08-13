package com.jiangsonglin.fastbean.beans;

import com.jiangsonglin.fastbean.convert.Converter;
import com.jiangsonglin.fastbean.convert.ConverterChain;
import com.jiangsonglin.fastbean.copier.FastBeanCopier;
import com.jiangsonglin.fastbean.interfaces.JConsumer;
import com.sun.tools.javac.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/11
 */
public class FastBeanCopierChain<S, T> {
    private final S srcObject;
    private final T targetObject;
    private final LambdaNameMappingWrapper<T, S> nameMappingWrapper = new LambdaNameMappingWrapper<>();
    private final LambdaIgnoreWrapper<T> ignoreWrapper = new LambdaIgnoreWrapper<>();
    /**
     * 局部生效的
     */
    private ConverterChain partConverterChain = null;
    private List<Converter> partConverterList = new ArrayList<>();

    public FastBeanCopierChain(S srcObject, T targetObject) {
        this.srcObject = srcObject;
        this.targetObject = targetObject;
    }

    /**
     * 增加一个名称映射
     *
     * @param targetKey target
     * @param srcKey    src
     * @return
     */
    public FastBeanCopierChain<S, T> nameMapping(JConsumer<T> targetKey, JConsumer<S> srcKey) {
        nameMappingWrapper.add(targetKey, srcKey);
        return this;
    }

    /**
     * target ignore，设置了就不会set
     *
     * @param targetKey target
     * @return
     */
    public FastBeanCopierChain<S, T> ignore(JConsumer<T> targetKey) {
        ignoreWrapper.add(targetKey);
        return this;
    }

    /**
     * 添加一个局部生效的转换器链
     *
     * @param partConverterChain
     * @return
     */
    public FastBeanCopierChain<S, T> converterChain(ConverterChain partConverterChain) {
        this.partConverterChain = partConverterChain;
        return this;
    }

    /**
     * 添加一个局部生效的converter，基于全局或者局部转换器（有局部只使用局部）
     *
     * @param partConverters
     * @return
     */
    public FastBeanCopierChain<S, T> converter(Converter... partConverters) {
        if (this.partConverterChain != null) {
            for (Converter partConverter : partConverters) {
                partConverterChain.add(partConverter);
            }
        } else {
            partConverterList.addAll(Arrays.asList(partConverters));
        }
        return this;
    }

    /**
     * 复制
     *
     * @return
     */
    public T copy() {
        Assert.checkNonNull(srcObject, "srcObject cant not be null");
        Assert.checkNonNull(targetObject, "targetObject cant not be null");

        FastBeanCopier fastBeanCopier = FastBeanCopierHelper.create(srcObject.getClass(), targetObject.getClass(), nameMappingWrapper, ignoreWrapper);
        if (partConverterChain != null) {
            fastBeanCopier.copy(srcObject, targetObject, partConverterChain);
        } else if (!partConverterList.isEmpty()) {
            fastBeanCopier.copy(srcObject, targetObject, partConverterList.toArray(new Converter[0]));
        } else {
            fastBeanCopier.copy(srcObject, targetObject);
        }
        return targetObject;
    }
}
