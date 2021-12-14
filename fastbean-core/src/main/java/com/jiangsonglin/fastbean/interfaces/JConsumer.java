package com.jiangsonglin.fastbean.interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface JConsumer<T> extends Consumer<T>, Serializable {
}
