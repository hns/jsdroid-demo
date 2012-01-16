package org.rhindroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

import java.util.Map;

public class Main extends Activity {

    enum OnActivity {create, pause, retain}
    enum OnView {touch, draw}

    Map<OnActivity,Function> callbacks = Callbacks.map(OnActivity.class);
    Scriptable scope;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new ScriptedView(this);
        setContentView(view);
        scope = ScriptUtils.createScope();
        ScriptUtils.defineProperty(scope, "activity", this);
        ScriptUtils.defineProperty(scope, "view", view);
        ScriptUtils.evaluate("js/view.js", scope, getAssets());
        Function callback = callbacks.get(OnActivity.create);
        if (callback != null) {
            ScriptUtils.invoke(scope, callback, savedInstanceState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Function callback = callbacks.get(OnActivity.pause);
        if (callback != null) {
            ScriptUtils.invoke(scope, callback);
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        Function callback = callbacks.get(OnActivity.retain);
        return (callback != null) ? ScriptUtils.invoke(scope, callback) : null;
    }

    public void on(String type, final Function callback) {
        callbacks.put(OnActivity.valueOf(type), callback);
    }

    public class ScriptedView extends View {

        Map<OnView,Function> callbacks = Callbacks.map(OnView.class);

        public ScriptedView(Context context) {
            super(context);
        }

        public void on(String type, final Function callback) {
            callbacks.put(OnView.valueOf(type), callback);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Function callback = callbacks.get(OnView.touch);
            if (callback != null) {
                return ScriptRuntime.toBoolean(ScriptUtils.invoke(scope, callback, event));
            } else {
                return super.onTouchEvent(event);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Function callback = callbacks.get(OnView.draw);
            if (callback != null) {
                ScriptUtils.invoke(scope, callback, canvas);
            } else {
                super.onDraw(canvas);
            }
        }
    }

}
