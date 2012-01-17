/**
 * utils.js - various utility functions
 */

var {Intent} = android.content;
var {Main, R, ViewSource} = org.jsdroid.demo;
var {Toast} = android.widget;

// display a message
function alert(message) {
    Toast.makeText(activity, String(message), Toast.LENGTH_SHORT).show();
}

// called when an options menu item is selected
activity.on("select", function(menuItem) {
    if (menuItem.getItemId() === android.R.id.home) {
        // click on the app icon in action bar, go back to main action
        var intent = new Intent(activity, Main);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }
});

// activate "view source" button if available
var button = activity.findViewById(R.id.viewSource);
if (button) {
    button.setOnClickListener(function() {
        var intent = new Intent(activity, ViewSource);
        var name = activity.class.simpleName.toLowerCase();
        intent.putExtra("path", "js/" + name + ".js");
        activity.startActivity(intent);
    });
}