package  in.innobins.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LandingActivity extends Activity {
    FrameLayout frame ;
    LinearLayout alert;
    private static int SPLASH_TIME_OUT = 3000;
    Boolean flag = false ;
    boolean Activenetwork = true;
    FusedLocation fusedLocation;
    boolean GPS = false, networkedchecked = true;
    private ProgressBar progressBar;

    public static final int MULTIPLE_PERMISSIONS = 5;
    String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CALL_PHONE};


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_landing);
        UtilFunctions.restoreState(this);
        alert = (LinearLayout)findViewById(R.id.alert);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_cyclic);
        final Context context = this ;
        final Handler handler = new android.os.Handler();

        int permission_call = PermissionChecker.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);
        int permission_location = PermissionChecker.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permission_call == PermissionChecker.PERMISSION_GRANTED && permission_location == PermissionChecker.PERMISSION_GRANTED ) {
                // loadMap();

               // setUpMapIfNeeded();

            } else if (checkPermissions()) {
// checkPermission();

                // loadMap();
            }
        } else {
            //setUpMapIfNeeded();
            //loadMap();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( UtilFunctions.isNetworkAvailable(context)){
                    progressBar.setVisibility(View.VISIBLE);
//                    fusedLocation = new FusedLocation(LandingActivity.this,LandingActivity.this);
//                    if (!isGPSenabled(LandingActivity.this)){
//                        GPS = true;
//                        CheckGPS();
//                    } else {
//                        chooseBetweenLoginAndMainActivity();
//                    }
                    new IsNetworkActive().execute();
                }
                else {
                    if (!UtilFunctions.isNetworkAvailable(context)){
                        progressBar.setVisibility(View.VISIBLE);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
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
                                if (!UtilFunctions.isNetworkAvailable(context)){
                                    dialog.show();
                                } else {
//                                    fusedLocation = new FusedLocation(LandingActivity.this,LandingActivity.this);
//                                    if (!isGPSenabled(LandingActivity.this)){
//                                        GPS = true;
//                                        CheckGPS();
//                                    } else {
//                                        chooseBetweenLoginAndMainActivity();
//                                    }
                                    new IsNetworkActive().execute();
                                }
                            }
                        });
                    }
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // setUpMapIfNeeded();
// permissions granted.

                } else {
// String permissionss = "";
// for (String per : permissionsList) {
// permissionss += "\n" + per;
// }
// permissions list of don't granted permission
                }
                return;
            }
        }
    }


    private boolean checkPermissions() {

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
                //setUpMapIfNeeded();
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;

    }




    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (Constants.blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    public boolean isGPSenabled(Context context) {
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled=false;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}
        if (gps_enabled&&network_enabled){
            return true;
        }else {
            return false;
        }
    }
    public void CheckGPS(){
        final Handler handler = new Handler();
        Log.d("CheckGPS", "CHECKING GPS");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGPSenabled(LandingActivity.this)){
                    Toast.makeText(getApplicationContext(),"GPS is Disable. Please enbale it to Proceed", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(this,2000);
                } else {
                    chooseBetweenLoginAndMainActivity();
                }
            }
        },5000);
    }

    public void Continousservercheck(){
        final Handler handler = new Handler();
        Log.d("server", "Checking is server working or not");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Activenetwork){
                    new IsNetworkActive().execute();
                    handler.postDelayed(this,5000);
                } else {
                    chooseBetweenLoginAndMainActivity();
                }
            }
        },10000);
    }
//
//    public void showSettingsAlert(final Context context) {
//        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//        // Setting Dialog Title
//        alertDialog.setTitle("Location service disabled");
//        alertDialog.setCancelable(false);
//        // Setting Dialog Message
//        alertDialog
//                .setMessage("Please enable location services");
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Enable",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(
//                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        context.startActivity(intent);
//                        dialog.cancel();
////                        new IsNetworkActive().execute();
//                        chooseBetweenLoginAndMainActivity();
//                    }
//                });
//        alertDialog.show();
//    }

    void chooseBetweenLoginAndMainActivity() {
       // progressBar.setVisibility(View.VISIBLE);
        Boolean result =  Constants.appState.equals(Constants.STATE_LOGGED_OUT);
        if (!result){
            Intent tracking = new Intent(LandingActivity.this, MainActivity.class);
            Bundle extra = new Bundle();
            extra.putInt("FROM", new Integer(1));
            tracking.putExtras(extra);
            startActivity(tracking);
            finish();
           // progressBar.setVisibility(View.INVISIBLE);
        }
        else{
            frame = (FrameLayout)findViewById(R.id.frame);
            LoginFragment loginfrag = new LoginFragment();
            getFragmentManager().beginTransaction().replace(R.id.frame,loginfrag).commit();
           // progressBar.setVisibility(View.INVISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
    }

    public class IsNetworkActive extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("net", "Internet");
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if(urlc.getResponseCode() == 200){
                   // progressBar.setVisibility(View.INVISIBLE);
                    Activenetwork = true;
                } else {
                    Activenetwork = false;
                    progressBar.setVisibility(View.INVISIBLE);

                }
            } catch (IOException e) {
                Activenetwork = false;
                Log.d("error",e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!Activenetwork){
                alert.setVisibility(View.VISIBLE);
                if (networkedchecked){
                    networkedchecked = false;
                    Continousservercheck();
                }
            } else {
                alert.setVisibility(View.GONE);
                fusedLocation = new FusedLocation(LandingActivity.this,LandingActivity.this);
                if (!isGPSenabled(LandingActivity.this)){
                    GPS = true;
                    CheckGPS();
                } else {
                    chooseBetweenLoginAndMainActivity();
                }
            }
        }
    }
}