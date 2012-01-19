package org.jsdroid.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import org.jsdroid.CallbackHolder;
import org.jsdroid.Callbacks;
import org.jsdroid.demo.Events;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;

public class ScriptedView extends View implements CallbackHolder {

    Callbacks<Events.View> callbacks = Callbacks.create(Events.View.class);

    public ScriptedView(Context context) {
        super(context);
    }

    public ScriptedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void on(String event, final Function callback) {
        callbacks.on(event, callback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Object result = callbacks.invoke(Events.View.touch, event);
        if (result != Callbacks.UNHANDLED) {
            return ScriptRuntime.toBoolean(result);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (callbacks.invoke(Events.View.draw, canvas) == Callbacks.UNHANDLED) {
            super.onDraw(canvas);
        }
    }
}
