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


public class Dots extends Activity implements CallbackHolder {

    Callbacks<Events.Activity> callbacks = Callbacks.create(Events.Activity.class);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new ScriptedView(this);
        setContentView(view);
        new ScriptBuilder(getAssets())
                .defineGlobal("activity", this)
                .defineGlobal("view", view)
                .evaluate("js/dots.js")
                .evaluate("js/back.js");
        callbacks.invoke(Events.Activity.create, savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        callbacks.invoke(Events.Activity.pause);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return callbacks.invoke(Events.Activity.retain);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        callbacks.invoke(Events.Activity.select, menuItem);
        return super.onOptionsItemSelected(menuItem);
    }

    public void on(String event, final Function callback) {
        callbacks.put(Events.Activity.valueOf(event), callback);
    }

}

class ScriptedView extends View implements CallbackHolder {

    Callbacks<Events.View> callbacks = Callbacks.create(Events.View.class);

    public ScriptedView(Context context) {
        super(context);
    }

    public void on(String event, final Function callback) {
        callbacks.put(Events.View.valueOf(event), callback);
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