package org.rhindroid;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;

public class ScriptedView extends View {

    Function drawHandler;

    public ScriptedView(Context context) {
        super(context);
    }

    public void on(String type, final Function callback) {
        if (type.equals("touch")) {
            setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Object result = ScriptUtils.invoke(callback, motionEvent);
                    return ScriptRuntime.toBoolean(result);
                }
            });
        } else if (type.equals("draw")) {
            drawHandler = callback;
        } else {
            throw new IllegalArgumentException("Unknown event: " + type);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawHandler != null) {
            ScriptUtils.invoke(drawHandler, canvas);
        } else {
            super.onDraw(canvas);
        }
    }
}
