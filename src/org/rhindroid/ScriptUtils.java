package org.rhindroid;

import android.content.res.AssetManager;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.Wrapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ScriptUtils {

    static ContextFactory contextFactory;
    static ScriptableObject scope;

    public static void initScriptEngine() {
        contextFactory = new ContextFactory();
        scope = (ScriptableObject) contextFactory.call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                return cx.initStandardObjects();
            }
        });
    }

    public static void defineProperty(final String name, final Object value) {
        contextFactory.call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                WrapFactory wrapFactory = cx.getWrapFactory();
                Object wrapped = wrapFactory.wrap(cx, scope, value, null);
                scope.defineProperty (name, wrapped, ScriptableObject.READONLY);
                return null;
            }
        });
    }

    public static Object evaluate(final String source, final AssetManager assets) {
        Object result = contextFactory.call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                cx.setOptimizationLevel(-1);
                cx.setLanguageVersion(org.mozilla.javascript.Context.VERSION_1_8);
                try {
                    Reader reader = new InputStreamReader(assets.open(source));
                    cx.evaluateReader(scope, reader, source, 0, null);
                } catch (IOException iox) {
                    throw new RuntimeException(iox);
                }
                return null;
            }
        });
        return unwrap(result);
    }

    public static Object invoke(final Function fn, final Object... args) {
        Object result = contextFactory.call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
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
