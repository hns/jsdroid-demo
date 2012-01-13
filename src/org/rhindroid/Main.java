package org.rhindroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class Main extends Activity
{
    private ScriptedView view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = new ScriptedView(this);
        setContentView(view);
        init();
    }
    
    private void init() {
        final ProgressDialog loadingDialog = ProgressDialog.show(
                this, null, "Loading...", true, false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    ScriptUtils.initScriptEngine();
                    ScriptUtils.defineProperty("view", view);
                    ScriptUtils.evaluate("js/view.js", getAssets());
                } catch (Exception iox) {
                    throw new RuntimeException(iox);
                } finally {
                    loadingDialog.dismiss();
                }
            }
        }).start();
    }

}
