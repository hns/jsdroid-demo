package org.rhindroid;

import org.mozilla.javascript.Function;

/**
 * This interface can be implemented by application classes to allow
 * callback functions to be registered for specific events.
 */
public interface CallbackHolder {

    /**
     * Register a callback function for the given event.
     * @param event the event name
     * @param callback the callback function
     */
    public void on(String event, final Function callback);
}
