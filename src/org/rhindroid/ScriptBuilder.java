package org.rhindroid;

import android.content.res.AssetManager;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * A helper class for evaluating scripts. This encapsulates a scope object
 * and provides various methods to manipulate the scope before evaluating
 * one or more scripts.
 */
public class ScriptBuilder {

    private static Scriptable global;

    private ScriptableObject scope;
    private int languageVersion;

    /**
     * Create a ScriptBuilder with JavaScript version 1.8.
     */
    public ScriptBuilder() {
        this(Context.VERSION_1_8);
    }

    /**
     * Create a ScriptBuilder with the given JavaScript language version.
     * @param languageVersion the JS version
     */
    public ScriptBuilder(int languageVersion) {
        this.languageVersion = languageVersion;
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

    /**
     * Define a top level property for the <code>android</code> java package.
     * @return this ScriptBuilder
     */
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

    /**
     * Define a top level property with the given name and value.
     * @param name the name
     * @param value the value
     * @return this ScriptBuilder
     */
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

    /**
     * Evaluate a script
     * @param source the source path
     * @param assets the asset manager to retrieve the source
     * @return this ScriptBuilder
     */
    public ScriptBuilder evaluate(final String source,
                                  final AssetManager assets) {
        ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                cx.setOptimizationLevel(-1);
                cx.setLanguageVersion(languageVersion);
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
