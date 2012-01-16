var {ArrayAdapter} = android.widget;
var {Intent} = android.content;

var items = ["Dots"];
var classes = [org.rhindroid.Dots];

activity.on("create", function(bundle) {
    var adapter = new ArrayAdapter(activity,
            android.R.layout.simple_list_item_1,
            items);
    activity.setListAdapter(adapter);
});

activity.on("click", function(item) {
    var class = classes[item];
    if (class) {
        var intent = new Intent(activity, class);
        activity.startActivity(intent);
    }
});