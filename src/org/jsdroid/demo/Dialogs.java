package org.jsdroid.demo;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import org.jsdroid.CallbackHolder;
import org.jsdroid.Callbacks;
import org.jsdroid.ScriptBuilder;
import org.mozilla.javascript.Function;

public class Dialogs extends ListActivity implements CallbackHolder {

    Callbacks<Events.Activity> callbacks = Callbacks.create(Events.Activity.class);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs);
        new ScriptBuilder(getAssets())
                .defineGlobal("activity", this)
                .evaluate("js/utils.js")
                .evaluate("js/dialogs.js");
        callbacks.invoke(Events.Activity.create, savedInstanceState);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        callbacks.invoke(Events.Activity.click, Integer.valueOf(position));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return (Dialog) callbacks.invoke(Events.Activity.dialog, Integer.valueOf(id));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        callbacks.invoke(Events.Activity.select, menuItem);
        return super.onOptionsItemSelected(menuItem);
    }

    public void on(String event, Function callback) {
        callbacks.put(Events.Activity.valueOf(event), callback);
    }
}