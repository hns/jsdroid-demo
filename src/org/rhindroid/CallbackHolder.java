package org.rhindroid;

import org.mozilla.javascript.Function;

public interface CallbackHolder {

    public void on(String event, final Function callback);
}
