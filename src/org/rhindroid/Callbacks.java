package org.rhindroid;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.Wrapper;

import java.util.EnumMap;

public class Callbacks<T extends Enum<T>> {

    public static final Object UNHANDLED = new Object();

    private EnumMap<T, Function> map;

    private Callbacks(Class<T> clazz) {
        map = new EnumMap<T, Function>(clazz);
    }

    public static <T extends Enum<T>> Callbacks<T> create(Class<T> c) {
        return new Callbacks<T>(c);
    }

    public boolean contains(T item) {
        return map.containsKey(item);
    }

    public Function get(T item) {
        return map.get(item);
    }

    public void put(T item, Function func) {
        map.put(item, func);
    }

    public Object invoke(final T item, final Object... args) {
        if (!map.containsKey(item)) {
            return UNHANDLED;
        }
        Object result = ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(Context cx) {
                Function fn = map.get(item);
                Scriptable scope = fn.getParentScope();
                WrapFactory wrapFactory = cx.getWrapFactory();
                for (int i = 0; i < args.length; i++) {
                    args[i] = wrapFactory.wrap(cx, scope, args[i], null);
                }
                return fn.call(cx, scope, scope, args);
            }
        });
        return unwrap(result);
    }

    private static Object unwrap(Object value) {
        if (value == Undefined.instance) {
            value = null;
        } else if (value instanceof Wrapper) {
            value = ((Wrapper)value).unwrap();
        }
        return value;
    }

}
