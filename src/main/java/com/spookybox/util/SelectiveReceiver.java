package com.spookybox.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SelectiveReceiver<T> {
    public final Consumer<T> mReceiver;
    public final Predicate<T> mPredicate;

    public SelectiveReceiver(Consumer<T> receiver, Predicate<T> predicate) {
        mReceiver = receiver;
        mPredicate = predicate;
    }
}
