package org.rhindroid;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScriptUtils.initScriptEngine(getAssets());
        setContentView(new ScriptedView(this));
    }

}
