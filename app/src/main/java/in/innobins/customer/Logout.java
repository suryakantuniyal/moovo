package in.innobins.customer;

/**
 * Created by Harshit on 12-09-2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by Harshit on 12-09-2015.
 */
public class Logout {

    static Context mcontext;
    public static boolean logoutRequest(Context context) {
         mcontext = context;
        new Login().execute();
        SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(mcontext);
        Log.d("sharedprefreturn", String.valueOf(session.getBoolean("return",false)));
        return  true;
    }

    public static class Login extends AsyncTask<String, Void,Void>{
        SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(mcontext);
        //  SharedPreferences session = getBaseContext().getSharedPreferences(SharedPrefUtil.PREFERENCES, Context.MODE_PRIVATE);
//        String driverId = session.getString(SharedPrefUtil.KEY_DRIVER_ID, "Example");
        @Override
        protected Void doInBackground(String... params) {
//            JSONObject jObject = new JSONObject();
//            try {
//                jObject.put("driverId", driverId);
//            } catch (Exception e) {
//                Log.d("error", "in TrackingBodyMap");
//            }
//            String url = jObject.toString();
//            try {
//                url = URLEncoder.encode(url, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            url = "https://inno-100.appspot.com/api/customer/driver/logout/?data=" + url;
//            Log.d("url", url);
//            jObject = UtilFunctions.getJSONFromUrl(url);
//            Log.d("Jsobject",jObject.toString());
//            try {
//                jObject = jObject.getJSONObject("response");
//                if (jObject.getInt("confirmation") == 1) {
                Log.d("", "Logged out");
                SharedPreferences.Editor sessionEditor = session.edit();
                sessionEditor.putString(SharedPrefUtil.KEY_USERNAME, "");
                sessionEditor.putString(SharedPrefUtil.KEY_USER_EMAIL,"");
                sessionEditor.putString(SharedPrefUtil.KEY_USER_PHONE,"");
                sessionEditor.putBoolean(SharedPrefUtil.KEY_LOGGED_IN, false);
                sessionEditor.putString(SharedPrefUtil.KEY_APPLICATION_STATE, Constants.STATE_LOGGED_OUT);
                sessionEditor.putBoolean("login", false);
                sessionEditor.apply();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            flag = false;
            return null;
        }
    }
}