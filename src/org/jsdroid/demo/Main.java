package org.jsdroid.demo;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import org.jsdroid.CallbackHolder;
import org.jsdroid.Callbacks;
import org.jsdroid.ScriptBuilder;
import org.mozilla.javascript.Function;

public class Main extends ListActivity implements CallbackHolder {

    Callbacks<Events.Activity> callbacks = Callbacks.create(Events.Activity.class);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new ScriptBuilder(getAssets())
                .defineGlobal("activity", this)
                .evaluate("js/utils.js")
                .evaluate("js/main.js");
        callbacks.invoke(Events.Activity.create, savedInstanceState);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        callbacks.invoke(Events.Activity.click, Integer.valueOf(position));
    }

    public void on(String event, Function callback) {
        callbacks.put(Events.Activity.valueOf(event), callback);
    }
}