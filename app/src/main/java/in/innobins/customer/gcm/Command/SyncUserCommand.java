package in.innobins.customer.gcm.Command;

import in.innobins.customer.gcm.GcmCommand;





import android.content.Context;
import android.util.Log;

import org.json.JSONObject;



/**
 * Created by Abhishek on 10/10/2015.
 */
public class SyncUserCommand extends GcmCommand {
    private static final String TAG ="SyncUserCommand";
    @Override
    public void execute(Context context, String type, JSONObject extraData) {
        Log.i(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
    }
}
