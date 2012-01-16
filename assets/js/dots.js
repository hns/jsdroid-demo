/*
 * Android app that paints dotted multi-touch traces using nice vivid colors.
 */

var {Color, Paint} = android.graphics;
var {Toast} = android.widget;

var MAXDOTS = 100;
var dots = [];
var colors = [Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN];

// called when the activity is created
activity.on("create", function(bundle) {
    var array = activity.getLastNonConfigurationInstance();
    if (array) {
        dots = array;
    } else {
        Toast.makeText(activity, "Touch the screen!", Toast.LENGTH_SHORT).show();
    }
});

// called to allow retaining state when the activity is stopped
activity.on("retain", function() {
    return dots;
});

// set touch event handler on the view
view.on("touch", function(event) {
    var count = Math.min(event.getPointerCount(), 4);
    var hist = event.getHistorySize();
    // process batched events
    for (var i = 0; i < count; i++) {
        for (var h = 0; h < hist; h++) {
            dots.push({
                x: event.getHistoricalX(i, h),
                y: event.getHistoricalY(i, h),
                color: colors[i]
            });
        }
    }
    // process last event
    for (i = 0; i < count; i++) {
        dots.push({
            x: event.getX(i),
            y: event.getY(i),
            color: colors[i]
        });
    }
    view.invalidate();
    return true;
});

// called when the view should be (re)drawn
view.on("draw", function (canvas) {
    canvas.drawColor(Color.BLACK);
    var paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setStyle(Paint.Style.FILL);
    paint.setAntiAlias(true);

    var length = dots.length;
    if (length > MAXDOTS + 20) {
        // truncate dots every once in a while
        dots = dots.slice(-MAXDOTS);
        length = MAXDOTS;
    }

    for (var i = Math.max(length - MAXDOTS, 0); i < length; i++) {
        paint.setColor(dots[i].color);
        paint.setAlpha(255 - (length - i) * 255 / MAXDOTS);
        canvas.drawCircle(dots[i].x, dots[i].y, 20, paint);
    }
});