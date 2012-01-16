package org.jsdroid.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import org.jsdroid.CallbackHolder;
import org.jsdroid.Callbacks;
import org.jsdroid.ScriptBuilder;
import org.mozilla.javascript.Function;


public class Dots extends Activity implements CallbackHolder {

    Callbacks<Events.Activity> callbacks = Callbacks.create(Events.Activity.class);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dots);
        View view = findViewById(R.id.view);
        new ScriptBuilder(getAssets())
                .defineGlobal("activity", this)
                .defineGlobal("view", view)
                .evaluate("js/dots.js")
                .evaluate("js/back.js")
                .evaluate("js/viewSource.js");
        callbacks.invoke(Events.Activity.create, savedInstanceState);
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
