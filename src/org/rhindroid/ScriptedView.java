package org.rhindroid;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;

public class ScriptedView extends View {

    enum Events {touch, draw}
    Function[] callbacks = new Function[Events.values().length];

    public ScriptedView(Context context) {
        super(context);
    }

    public void on(String type, final Function callback) {
        callbacks[Events.valueOf(type).ordinal()] = callback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Function callback = callbacks[Events.touch.ordinal()];
        if (callback != null) {
            return ScriptRuntime.toBoolean(ScriptUtils.invoke(callback, event));
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Function callback = callbacks[Events.draw.ordinal()];
        if (callback != null) {
            ScriptUtils.invoke(callback, canvas);
        } else {
            super.onDraw(canvas);
        }
    }
}
