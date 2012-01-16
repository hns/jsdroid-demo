package org.rhindroid;

import android.content.res.AssetManager;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ScriptBuilder {

    private static Scriptable global;
    private ScriptableObject scope;

    public ScriptBuilder() {
        if (global == null) {
            ContextFactory contextFactory = ContextFactory.getGlobal();
            global = (ScriptableObject) contextFactory.call(new ContextAction() {
                public Object run(org.mozilla.javascript.Context cx) {
                    return cx.initStandardObjects();
                }
            });
        }
        scope = new NativeObject();
        scope.setPrototype(global);
    }

    public ScriptBuilder defineAndroidPackage() {
        ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                // Define top-level android package for convenience
                Scriptable packages = (Scriptable) global.get("Packages", global);
                Object android = packages.get("android", packages);
                scope.defineProperty("android", android, ScriptableObject.DONTENUM);
                return null;
            }
        });
        return this;
    }

    public ScriptBuilder defineGlobal(final String name,
                                      final Object value) {
        ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                WrapFactory wrapFactory = cx.getWrapFactory();
                Object wrapped = wrapFactory.wrap(cx, scope, value, null);
                scope.put(name, scope, wrapped);
                return null;
            }
        });
        return this;
    }

    public ScriptBuilder evaluate(final String source,
                                  final AssetManager assets) {
        ContextFactory.getGlobal().call(new ContextAction() {
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
        return this;
    }

}
