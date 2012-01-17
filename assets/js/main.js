/**
 * main.js - display a list of available demos
 */

var {ArrayAdapter} = android.widget;
var {Intent} = android.content;
var {Uri} = android.net;

var items = ["Dots", "Dialogs"];

activity.on("create", function(bundle) {
    var adapter = new ArrayAdapter(activity,
            android.R.layout.simple_list_item_1, items);
    activity.setListAdapter(adapter);
});

activity.on("click", function(item) {
    var class = org.jsdroid.demo[items[item]];
    var intent = new Intent(activity, class);
    activity.startActivity(intent);
});