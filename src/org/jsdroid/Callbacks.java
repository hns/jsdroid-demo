package org.jsdroid;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.Wrapper;

import java.util.EnumMap;

/**
 * A utility class for storing and invoking callback functions. Callbacks
 * are associated with event types represented by an enum type.
 *
 * @param <T> the enum specifying the supported event types
 */
public class Callbacks<T extends Enum<T>> {

    public static final Object UNHANDLED = new Object();

    private EnumMap<T, Function> map;

    private Callbacks(Class<T> clazz) {
        map = new EnumMap<T, Function>(clazz);
    }

    /**
     * Create a new Callbacks object for the given enum type.
     * @param c the enum class
     * @param <T> the enum type parameter
     * @return an empty Callbacks object
     */
    public static <T extends Enum<T>> Callbacks<T> create(Class<T> c) {
        return new Callbacks<T>(c);
    }

    /**
     * Returns true if this instance contains a callback for event
     * <code>event</code>.
     * @param event the event type
     * @return true if a callback is available for the given event
     */
    public boolean contains(T event) {
        return map.containsKey(event);
    }

    /**
     * Returns the callback function registered for event <code>event</code>.
     * @param event the event type
     * @return the callback function, or null
     */
    public Function get(T event) {
        return map.get(event);
    }

    /**
     * Registers a callback for event <code>event</code>.
     * @param event the event type
     * @param func the callback function
     */
    public void put(T event, Function func) {
        map.put(event, func);
    }

    /**
     * Removes all registered callbacks.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Invokes the callback registered for event <code>event</code>.
     * Returns the value returned by the function, or {@link #UNHANDLED}
     * if no callback is registered for the event.
     * @param event the event type
     * @param args the arguments
     * @return the return value, or <code>UNHANDLED</code> if no callback is
     * registered
     */
    public Object invoke(final T event, final Object... args) {
        if (!map.containsKey(event)) {
            return UNHANDLED;
        }
        Object result = ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(Context cx) {
                Function fn = map.get(event);
                Scriptable scope = fn.getParentScope();
                WrapFactory wrapFactory = cx.getWrapFactory();
                wrapFactory.setJavaPrimitiveWrap(false);
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
