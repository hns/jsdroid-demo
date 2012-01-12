var {Color, Paint} = Packages.android.graphics;

var MAXDOTS = 100;
var dots = [];
var colors = [Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN];

view.setOnTouchListener(function(view, event) {
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

function onDraw(canvas) {
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
}