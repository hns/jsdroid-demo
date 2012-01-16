package org.rhindroid;

import org.mozilla.javascript.Function;

import java.util.EnumMap;

public class Callbacks {

    // This really only exists for sparing users the repetitive generics.
    public static <T extends Enum<T>> EnumMap<T, Function> map(Class<T> c) {
        return new EnumMap<T, Function>(c);
    }

}
