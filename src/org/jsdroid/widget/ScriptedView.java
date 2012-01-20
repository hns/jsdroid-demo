package org.jsdroid.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import org.jsdroid.EventMap;
import org.jsdroid.demo.events.ViewEvent;
import org.mozilla.javascript.ScriptRuntime;

public class ScriptedView extends View {

    public EventMap<ViewEvent> events = EventMap.create(ViewEvent.class);

    public ScriptedView(Context context) {
        super(context);
    }

    public ScriptedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Object result = events.invoke(ViewEvent.touch, event);
        if (result != EventMap.UNHANDLED) {
            return ScriptRuntime.toBoolean(result);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (events.invoke(ViewEvent.draw, canvas) == EventMap.UNHANDLED) {
            super.onDraw(canvas);
        }
    }
}
