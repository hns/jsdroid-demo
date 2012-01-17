/**
 * dialogs.js - display different kinds of alert dialogs
 */

var {AlertDialog, ProgressDialog} = android.app;
var {DialogInterface} = android.content;
var {ArrayAdapter} = android.widget;
var {Handler} = android.os;

var SHORT_DIALOG = 0,
    LONG_DIALOG = 1,
    LIST_DIALOG = 2,
    PROGRESS_DIALOG = 3;

var items = [
    "Short OK/Cancel dialog",
    "Long OK/Cancel dialog",
    "List dialog",
    "Progress dialog"
];

var listItems = ["One", "Two", "Three", "Four", "Five"];

var progressDialog, progressHandler;

activity.on("create", function(bundle) {
    var adapter = new ArrayAdapter(activity,
            android.R.layout.simple_list_item_1, items);
    activity.setListAdapter(adapter);
});

activity.on("click", function(item) {
    activity.showDialog(item);

    // for progress dialog start a handler that increments progress
    if (item === PROGRESS_DIALOG) {
        progressDialog.progress = 0;
        progressHandler.sendEmptyMessageDelayed(0, 100);
    }
});

activity.on("dialog", function(item) {
    switch (item) {
        case SHORT_DIALOG:
            return new AlertDialog.Builder(activity)
                    .setTitle(items[item])
                    .setPositiveButton("OK", okHandler)
                    .setNegativeButton("Cancel", cancelHandler)
                    .create();
        case LONG_DIALOG:
            return new AlertDialog.Builder(activity)
                    .setTitle(items[item])
                    .setMessage(org.jsdroid.demo.R.string.long_text)
                    .setPositiveButton("OK", okHandler)
                    .setNegativeButton("Cancel", cancelHandler)
                    .create();
        case LIST_DIALOG:
            return new AlertDialog.Builder(activity)
                    .setTitle(items[item])
                    .setItems(listItems, listHandler)
                    .create();
        case PROGRESS_DIALOG:
            var d = progressDialog = new ProgressDialog(activity);
            d.setTitle(items[item]);
            d.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            d.setMax(100);
            d.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        cancelHandler);
            return d;
    }
    return null;
});

function okHandler(dialog, whichButton) {
    alert("You clicked OK!");
}

function cancelHandler(dialog, whichButton) {
    alert("You clicked Cancel!");
}

function listHandler(dialog, whichButton) {
    alert("You selected '" + listItems[whichButton] + "'");
}

progressHandler = new Handler(function(msg) {
    if (progressDialog.progress == 100) {
        progressDialog.dismiss();
    } else {
        progressDialog.incrementProgressBy(1);
        progressHandler.sendEmptyMessageDelayed(0, 50);
    }
    return true;
});
