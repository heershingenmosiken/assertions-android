package com.heershingenmosiken.assertions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class Utils {

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

    public static <T> void filter(TreeSet<T> set, Predicate<T> filter) {
        List<T> itemsToRemove = new ArrayList<T>();
        for (T item : set) if (filter.test(item)) itemsToRemove.add(item);
        set.removeAll(itemsToRemove);
    }
}
