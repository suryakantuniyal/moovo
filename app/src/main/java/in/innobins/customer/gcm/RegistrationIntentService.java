package in.innobins.customer.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/*import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.iid.InstanceID;*/
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import in.innobins.customer.SharedPrefUtil;

/**
 * Created by Abhishek on 10/9/2015.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    SharedPreferences sharedPreferences ;
    public RegistrationIntentService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"on handle in module ");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            //InstanceID instanceID = InstanceID.getInstance(this);
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("fcmtokenva",token);

          //  GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.e(TAG, "FCM Registration Token: " + token);
            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            //Subscribe to topic channels
             subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.

            //[END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(in.innobins.customer.gcm.ServerUtilities.KEY_TOKEN_SENT_TO_APP, false).apply();
        }

    }


  // To be updated in next version
    private void subscribeTopics(String token) throws IOException {
       /* GcmPubSub pubSub = GcmPubSub.getInstance(this);

            pubSub.subscribe(token, "/topics/moovoCustomer", null);*/

        FirebaseMessaging.getInstance().subscribeToTopic( token);
       // FirebaseMessaging.getInstance().unsubscribeToTopic("mytopic");

    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     *
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        new TokenRequest().execute(token);
    }

    private class TokenRequest extends AsyncTask<String,Void,Void> {
        boolean bool;

        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Void doInBackground(String... params) {
            bool=sendToken(params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            if(bool){
                sharedPreferences.edit().putBoolean(in.innobins.customer.gcm.ServerUtilities.KEY_TOKEN_SENT_TO_APP, true).apply();
            }

            else{

            }
        }

    }
    /**
     *.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     * and send the information to the application server
     * @param token The new token.
     *
     */
    private boolean sendToken(String token){
        JSONObject jObject2=new JSONObject();
        JSONObject jObject;
       // TODO : add key value pairs according to the need of the application and what should be sent to the server
        try{
            jObject2.put("userName", sharedPreferences.getString(SharedPrefUtil.KEY_USERNAME,""));
            jObject2.put("userPhone",sharedPreferences.getString(SharedPrefUtil.KEY_USER_PHONE,""));
            jObject2.put("fcmToken",token);
        }
        catch (Exception e){
            Log.d("error", "error in creating token Sending request ");
        }
        String url = jObject2.toString();
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "error in token sending while encoding url ");
        }
        url = in.innobins.customer.gcm.ServerUtilities.BASE_SERVER_URL+"/fcm/token/?data=" +url;
        Log.d(TAG, url);



        try{
            jObject= in.innobins.customer.gcm.ServerUtilities.getJSONFromUrl(url);
            Log.d("token response", jObject.toString());
            jObject=jObject.getJSONObject("response");
            if(jObject.getInt("confirmation")==1){
                Log.d(TAG, "Token successfully sent to server ");

                return true;

            }
            else{
                return false;
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


}

