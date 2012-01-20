package org.jsdroid;

import android.content.res.AssetManager;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import static org.mozilla.javascript.ScriptableObject.DONTENUM;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

/**
 * A helper class for evaluating scripts. This encapsulates a scope object
 * and provides various methods to manipulate the scope before evaluating
 * one or more scripts.
 */
public class ScriptBuilder {

    private static ScriptableObject global;

    private AssetManager assets;
    private ScriptableObject scope;
    private int languageVersion;

    /**
     * Create a ScriptBuilder with JavaScript version 1.8.
     * @param assets the asset manager to retrieve the source
     */
    public ScriptBuilder(AssetManager assets) {
        this(assets, Context.VERSION_1_8);
    }

    /**
     * Create a ScriptBuilder with the given JavaScript language version.
     * @param assets the asset manager to retrieve the source
     * @param languageVersion the JS version
     */
    public ScriptBuilder(AssetManager assets, int languageVersion) {
        this.assets = assets;
        this.languageVersion = languageVersion;
        if (global == null) {
            ContextFactory.getGlobal().call(new ContextAction() {
                public Object run(org.mozilla.javascript.Context cx) {
                    global = cx.initStandardObjects();
                    // Define a top level shortcut for the android java package.
                    Scriptable packages = (Scriptable) global.get("Packages", global);
                    Object android = packages.get("android", packages);
                    global.defineProperty("android", android, ScriptableObject.DONTENUM);
                    return null;
                }
            });
        }
        scope = new NativeObject();
        scope.setPrototype(global);
    }

    /**
     * Define a top level property with the given name and value.
     * @param name the name
     * @param value the value
     * @return this ScriptBuilder
     */
    public ScriptBuilder define(final String name, final Object value) {
        ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                WrapFactory wrapFactory = cx.getWrapFactory();
                Object wrapped = wrapFactory.wrap(cx, global, value, null);
                scope.put(name, scope, wrapped);
                return null;
            }
        });
        return this;
    }

    /**
     * Define a top level event source with the given name and value, adding
     * a <code>on()</code> method to its prototype that registers callbacks
     * with the given event map.
     * @param name the name
     * @param value the value
     * @param events the event map
     * @return this ScriptBuilder
     */
    public ScriptBuilder defineEventSource(final String name,
                                            final Object value,
                                            final EventMap events) {
        ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                WrapFactory wrapFactory = cx.getWrapFactory();
                Scriptable wrapped = wrapFactory.wrapAsJavaObject(
                        cx, global, value, null);
                ScriptableObject proto = (ScriptableObject)cx.newObject(global);
                proto.defineProperty("on", new AddListener(events), DONTENUM);
                Scriptable wrappedEvents = wrapFactory.wrapAsJavaObject(
                        cx, global, events, null);
                proto.defineProperty("events", wrappedEvents, DONTENUM);
                wrapped.setPrototype(proto);
                scope.put(name, scope, wrapped);
                return null;
            }
        });
        return this;
    }

    /**
     * Evaluate a script
     * @param source the source path
     * @return this ScriptBuilder
     */
    public ScriptBuilder evaluate(final String source) {
        ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(org.mozilla.javascript.Context cx) {
                cx.setOptimizationLevel(-1);
                cx.setLanguageVersion(languageVersion);
                cx.getWrapFactory().setJavaPrimitiveWrap(false);
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

    class AddListener extends BaseFunction {

        private EventMap events;

        AddListener(EventMap events) {
            super(global, getFunctionPrototype(global));
            this.events = events;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            if (args.length == 2) {
                if (!(args[0] instanceof CharSequence)) {
                    throw Context.reportRuntimeError("Expected String as first argument to on()");
                }
                if (!(args[1] instanceof Function)) {
                    throw Context.reportRuntimeError("Expected Function as second argument to on()");
                }
                events.addListener(args[0].toString(), (Function)args[1]);
                return thisObj;
            } else if (args.length == 1) {
                if (!(args[0] instanceof Map)) {
                    throw Context.reportRuntimeError("Expected Map as argument to on()");
                }
                Map<?,?> map = (Map)args[0];
                for (Map.Entry entry : map.entrySet()) {
                    String key = entry.getKey().toString();
                    events.addListener(key, (Function)entry.getValue());
                }
                return thisObj;
            }
            throw Context.reportRuntimeError("Wrong number of arguments in on()");
        }
    }

}
