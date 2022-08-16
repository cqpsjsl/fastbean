package com.jiangsonglin.fastbean.beans;

import com.jiangsonglin.fastbean.convert.Converter;
import com.jiangsonglin.fastbean.convert.ConverterChain;
import com.jiangsonglin.fastbean.copier.FastBeanCopier;
import com.jiangsonglin.fastbean.interfaces.JConsumer;
import com.jiangsonglin.fastbean.strategy.FastBeanStrategy;
import com.jiangsonglin.fastbean.strategy.StrategyConstant;

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
    /**
     * 能设置NULL
     */
    private Integer setNullStrategy = null;
    /**
     * 存在值时能被覆盖
     */
    private Integer coverStrategy = null;

    /**
     * 深拷贝
     */
    private Integer deeCopyStrategy = null;

    /**
     * 局部策略
     */
    FastBeanStrategy partStrategy = null;

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
     * 能够被设置为NULL，默认不能。（不影响全局）
     * @return
     */
    public FastBeanCopierChain<S, T> canSetNull() {
        this.setNullStrategy = StrategyConstant.CAN_SET_NULL;
        return this;
    }
    /**
     * 能够被覆盖，默认不能。（不影响全局）
     * @return
     */
    public FastBeanCopierChain<S, T> canCover() {
        this.coverStrategy = StrategyConstant.CAN_SET_COVER;
        return this;
    }
    /**
     * 是否深拷贝。（不影响全局）
     * @param isDeep true 开启深拷贝
     * @return
     */
    public FastBeanCopierChain<S, T> isDeep(boolean isDeep) {
        this.deeCopyStrategy = isDeep ? StrategyConstant.DEEP_COPY : StrategyConstant.SHALLOW_COPY;
        return this;
    }
    /**
     * 策略。（不影响全局）
     * @param strategy 局部策略
     * @return
     */
    public FastBeanCopierChain<S, T> strategy(FastBeanStrategy strategy) {
        this.partStrategy = strategy;
        return this;
    }
    /**
     * 复制
     *
     * @return
     */
    public T copy() {
        if (srcObject == null) {
            throw new NullPointerException("srcObject cant not be null");
        }
        if (targetObject == null) {
            throw new NullPointerException("targetObject cant not be null");
        }
        FastBeanCopier fastBeanCopier = FastBeanCopierHelper.create(srcObject.getClass(), targetObject.getClass(), nameMappingWrapper, ignoreWrapper);
        // 局部自定义
        if (coverStrategy != null || setNullStrategy != null || deeCopyStrategy != null) {
            partStrategy = new FastBeanStrategy();
            if (coverStrategy != null) {
                partStrategy.setCoverStrategy(coverStrategy);
            }
            if (setNullStrategy != null) {
                partStrategy.setSetNullStrategy(setNullStrategy);
            }
            if (deeCopyStrategy != null) {
                partStrategy.setCopyStrategy(deeCopyStrategy);
            }
        }
        if (partConverterChain != null) {
            fastBeanCopier.copy(srcObject, targetObject, partStrategy, partConverterChain);
        } else if (!partConverterList.isEmpty()) {
            fastBeanCopier.copy(srcObject, targetObject, partStrategy, partConverterList.toArray(new Converter[0]));
        } else {
            fastBeanCopier.copy(srcObject, targetObject, partStrategy);
        }
        return targetObject;
    }
}
