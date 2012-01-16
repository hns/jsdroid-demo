package org.rhindroid;

import android.content.res.AssetManager;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.Wrapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ScriptUtils {

    static ContextFactory contextFactory;
    static ScriptableObject global;

    public static void initScriptEngine() {
        contextFactory = new ContextFactory();
        global = (ScriptableObject) contextFactory.call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                ScriptableObject scope = cx.initStandardObjects();
                // Define top-level android package for convenience
                Scriptable packages = (Scriptable) scope.get("Packages", scope);
                Object android = packages.get("android", packages);
                scope.defineProperty("android", android, ScriptableObject.DONTENUM);
                return scope;
            }
        });
    }

    public static ScriptableObject createScope() {
        if (global == null) {
            initScriptEngine();
        }
        ScriptableObject scope = new NativeObject();
        scope.setPrototype(global);
        return scope;
    }

    public static void defineProperty(final Scriptable scope,
                                      final String name,
                                      final Object value) {
        contextFactory.call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                WrapFactory wrapFactory = cx.getWrapFactory();
                Object wrapped = wrapFactory.wrap(cx, scope, value, null);
                scope.put(name, scope, wrapped);
                return null;
            }
        });
    }

    public static Object evaluate(final String source,
                                  final Scriptable scope,
                                  final AssetManager assets) {
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

    public static Object invoke(final Scriptable scope,
                                final Function fn,
                                final Object... args) {
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
