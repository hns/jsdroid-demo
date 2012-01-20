package org.jsdroid.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import org.jsdroid.EventMap;
import org.jsdroid.ScriptBuilder;
import org.jsdroid.demo.events.ActivityEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class ViewSource extends Activity {

    EventMap<ActivityEvent> events = EventMap.create(ActivityEvent.class);

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
                .defineEventSource("activity", this, events)
                .evaluate("js/utils.js");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        events.invoke(ActivityEvent.select, menuItem);
        return super.onOptionsItemSelected(menuItem);
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
