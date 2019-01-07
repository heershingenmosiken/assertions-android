package com.heershingenmosiken.assertions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

public final class Utils {

    interface Predicate<T> {
        boolean test(T t);
    }

    interface Consumer<T> {
        void accept(T t);
    }

    public static <T> void forEach(Collection<T> collection, Consumer<T> consumer) {
        for (T item : collection) {
            consumer.accept(item);
        }
    }

    public static <T> void filter(Collection<T> queue, Predicate<T> filter) {
        List<T> itemsToRemove = new ArrayList<T>();
        for (T item : queue) if (filter.test(item)) itemsToRemove.add(item);
        queue.removeAll(itemsToRemove);
    }
}
