package in.innobins.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sushant on 18/10/15.
 */
public class Confirm extends Activity implements OnMapReadyCallback {
    TextView truck,timedate,pick,drop,Addreq,Estdis,Estfare;
    String jsonwaypoints, pickup,dropoff,bookdate,vehicletruck,estdistance,basefare;
    int countlabour = 0,estnetfare,surcharge,estdistfare,additional=0,Closebody=0,Tarpoline=0;
    Button ConfirmBooking;
    LinearLayout Applycoupon,Additionreq,Faredetails;
    public static Double startlat,startlng,endlat,endlng;
    Dialog Coupondialog,Additionaldialog,Faredetailsdialog;
    GoogleMap mMap;
    LinearLayout alert;
    String request_api_key="API_KEY";
    private static LatLng startPoint;
    private static LatLng endPoint;
    JSONObject jsonorderid = new JSONObject();
    JSONObject jsonConfirmation = new JSONObject();
    JSONObject jsongetOrderid = new JSONObject();
    boolean Activenetwork = true;
    ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
    ArrayList<String> startEndPlace = new ArrayList<String>();
    private static boolean activityVisible = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.PrimaryColorStatusbar));
        }
        alert = (LinearLayout)findViewById(R.id.alert);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        request_api_key = sharedPreferences.getString("API_KEY",request_api_key);
        setContentView(R.layout.confirmfragment);
        truck = (TextView)findViewById(R.id.vehicle);
        timedate = (TextView)findViewById(R.id.datetime);
        pick = (TextView)findViewById(R.id.from);
        drop = (TextView)findViewById(R.id.to);
        Addreq = (TextView)findViewById(R.id.additionalrequirment);
        Estdis = (TextView)findViewById(R.id.estdis);
        Estfare = (TextView)findViewById(R.id.estfare);
        ConfirmBooking = (Button)findViewById(R.id.confirmbooking);
        Applycoupon = (LinearLayout)findViewById(R.id.applycouponlayout);
        Additionreq = (LinearLayout)findViewById(R.id.additionallayout);
        Faredetails = (LinearLayout)findViewById(R.id.faredetails);
        String datasets = getIntent().getStringExtra("datasets");
        Log.d("data",datasets);
        try {
            JSONObject jsonObject = new JSONObject(datasets);
            jsonwaypoints = jsonObject.getString("jsonwaypoints");
            pickup = jsonObject.getString("from");
            dropoff = jsonObject.getString("to");
            estdistance = jsonObject.getString("estimateddist");
            vehicletruck = jsonObject.getString("truckname");
            bookdate = jsonObject.getString("datetime");
           // bookdate = jsonObject.getString("2018\\/3\\/13 14:54");
            startlat = jsonObject.getDouble("startlat");
            startlng = jsonObject.getDouble("startlng");
            endlat = jsonObject.getDouble("endlat");
            endlng = jsonObject.getDouble("endlng");
            estdistfare = jsonObject.getInt("estdistfare");
            estnetfare = jsonObject.getInt("estnetfare");
            surcharge = jsonObject.getInt("surcharge");
            basefare = jsonObject.getString("basefare");
        } catch (JSONException e) {
            Log.d("Error jsonify datasets", e.toString());
        }
        truck.setText(vehicletruck);
        timedate.setText(bookdate);
        pick.setText(pickup);
        drop.setText(dropoff);
        Estfare.setText("\u20B9 " + String.valueOf(estnetfare));
        Estdis.setText(estdistance);
        Addreq.setText(String.valueOf(countlabour + Closebody + Tarpoline));
        MapFragment supportMapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        startPoint = new LatLng(startlat, startlng);
        endPoint = new LatLng(endlat, endlng);
        coordinates.add(startPoint);
        coordinates.add(endPoint);
        startEndPlace.add(pickup);
        startEndPlace.add(dropoff);
        Applycoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Confirm.this);
                LayoutInflater applyinfalter = LayoutInflater.from(Confirm.this);
                View Applycouponview = applyinfalter.inflate(R.layout.applycoupon, null);
                builder.setView(Applycouponview)
                        .setCancelable(true);
                Coupondialog = builder.create();
                Button Cancel, Apply;
                final EditText Couponcode;
                Cancel = (Button) Applycouponview.findViewById(R.id.cancel);
                Apply = (Button) Applycouponview.findViewById(R.id.apply);
                Couponcode = (EditText) Applycouponview.findViewById(R.id.couponcode);
                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Coupondialog.cancel();
                    }
                });
                Apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Couponcode.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Please enter a couponcode", Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("couponCode", Couponcode.getText().toString());
                                jsonObject.put("key",request_api_key);
                            } catch (JSONException e) {
                                Log.d("Error jsonify coupon", e.toString());
                            }
                            Coupondialog.cancel();
                            new ApplyCoupon().execute(jsonObject.toString());
                        }
                    }
                });
                Coupondialog.show();
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(Confirm.this);
        LayoutInflater additionalinflater = LayoutInflater.from(Confirm.this);
        final View addtitionalview = additionalinflater.inflate(R.layout.additionalrequirment,null);
        builder.setCancelable(false)
                .setView(addtitionalview);
        Additionaldialog = builder.create();
        final ImageView Addlabour,Sublabour,Closedbodycheck, Tarpolinecheck;
        final TextView Labour;
        final LinearLayout Done;
        Addlabour = (ImageView)addtitionalview.findViewById(R.id.addlabour);
        Sublabour = (ImageView)addtitionalview.findViewById(R.id.removelabour);
        Closedbodycheck = (ImageView)addtitionalview.findViewById(R.id.closedbody);
        Tarpolinecheck = (ImageView)addtitionalview.findViewById(R.id.tarpoline);
        Done = (LinearLayout)addtitionalview.findViewById(R.id.done);
        Labour = (TextView)addtitionalview.findViewById(R.id.labournumber);
        Additionreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Additionaldialog.show();
                final Boolean[] isclosedbody = {false},isTarpoline = {false};
                Labour.setText(String.valueOf(countlabour));
                Addlabour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (countlabour < 2) {
                            countlabour++;
                        } else {
                            Toast.makeText(getApplicationContext(), "Maximum 2 labour allowed", Toast.LENGTH_SHORT).show();
                        }
                        Labour.setText(String.valueOf(countlabour));
                    }
                });
                Sublabour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (countlabour > 0) {
                            countlabour--;
                        }
                        Labour.setText(String.valueOf(countlabour));
                    }
                });
                Closedbodycheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isclosedbody[0] = !isclosedbody[0];
                        if (isclosedbody[0]){
                            Closedbodycheck.setImageResource(R.drawable.ic_checkbox_marked);
                            Closebody = 1;
                        } else {
                            Closedbodycheck.setImageResource(R.drawable.ic_checkbox_blank_outline);
                            Closebody = 0;
                        }
                    }
                });
                Tarpolinecheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isTarpoline[0] = !isTarpoline[0];
                        if (isTarpoline[0]){
                            Tarpolinecheck.setImageResource(R.drawable.ic_checkbox_marked);
                            Tarpoline = 1;
                        } else {
                            Tarpolinecheck.setImageResource(R.drawable.ic_checkbox_blank_outline);
                            Tarpoline = 0;
                        }
                    }
                });
                Done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        additional = countlabour + Closebody + Tarpoline;
                        Addreq.setText(String.valueOf(countlabour + Closebody + Tarpoline));
                        Estfare.setText("\u20B9 "+String.valueOf(estnetfare+countlabour*300 + Closebody*300));
                        Additionaldialog.cancel();
                    }
                });
            }
        });
        Faredetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder Faredetailsbuilder = new AlertDialog.Builder(Confirm.this);
                LayoutInflater faredetailsinflater = LayoutInflater.from(Confirm.this);
                View Faredetailsview = faredetailsinflater.inflate(R.layout.farebreak, null);
                Faredetailsbuilder.setView(Faredetailsview)
                        .setCancelable(true);
                Faredetailsdialog = builder.create();
                Faredetailsbuilder.show();
                TextView Faretruck, Farebasefare, Faredist, Faresurcharge, Farelabour, Fareclossed, Farenet;
                Faretruck = (TextView)Faredetailsview.findViewById(R.id.vehicle);
                Farebasefare = (TextView)Faredetailsview.findViewById(R.id.basefare);
                Faredist = (TextView)Faredetailsview.findViewById(R.id.distancecharge);
                Faresurcharge = (TextView)Faredetailsview.findViewById(R.id.surcharge);
                Farelabour = (TextView)Faredetailsview.findViewById(R.id.labourcharge);
                Fareclossed = (TextView)Faredetailsview.findViewById(R.id.closedbodycharge);
                Farenet = (TextView)Faredetailsview.findViewById(R.id.estimatedfare);
                Faretruck.setText(truck.getText());
                Farelabour.setText("\u20B9 "+String.valueOf(300 * countlabour));
                Fareclossed.setText("\u20B9 "+String.valueOf(300*Closebody));
                Farenet.setText(String.valueOf(Estfare.getText()));
                Faresurcharge.setText("\u20B9 "+String.valueOf(surcharge));
                Farebasefare.setText("\u20B9 "+String.valueOf(basefare));
                Faredist.setText("\u20B9 "+String.valueOf(estdistfare));
            }
        });
        ConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Mainactivity = new Intent(getApplicationContext(), MainActivity.class);
                JSONObject jsonbook = new JSONObject();
                try {
                    jsonbook.put("key", request_api_key);
                    jsonbook.put("name",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(SharedPrefUtil.KEY_USERNAME, ""));
                    jsonbook.put("email",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(SharedPrefUtil.KEY_USER_EMAIL, ""));
                    jsonbook.put("pickup",pickup);
                    jsonbook.put("dropoff",dropoff);
                    jsonbook.put("extra",additional);
                    jsonbook.put("vehicle",truck.getText());
                    jsonbook.put("estimatedfare", String.valueOf(estnetfare));
                    jsonbook.put("estimateddistance",estdistance);
                    jsonbook.put("startlatitude",startlat);
                    jsonbook.put("startlongitude",startlng);
                    jsonbook.put("endlatitude",endlat);
                    jsonbook.put("endlongitude",endlng);
                    jsonbook.put("datetime",bookdate);
                    jsonbook.put("contact", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(SharedPrefUtil.KEY_USER_PHONE, ""));
                    jsonbook.put("ipaddress","App");
                } catch (JSONException e) {
                    Log.d("Error jsonify Orders", e.toString());
                }
                Log.d("string", jsonbook.toString());
                SharedPreferences sharedPreferences = getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                if (!sharedPreferences.getBoolean("LATERNOW",false)){
                    new bookMoovo().execute(jsonbook.toString());
                } else {
                    new SendBroadcastRequest().execute(jsonbook.toString());
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent tracking = new Intent(Confirm.this, MainActivity.class);
        Bundle extra = new Bundle();
        extra.putInt("FROM", new Integer(1));
        tracking.putExtras(extra);
        startActivity(tracking);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Route route = new Route();
        route.drawRoute(googleMap, getApplicationContext(), coordinates, startEndPlace, true, false,jsonwaypoints);
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

    public class ApplyCoupon extends AsyncTask<String,Void,Void> {
        JSONObject jsonresult = new JSONObject();
        String search_s=null,complete_url=null,message;
        ProgressDialog pd = new ProgressDialog(Confirm.this);
        protected void onPreExecute() {
            pd.setMessage(" Applying Coupon...");
            pd.show();
            pd.setCanceledOnTouchOutside(false);
        }
        @Override
        protected Void doInBackground(String... params) {
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            complete_url = "api/customer/order/coupon/";
            jsonresult = resTfulAPI.getJSONfromurl(complete_url, "POST",params[0]);
//            Log.d("APPLYRESULT", jsonresult.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            if (jsonresult != null){
                try {
                    if (jsonresult.getInt("success")==1){
                        message = jsonresult.getJSONObject("Response").getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(Confirm.this);
                        if (message.equals("Coupon Applied")){
                            builder.setTitle("Coupon Applied");
                            builder.setMessage("Coupon Applied Successfully");
                        } else if (message.equals("Invalid Coupon")){
                            builder.setTitle("Invalid Coupon");
                            builder.setMessage("Invalid Coupon Code Please Try again");
                        }
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        Dialog dialog = builder.create();
                        dialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Confirm.this);
                        builder.setCancelable(false)
                                .setMessage("Authentication Problem")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Logout.logoutRequest(Confirm.this)){
                                            Intent loginSignUp = new Intent(Confirm.this, in.innobins.customer.LoginSignUpActivity.class);
                                            startActivity(loginSignUp);
                                        };
                                        dialog.cancel();
                                    }
                                }).show();

                    }
                } catch (JSONException e) {
                    Log.d("error applycoupon", "Retrieving index error");
                }
            } else {
                Toast.makeText(Confirm.this,"Unable to connect server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class bookMoovo extends AsyncTask<String, Void, Void>{
        JSONObject jsonresult = new JSONObject();
        String search_s=null,complete_url=null,message=null;
        ProgressDialog pd = new ProgressDialog(Confirm.this);
        protected void onPreExecute() {
            pd.setMessage(" Booking Requesting....");
            pd.show();
            pd.setCanceledOnTouchOutside(false);
        }
        @Override
        protected Void doInBackground(String... params) {
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            complete_url = "api/customer/order/moovo/";
            jsonresult = resTfulAPI.getJSONfromurl(complete_url, "POST",params[0]);
            Log.e("confirmBook", String.valueOf(jsonresult));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            if (jsonresult != null){
                try {
                    if (jsonresult.getInt("success")==1){
                        Log.d("APPLYRESULT",jsonresult.toString());
                        message = jsonresult.getJSONObject("response").getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(Confirm.this);
                        LayoutInflater orderdetailsinflater = LayoutInflater.from(Confirm.this);
                        View ordredetailsview = orderdetailsinflater.inflate(R.layout.orderdetails,null);
                        builder.setView(ordredetailsview)
                                .setCancelable(false);
                        final Dialog orderdetailsdialog = builder.create();
                        TextView From, To, OrderId, Vehicle, Datetime, Estimateddistance, Estimatedfare, AdditReq;
                        Button Ok;
                        OrderId = (TextView)ordredetailsview.findViewById(R.id.orderid);
                        To = (TextView)ordredetailsview.findViewById(R.id.to);
                        From = (TextView)ordredetailsview.findViewById(R.id.from);
                        Vehicle = (TextView)ordredetailsview.findViewById(R.id.vehicle);
                        Estimateddistance = (TextView)ordredetailsview.findViewById(R.id.estimateddistance);
                        Estimatedfare = (TextView)ordredetailsview.findViewById(R.id.estimatedfare);
                        AdditReq = (TextView)ordredetailsview.findViewById(R.id.additionalrequirment);
                        Datetime = (TextView)ordredetailsview.findViewById(R.id.datetime);
                        Ok = (Button)ordredetailsview.findViewById(R.id.done);
//            if (message.equals())
                        try {
                            OrderId.setText(jsonresult.getJSONObject("response").getString("orderId"));
                            To.setText(dropoff);
                            From.setText(pickup);
                            Vehicle.setText(vehicletruck);
                            Estimateddistance.setText(estdistance);
                            Estimatedfare.setText(String.valueOf(Estfare.getText()));
                            AdditReq.setText(String.valueOf(additional));
                            Datetime.setText(bookdate);
                        } catch (JSONException e) {
                            Log.d("error bookorder","Retrieving index error");
                        }
                        Ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                orderdetailsdialog.cancel();
//                    Fragment fragment = new HomeFragment();
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();
                                Intent Mainactivity = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(Mainactivity);
                                finish();
                            }
                        });
                        if (message.equals("Order Placed")){
                            orderdetailsdialog.show();
                            Toast.makeText(getApplicationContext(), "Order Placed Successfully.Driver Will be Assigned 1 Hour Before the Booking Time", Toast.LENGTH_LONG).show();
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Confirm.this);
                            builder1.setCancelable(false)
                                    .setMessage("Booking Not completed")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Confirm.this);
                        builder.setCancelable(false)
                                .setMessage("Authentication Problem")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Logout.logoutRequest(Confirm.this)){
                                            Intent loginSignUp = new Intent(Confirm.this, in.innobins.customer.LoginSignUpActivity.class);
                                            startActivity(loginSignUp);
                                        };
                                        dialog.cancel();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    Log.d("error bookorder", "Retrieving index error");
                }
            } else {
                Toast.makeText(Confirm.this, "Error While Connecting to server.Check your Network and try again",Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SendBroadcastRequest extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... strings) {
            RESTfulAPI restfullapi = new RESTfulAPI();
            jsongetOrderid = restfullapi.getJSONfromurl("api/customer/order/moovo/bygcm/", "POST", strings[0]);
            Log.d("orderId", String.valueOf(jsongetOrderid));
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            final SharedPreferences sharedPreferences = getSharedPreferences("MOOVO", MODE_PRIVATE);
            final int[] count = {0};
            final ProgressDialog progressBar = new ProgressDialog(Confirm.this);
            progressBar.setMessage("Requesting......");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);
            progressBar.setCancelable(false);
            progressBar.show();
            progressBar.setProgress(count[0]);
            if (jsongetOrderid != null) {
                try {
                    if (jsongetOrderid.getJSONObject("response").getInt("confirmation") == 1) {
                        final String OrderId = jsongetOrderid.getJSONObject("response").getString("orderId");
                        Log.d("crash", OrderId);
                        jsonorderid.put("orderId", OrderId);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (count[0] < 100) {
                                        count[0] = count[0] + 4;
                                        Log.d("count", String.valueOf(count[0]));
                                        progressBar.setProgress(count[0]);
                                        progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                progressBar.dismiss();
                                                count[0] = 200;
                                            }
                                        });
                                        new CheckOrderConfirm().execute();
                                        if (jsonConfirmation != null){
                                            if (jsonConfirmation.getInt("confirmation") == 1) {
                                                SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(Confirm.this);

                                              /*  String action= MessageReceiverService.sendMessage();
                                                Log.d("mesgaction"," "+action);*/

                                                handler.removeCallbacks(this);
                                                Log.d("Confirmation", String.valueOf(jsonConfirmation.getInt("confirmation")));
                                                Intent loginSignUp = new Intent(Confirm.this, TrackMoovo.class);
                                                loginSignUp.putExtra("orderid",OrderId);
                                                loginSignUp.putExtra("contact",jsonConfirmation.getString("driverContact"));
                                                loginSignUp.putExtra("latitude",jsonConfirmation.getDouble("latitude"));
                                                loginSignUp.putExtra("longitude", jsonConfirmation.getDouble("longitude"));
                                                loginSignUp.putExtra("driverName",jsonConfirmation.getString("driverName"));
                                                loginSignUp.putExtra("truckNo",jsonConfirmation.getString("truckNo"));
                                                loginSignUp.putExtra("truckname", jsonConfirmation.getString("truckname"));
                                                loginSignUp.putExtra("driverId",jsonConfirmation.getString("driverId"));

                                                SharedPreferences.Editor sessionEditor = session.edit();
                                                sessionEditor.putString(SharedPrefUtil.KEY_ORDER_ID,OrderId);
                                                sessionEditor.putString(SharedPrefUtil.KEY_DRIVER_ID,jsonConfirmation.getString("driverId"));
                                                sessionEditor.apply();
                                                startActivity(loginSignUp);
                                                finish();
                                            } else {
                                                handler.postDelayed(this, 2000);
                                            }
                                        } else {
                                            handler.postDelayed(this, 2000);
                                        }
                                    }else if (count[0] == 200){
                                        handler.removeCallbacks(this);
                                    }
                                    else {
                                        progressBar.dismiss();
                                        final AlertDialog.Builder builddialog = new AlertDialog.Builder(Confirm.this);
                                        builddialog.setCancelable(false)
                                                .setMessage("Your Order has Not been Confirmed by Driver. Please try Again!")
                                                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent Mainactivity = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(Mainactivity);
                                                        finish();
                                                    }
                                                }).show();
                                    }
                                } catch (JSONException e) {
                                    handler.postDelayed(this, 2000);
                                }
                            }
                        }, 4000);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Confirm.this);
                        builder.setCancelable(false)
                                .setMessage("Driver not Available")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Logout.logoutRequest(Confirm.this)) {
                                            Intent loginSignUp = new Intent(Confirm.this, in.innobins.customer.MainActivity.class);
                                            startActivity(loginSignUp);
                                        }
                                        ;
                                        dialog.cancel();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                progressBar.dismiss();
                Toast.makeText(getApplicationContext(), "Network problem. Try Again", Toast.LENGTH_LONG).show();
            }
        }
    }
    public class CheckOrderConfirm extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            RESTfulAPI restfullapi = new RESTfulAPI();
            jsonConfirmation = restfullapi.getJSONfromurl("api/customer/order/assign/notice/status/","POST",jsonorderid.toString());
            if (jsonConfirmation != null){
                Log.d("result", jsonConfirmation.toString());
            }
            return null;
        }
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConfirmVisible = Confirm.isActivityVisible();
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                try {
                    if (isConfirmVisible) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if ((networkInfo != null) && (networkInfo.getState() == NetworkInfo.State.CONNECTED)) {
                        } else {
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Confirm.this);
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
                    Log.d("error checking Internet", e.toString());
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Confirm.activityResumed();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Confirm.activityPaused();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
            } else {
                throw e;
            }
        }
    }
}
