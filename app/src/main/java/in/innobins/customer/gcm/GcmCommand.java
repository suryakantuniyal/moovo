package in.innobins.customer.gcm;

import android.content.Context;


import org.json.JSONObject;

/**
 * Created by Abhishek on 10/10/2015.
 */
public abstract class GcmCommand {
    public abstract void execute(Context context, String type, JSONObject extraData);
}
