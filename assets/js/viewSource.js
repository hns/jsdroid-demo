/**
 * Set click listener on "View Source" button
 */

var {Intent} = android.content;
var {R, ViewSource} = org.jsdroid.demo;

var button = activity.findViewById(R.id.viewSource);

if (button) {
    button.setOnClickListener(function() {
        var intent = new Intent(activity, ViewSource);
        var name = activity.class.simpleName.toLowerCase();
        intent.putExtra("path", "js/" + name + ".js");
        activity.startActivity(intent);
    });
}