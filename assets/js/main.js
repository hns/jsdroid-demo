/**
 * Main activity showing a list of available demos
 */

var {ArrayAdapter} = android.widget;
var {Intent} = android.content;
var {Uri} = android.net;
var {simple_list_item_1} = android.R.layout;
var {Dots} = org.jsdroid.demo;

var items = ["Dots"];
var classes = [Dots];

activity.on("create", function(bundle) {
    var adapter = new ArrayAdapter(activity, simple_list_item_1, items);
    activity.setListAdapter(adapter);
});

activity.on("click", function(item) {
    if (classes[item]) {
        var intent = new Intent(activity, classes[item]);
        activity.startActivity(intent);
    }
});