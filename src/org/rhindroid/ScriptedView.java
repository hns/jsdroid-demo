package org.rhindroid;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class ScriptedView extends View {

    enum Events {touch, draw}
    Function[] callbacks = new Function[Events.values().length];
    Scriptable scope;

    public ScriptedView(Context context) {
        super(context);
        scope = ScriptUtils.createScope();
        ScriptUtils.defineProperty(scope, "view", this);
        ScriptUtils.evaluate(scope, "js/view.js");
    }

    public void on(String type, final Function callback) {
        callbacks[Events.valueOf(type).ordinal()] = callback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Function callback = callbacks[Events.touch.ordinal()];
        if (callback != null) {
            return ScriptRuntime.toBoolean(ScriptUtils.invoke(scope, callback, event));
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Function callback = callbacks[Events.draw.ordinal()];
        if (callback != null) {
            ScriptUtils.invoke(scope, callback, canvas);
        } else {
            super.onDraw(canvas);
        }
    }
}
