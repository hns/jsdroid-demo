package org.jsdroid.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import org.jsdroid.CallbackHolder;
import org.jsdroid.Callbacks;
import org.jsdroid.ScriptBuilder;
import org.mozilla.javascript.Function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

public class ViewSource extends Activity implements CallbackHolder {

    Callbacks<Events.Activity> callbacks = Callbacks.create(Events.Activity.class);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_source);
        String path = getIntent().getStringExtra("path");
        TextView text = (TextView)findViewById(R.id.view);
        text.setPadding(10, 10, 10, 10);
        try {
            text.setText(loadAsset(path));
        } catch (IOException iox) {
            Toast.makeText(this, "Error loading source code", Toast.LENGTH_SHORT).show();
        }
        new ScriptBuilder(getAssets())
                .defineGlobal("activity", this)
                .evaluate("js/utils.js");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        callbacks.invoke(Events.Activity.select, menuItem);
        return super.onOptionsItemSelected(menuItem);
    }

    public void on(String event, Function callback) {
        callbacks.on(event, callback);
    }

    public void on(Map<String, Function> eventMap) {
        callbacks.on(eventMap);
    }

    private String loadAsset(String path) throws IOException {
        char[] buffer = new char[1024];
        StringWriter writer = new StringWriter();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open(path)));
        try {
            int read;
            while ((read = reader.read(buffer)) > -1) {
                writer.write(buffer, 0, read);
            }
        } finally {
            reader.close();
        }
        return writer.toString();
    }
}
