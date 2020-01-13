package in.innobins.customer;

/**
 * Created by Harshit on 12-09-2015.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Abhishek on 8/2/2015.
 */
public class UtilFunctions {

    public static JSONObject getJSONFromUrl(String completeurl){
        InputStream is = null;
        JSONObject jsonObject=null;
        String jsonstring="";
        try {
            URL url = new URL(completeurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(15000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            is = new BufferedInputStream(urlConnection.getInputStream());
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            if(s.hasNext()){
                jsonstring= s.next();
            }
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            Log.d("error", "error in getjsonfromurl MalformedUrlexception");
        } catch (IOException e) {
            Log.d("error", "error in getjsonfromurl Ioexception");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            jsonObject = new JSONObject(jsonstring);
        } catch (JSONException e) {
            Log.d("error", "Json exception");
        }
        return jsonObject;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static void makeToast(Context context,String message,int length){
        Toast.makeText(context,message,length).show();
    }

    private boolean isGooglePlayServicesAvailable(Activity activity) {  //need to be tested
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getBaseContext());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show();
            return false;
        }
    }

    public static void call(String phoneNo,Context context) {
        try{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            // callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            callIntent.setData(Uri.parse("tel:" + phoneNo));
            context.startActivity(callIntent);
        }

        catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(context, "yourActivity is not founded", Toast.LENGTH_SHORT).show();
        }
    }

    public static void dialogConfirm(Context context,Activity activity,View.OnClickListener listenerReject,View.OnClickListener listenerConfirm) {
        final AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(context);
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        View rootView = activity.getLayoutInflater().inflate(R.layout.dialog_box, ll, false);
        alertDialogBuilder.setView(rootView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
//        Button button1 = (Button) rootView.findViewById(R.id.button1);  //Reject Button
//        Button button2 = (Button) rootView.findViewById(R.id.button2);   //Confirm Button
        TextView textview = (TextView) rootView.findViewById(R.id.title);
//        button2.setOnClickListener(listenerConfirm);
//        button1.setOnClickListener(listenerReject);

//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                alertDialog.dismiss();
//            }
//        });
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//
        alertDialog.show();

    }
    public static void saveState(Context context ){
        Log.i("","Saving State");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // SharedPreferences.Editor editor = TrackingActivity.context.getSharedPreferences(SharedPrefUtil.PREFERENCES,Context.MODE_PRIVATE).edit();
        editor.putString(SharedPrefUtil.KEY_APPLICATION_STATE,Constants.appState);
        editor.putString(SharedPrefUtil.KEY_DRIVER_ID,Constants.driverId);
        editor.putBoolean(SharedPrefUtil.KEY_ORDER_AVAILABILITY, Constants.orderAvailability);
        editor.putString(SharedPrefUtil.KEY_ORDER_FINAL_DISTANCE,Constants.finalDistance);
        editor.putString(SharedPrefUtil.KEY_ORDER_FINAL_FARE,Constants.finalFare);
        editor.putString(SharedPrefUtil.KEY_ORDER_ID,Constants.orderId);
        editor.putString(SharedPrefUtil.KEY_ORDER_NAME, Constants.orderName);
        editor.putString(SharedPrefUtil.KEY_ORDER_EMAIL, Constants.orderEmail);
        editor.putString(SharedPrefUtil.KEY_ORDER_PHONE_NUMBER, Constants.orderPhoneNumber);

        editor.putString(SharedPrefUtil.KEY_ORDER_DATETIME, Constants.orderDateTime);
        editor.putString(SharedPrefUtil.KEY_ORDER_ESTIMATED_DISTANCE,Constants.orderEstimatedDistance);
        editor.putString(SharedPrefUtil.KEY_ORDER_ESTIMATED_FARE, Constants.orderEstimatedFare);
        editor.putString(SharedPrefUtil.KEY_ORDER_FROM_ADDRESS, Constants.orderFromAddress);
        editor.putString(SharedPrefUtil.KEY_ORDER_TO_ADDRESS,Constants.orderToAddress);
        editor.putString(SharedPrefUtil.KEY_ORDER_START_LATITUDE,Constants.orderStartLatitude);
        editor.putString(SharedPrefUtil.KEY_ORDER_START_LONGITUDE,Constants.orderStartLongitude);
        editor.putString(SharedPrefUtil.KEY_ORDER_END_LATITUDE,Constants.orderEndLatitude);
        editor.putString(SharedPrefUtil.KEY_ORDER_END_LONGITUDE,Constants.orderEndLongitude);
        editor.putString(SharedPrefUtil.KEY_ORDER_LABOUR, Constants.orderLabour);


        if(Constants.appState.equals(Constants.STATE_REACHING) || Constants.appState.equals(Constants.STATE_RUNNING)) {
            editor.putLong(SharedPrefUtil.KEY_ORDER_REACHING_TIME_START, Constants.reachingTimeStart.longValue());
        }
        editor.putInt(SharedPrefUtil.KEY_LOADING_TIME, Constants.loadingTime);
        editor.putInt(SharedPrefUtil.KEY_WAITING_TIME, Constants.waitingTime);
        editor.putInt(SharedPrefUtil.KEY_UNLOADING_TIME, Constants.unloadingTime);

        editor.commit();

    }
    public static void restoreState(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //SharedPreferences sharedPreferences = contgetSharedPreferences(SharedPrefUtil.PREFERENCES, Context.MODE_PRIVATE);
        Log.i("","restoring state "+sharedPreferences.getString(SharedPrefUtil.KEY_APPLICATION_STATE,"no state found using logged out"));
        Constants.appState=sharedPreferences.getString(SharedPrefUtil.KEY_APPLICATION_STATE,Constants.STATE_LOGGED_OUT);
        Constants.driverId=sharedPreferences.getString(SharedPrefUtil.KEY_DRIVER_ID, "");
        Constants.orderId=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_ID, "");
        Constants.orderName=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_NAME,"");
        Constants.orderEmail=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_EMAIL,"");
        Constants.orderPhoneNumber=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_PHONE_NUMBER, "");
        Constants.orderAvailability=sharedPreferences.getBoolean(SharedPrefUtil.KEY_ORDER_AVAILABILITY, false);
        Constants.orderDateTime=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_DATETIME, "");
        Constants.orderEstimatedDistance=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_ESTIMATED_DISTANCE, "");
        Constants.orderEstimatedFare=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_ESTIMATED_FARE, "");
        Constants.orderFromAddress=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_FROM_ADDRESS, "");
        Constants.orderToAddress=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_TO_ADDRESS, "");
        Constants.orderStartLatitude=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_START_LATITUDE,"");
        Constants.orderStartLongitude=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_START_LONGITUDE,"");
        Constants.orderEndLatitude=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_END_LATITUDE, "");
        Constants.orderEndLongitude=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_END_LONGITUDE,"");
        Constants.finalDistance=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_FINAL_DISTANCE, "");
        Constants.finalFare=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_FINAL_FARE, "");
        Constants.orderLabour=sharedPreferences.getString(SharedPrefUtil.KEY_ORDER_LABOUR, "");
        Constants.reachingTimeStart=sharedPreferences.getLong(SharedPrefUtil.KEY_ORDER_REACHING_TIME_START, 0);
        Constants.loadingTime=sharedPreferences.getInt(SharedPrefUtil.KEY_LOADING_TIME, 0);
        Constants.waitingTime=sharedPreferences.getInt(SharedPrefUtil.KEY_WAITING_TIME, 0);
        Constants.unloadingTime=sharedPreferences.getInt(SharedPrefUtil.KEY_UNLOADING_TIME, 0);





    }






}
