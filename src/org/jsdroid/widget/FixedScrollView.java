package org.jsdroid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

// See http://stackoverflow.com/a/2655740/466618 for why this is needed
// In a nutshell it seems you can't have vertical and horizontal scrolling
// in Android at the same time because ScrollViews steal each others events.

public class FixedScrollView extends ScrollView {

    private GestureDetector gestureDetector;

    public FixedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(new YScrollDetector());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Call super first because it does some hidden motion event handling
        boolean result = super.onInterceptTouchEvent(ev);
        //Now see if we are scrolling vertically with the custom gesture detector
        return gestureDetector.onTouchEvent(ev) && result;
    }

    // Return false if we're scrolling in the x direction
    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return Math.abs(distanceY) > Math.abs(distanceX);
        }
    }

}
