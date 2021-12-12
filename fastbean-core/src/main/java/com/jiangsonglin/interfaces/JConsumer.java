package com.jiangsonglin.interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface JConsumer<T> extends Consumer<T>, Serializable {
}
