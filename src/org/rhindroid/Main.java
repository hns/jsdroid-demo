package org.rhindroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;


public class Main extends Activity implements CallbackHolder {

    enum ActivityEvent {create, pause, retain, select}
    Callbacks<ActivityEvent> callbacks = Callbacks.create(ActivityEvent.class);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new ScriptedView(this);
        setContentView(view);
        new ScriptBuilder()
                .defineAndroidPackage()
                .defineGlobal("activity", this)
                .defineGlobal("view", view)
                .evaluate("js/view.js", getAssets());
        callbacks.invoke(ActivityEvent.create, savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        callbacks.invoke(ActivityEvent.pause);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return callbacks.invoke(ActivityEvent.retain);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        callbacks.invoke(ActivityEvent.select, menuItem);
        return super.onOptionsItemSelected(menuItem);
    }

    public void on(String event, final Function callback) {
        callbacks.put(ActivityEvent.valueOf(event), callback);
    }

}

class ScriptedView extends View implements CallbackHolder {

    enum ViewEvent {touch, draw}
    Callbacks<ViewEvent> callbacks = Callbacks.create(ViewEvent.class);

    public ScriptedView(Context context) {
        super(context);
    }

    public void on(String event, final Function callback) {
        callbacks.put(ViewEvent.valueOf(event), callback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Object result = callbacks.invoke(ViewEvent.touch, event);
        if (result != Callbacks.UNHANDLED) {
            return ScriptRuntime.toBoolean(result);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (callbacks.invoke(ViewEvent.draw, canvas) == Callbacks.UNHANDLED) {
            super.onDraw(canvas);
        }
    }
}