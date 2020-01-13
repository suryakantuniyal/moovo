package in.innobins.customer.gcm.Command;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;


import in.innobins.customer.gcm.GcmCommand;

/**
 * Created by Abhishek on 10/10/2015.
 */
public class SyncCommand extends GcmCommand {
    private static final String TAG ="syncCommand";
    @Override
    public void execute(Context context, String type, JSONObject extraData) {
        Log.i(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
    }
}
