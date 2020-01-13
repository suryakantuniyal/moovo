package in.innobins.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import in.innobins.customer.gcm.MessageReceiverService;

/**
 * Created by sasuke on 7/12/15.
 */
public class TrackMoovo extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient;
    private Activity context;
    public static GoogleMap mMap;
    private static boolean activityVisible = true;
    Location currentlocation;
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArrayLatLng = new JSONArray();
    JSONObject jsonLatLng = new JSONObject();
    JSONObject jsonresult = new JSONObject();
    MarkerOptions markerOptions;
    LatLng drivervurrlatlng;
    Marker marker;
    int i = 0, length = 10, moovoshowtime = 3000;
    CameraUpdate cameraUpdate;
    boolean togglemoovo = false, callmoovo = true, getdetails = true;
    TextView Orderid, Drivername, TruckName, Truckplate, Contact;
    ImageView Mymoovo, back_buttion_pressed;
    String orderId_str,driverId_str,fcmMesg;
    int fcmmsg;
    String OrderId;
    int time, distance;
    TextView remtime;

    private BroadcastReceiver statusReceiver;
    private IntentFilter mIntent;
    Sensor accelerometer;
    SensorManager sm;

    public static TrackMoovo instance;


    private String API_KEY = "NkHb13BxRBiZ0JSyxLbAU";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");


        /*Bundle extras = getIntent().getExtras();
        RemoteMessage msg = (RemoteMessage) extras.get("msg");

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
//        sm.registerListener((SensorEventListener) this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);*/

        SharedPreferences sharedPreferences = getSharedPreferences("MOOVO", MODE_PRIVATE);
        registerReceiver(broadcastReceiver, filter);
        setContentView(R.layout.trackmoovo);
        Orderid = (TextView) findViewById(R.id.orderid);
        Drivername = (TextView) findViewById(R.id.drivername);
        TruckName = (TextView) findViewById(R.id.truckname);
        Truckplate = (TextView) findViewById(R.id.plate);
        Contact = (TextView) findViewById(R.id.contact);
        Mymoovo = (ImageView) findViewById(R.id.mymoovo);

        instance = this;

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.PrimaryColorStatusbar));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        OrderId = getIntent().getStringExtra("orderid");
        driverId_str = getIntent().getStringExtra("driverId");
        //Log.d("ordrid",OrderId);
        Orderid.setText(OrderId);
//        Log.d("driverStr",driverId_str);


       /* AlertDialog alertDialog = new AlertDialog.Builder(TrackMoovo.this).create();
       *//* alertDialog.setTitle(getResources().getString(titleId));
        alertDialog.setMessage(getResources().getString(bodyId,bodyArg));*//*
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.finish();
                    }
                });
        alertDialog.show();
*/


       // remtime = (TextView) findViewById(R.id.remtime);
        back_buttion_pressed = (ImageView) findViewById(R.id.back_buttion);
       // remtime.setText(sharedPreferences.getInt("remtime", 0) + "\n min");
        drivervurrlatlng = new LatLng(getIntent().getDoubleExtra("latitude", 28.6129), getIntent().getDoubleExtra("longitude", 77.2293));
        Drivername.setText(getIntent().getStringExtra("driverName"));
        Truckplate.setText(getIntent().getStringExtra("truckNo"));
        TruckName.setText(getIntent().getStringExtra("truckname"));

        driverId_str = getIntent().getStringExtra("driverId");
        orderId_str  = getIntent().getStringExtra("orderId");
//        Log.d("driveridstr",orderId_str);

        setUpMapIfNeeded();
        buildGoogleApiClient();
        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = null;
                number = "tel:" + getIntent().getStringExtra("contact");
                Intent telephonecall = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                if (ActivityCompat.checkSelfPermission(TrackMoovo.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(telephonecall);
            }
        });
        Mymoovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(jsonLatLng.getDouble("lat"), jsonLatLng.getDouble("lng")), 16);
                    mMap.animateCamera(cameraUpdate);
                } catch (Exception e) {
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(getIntent().getDoubleExtra("latitude", 28.6129), getIntent().getDoubleExtra("longitude", 77.2293)), 16);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        });

        back_buttion_pressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back_arrowclicked();
            }
        });

/*
        if (!fcmMesg.equals(null)){

            final AlertDialog.Builder builder = new AlertDialog.Builder(TrackMoovo.this);
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater.inflate(R.layout.disconnected_network, null);
            builder.setView(view);
            Button Tryagain = (Button)view.findViewById(R.id.tryagain);
            builder.setCancelable(false);
            final Dialog dialog = builder.create();
            dialog.show();
        }
*/
    }


    public void getRating(String orderId,String driverId,String rating){
        this.orderId_str = orderId;
        this.driverId_str= driverId;

    }
    /*public static void getRating(String orderId,String driverId,String rating){

    }*/

    public void Getlatlng() {
        new trackMoovo().execute();
        final Handler handler = new Handler();
        final Timer timergetlatlng = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Log.d("moovololgetlatlng", "moovoget");
                            try {
                                if (jsonresult != null) {
                                    if (jsonresult.getJSONObject("Response").getString("trackStatus").equals("running")) {
                                        if (!togglemoovo)
                                            Log.d("moovololgetlatlng", "running and calling trackmoovo");
                                        new trackMoovo().execute();
                                    } else if (jsonresult.getJSONObject("Response").getString("trackStatus").equals("completed")) {
                                        Log.d("moovololgetlatlng", "completed");
                                        timergetlatlng.cancel();
                                        timergetlatlng.purge();
                                    }
                                } else {
                                    Log.d("moovololgetlatlng", "jsonnull");
                                    new trackMoovo().execute();
                                }
                            } catch (JSONException e) {
                                new trackMoovo().execute();
                            }
                        } catch (Exception e) {
                            new trackMoovo().execute();
                        }
                    }
                });
            }
        };
        timergetlatlng.schedule(doAsynchronousTask, 0, 10000);
    }

    public void showMoovo() {
        final Handler handler = new Handler();
        for (int j = 0; j < jsonArrayLatLng.length(); j++) {
            final int finalJ = j;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        jsonLatLng = jsonArrayLatLng.getJSONObject(finalJ);
                        marker.setPosition(new LatLng(jsonLatLng.getDouble("lat"), jsonLatLng.getDouble("lng")));
                        new calculatedistance().execute();
                    } catch (JSONException e) {
                        Log.d("jsonexception", e.toString());
                    }
                }
            }, 2000);
        }
//        final Timer timershowmoovo = new Timer();
//        TimerTask showMoovoTask = new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    Log.d("moovololshowmoovo", "showMoovo"+"toogle "+togglemoovo);
//                    try {
//                        if (jsonresult != null) {
//                            if (jsonresult.getJSONObject("Response").getString("trackStatus").equals("running")) {
//                                Log.d("moovololshowmoovo", "running" + " length " + jsonArrayLatLng.length() + " and i is " + i);
//                                if (i < length) {
//                                    togglemoovo = true;
//                                    jsonLatLng = jsonArrayLatLng.getJSONObject(i++);
//                                    marker.setPosition(new LatLng(jsonLatLng.getDouble("lat"), jsonLatLng.getDouble("lng")));
//                                    Log.d("moovololshowmoovo2", String.valueOf(jsonLatLng.getDouble("lat")));
//                                    marker.setSnippet("Your Moovo is " + time + " minutes away");
//                                    marker.showInfoWindow();
//                                } else {
//                                    togglemoovo = false;
//                                }
//                            } else {
//                                timershowmoovo.cancel();
//                                timershowmoovo.purge();
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("orderId", OrderId);
//                                new GetFinalDetails().execute(jsonObject.toString());
//                            }
//                        }
//                    } catch (JSONException e) {
//                        new trackMoovo().execute();
//                    }
//                } catch (Exception e) {
//                    new trackMoovo().execute();
//                }
//            }
//        };
//        timershowmoovo.schedule(showMoovoTask, 0, 5000);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(TrackMoovo.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackMoovo.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//            latLng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
        try {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(drivervurrlatlng, 16);
            mMap.animateCamera(cameraUpdate);
            markerOptions = new MarkerOptions().position(drivervurrlatlng).title("Your Moovo is " + time / 60 + " min away").icon(BitmapDescriptorFactory.fromResource(R.drawable.trckfrnt));
            marker = mMap.addMarker(markerOptions);
            try {
                jsonObject.put("orderId",OrderId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Getlatlng();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMap != null)
            mMap = null;



        Confirm.activityPaused();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. This is exactly what is desired
            } else {
                // unexpected, re-throw
                throw e;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

/*
    public static void displayAlert(final Context context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent splashIntent = new Intent(context, TrackMoovo.class);
                    splashIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(splashIntent);
                }
            });
            */
/*builder.setTitle("" + context.getString(R.string.app_name));
            builder.setMessage(msg);*//*

            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/


    public class calculatedistance extends AsyncTask<String, Void, Void> {
        Double estdistance;
        JSONObject jsonObject = new JSONObject();
        String json;
        @Override
        protected Void doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            try {
                json = jsonParser.getJSONFromUrl("https://maps.googleapis.com/maps/api/directions/json?origin="+Confirm.startlat+","+Confirm.startlng+"&destination="+jsonLatLng.getDouble("lat")+","+jsonLatLng.getDouble("lng"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (json != null){
                try {
                    JSONObject jsonObject = new JSONObject(json);
                        time = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");
                        distance = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
//                        Toast.makeText(getApplicationContext(),"Your Moovo is "+time/60+" min away",Toast.LENGTH_SHORT).show();
//                        marker.showInfoWindow();
                       // remtime.setText(time/60+"\n min");
                } catch (JSONException e) {
                        e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Network problem", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;

    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           /* String action=MessageReceiverService.sendMessage();
            Log.d("mesgaction"," "+action);

            if (action.equals("orderCompleted")){
                Toast.makeText(getApplicationContext(),"ordercompleted",Toast.LENGTH_LONG).show();

                final AlertDialog.Builder builder = new AlertDialog.Builder(TrackMoovo.this);
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                View view = layoutInflater.inflate(R.layout.disconnected_network, null);
                builder.setView(view);
                Button Tryagain = (Button)view.findViewById(R.id.tryagain);
                builder.setCancelable(false);
                final Dialog dialog = builder.create();
                dialog.show();
            }*/


            boolean isConfirmVisible = TrackMoovo.isActivityVisible();
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                try {
                    if (isConfirmVisible) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if ((networkInfo != null) && (networkInfo.getState() == NetworkInfo.State.CONNECTED)) {
                        } else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(TrackMoovo.this);
                            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                            View view = layoutInflater.inflate(R.layout.disconnected_network, null);
                            builder.setView(view);
                            Button Tryagain = (Button)view.findViewById(R.id.tryagain);
                            builder.setCancelable(false);
                            final Dialog dialog = builder.create();
                            dialog.show();
                            Tryagain.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                    if (!UtilFunctions.isNetworkAvailable(getApplicationContext())) {
                                        dialog.show();
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
       /* LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("MyData")
        );*/
    }
    @Override
    protected void onStop() {
        super.onStop();
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

/*
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getExtras().getString("action");
            Log.d("actionty","hello"+type);
           */
/* if (type=="orderCompleted"){
                Toast.makeText(getApplication(), "order completed", Toast.LENGTH_LONG).show();
            }*//*

        }
    };
*/

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("life", "activity resumed");


        Confirm.activityResumed();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);


//        try {
//            jsonLatLng = jsonArrayLatLng.getJSONObject(i++);
//            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(jsonLatLng.getDouble("lat"), jsonLatLng.getDouble("lng")), 16);
//            mMap.animateCamera(cameraUpdate);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public class trackMoovo extends AsyncTask<String,Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            jsonresult = resTfulAPI.getJSONfromurl("api/customer/livetracking/byid/", "POST", jsonObject.toString());
            Log.e("Tracklive", String.valueOf(jsonresult));
            if (jsonresult != null)
                try {
                    Log.d("JSONRESULT", jsonresult.getJSONObject("Response").getJSONArray("trackingInfo").toString());
                    jsonArrayLatLng = jsonresult.getJSONObject("Response").getJSONArray("trackingInfo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if (jsonresult != null){
                    if (jsonresult.getJSONObject("Response").getString("trackStatus").equals("running")){
                        showMoovo();
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("orderId", OrderId);
                        jsonObject.put("key",API_KEY);
                        new GetFinalDetails().execute(jsonObject.toString());
                        Log.e("RatingResult", String.valueOf(jsonObject));
                    }
                    length = jsonArrayLatLng.length();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.cancel(true);
        }
    }

    public class GetFinalDetails extends AsyncTask<String,Void, Void>{
        JSONObject jsonresult = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        @Override
        protected Void doInBackground(String... strings) {
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            jsonresult = resTfulAPI.getJSONfromurl("api/order/completeddetails/","POST",strings[0]);
            Log.e("JsonResult", String.valueOf(jsonresult));
            Log.e("String[0]",strings[0]);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if (jsonresult != null){
                    jsonArray = jsonresult.getJSONArray("datasets");
                    Intent Mainactivityintent = new Intent(TrackMoovo.this, RatingActivity.class);
                    Mainactivityintent.putExtra("Details",jsonArray.toString());
                    startActivity(Mainactivityintent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.cancel(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(TrackMoovo.this,MainActivity.class);
        startActivity(backIntent);
        finish();
    }

    public void back_arrowclicked(){
        Intent intent_back = new Intent(TrackMoovo.this,MainActivity.class);
        startActivity(intent_back);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String action = MessageReceiverService.sendMessage();
        Log.d("brodmsg",action);
    }
}
