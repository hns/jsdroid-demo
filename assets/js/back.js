// generic code for handling app icon click in nested activities

var {Intent} = android.content;

// called when an options menu item is selected
activity.on("select", function(menuItem) {
    if (menuItem.getItemId() === android.R.id.home) {
        // click on the app icon in action bar, go back to main action
        var intent = new Intent(activity, org.rhindroid.Main);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }
});