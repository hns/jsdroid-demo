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


public class Main extends Activity {

    enum ActivityEvent {create, pause, retain, select}
    enum ViewEvent {touch, draw}

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

    public void on(String type, final Function callback) {
        callbacks.put(ActivityEvent.valueOf(type), callback);
    }

    public class ScriptedView extends View {

        Callbacks<ViewEvent> callbacks = Callbacks.create(ViewEvent.class);

        public ScriptedView(Context context) {
            super(context);
        }

        public void on(String type, final Function callback) {
            callbacks.put(ViewEvent.valueOf(type), callback);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (callbacks.contains(ViewEvent.touch)) {
                Object result = callbacks.invoke(ViewEvent.touch, event);
                return ScriptRuntime.toBoolean(result);
            } else {
                return super.onTouchEvent(event);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (callbacks.contains(ViewEvent.draw)) {
                callbacks.invoke(ViewEvent.draw, canvas);
            } else {
                super.onDraw(canvas);
            }
        }
    }

}
