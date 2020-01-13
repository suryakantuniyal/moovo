package in.innobins.customer;

/**
 * Created by Harshit on 12-09-2015.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginSignUpActivity extends AppCompatActivity {
    FrameLayout frame ;
    boolean Activenetwork = true;
    LinearLayout alert;
    private static boolean activityVisible = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
        setContentView(R.layout.activity_login_sign_up);
        alert = (LinearLayout)findViewById(R.id.alert);
//        Continousservercheck();
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.PrimaryColorStatusbar));
        }
        frame = (FrameLayout)findViewById(R.id.frame);
        if (savedInstanceState != null) {
            return;
        }
        LoginFragment loginfrag = new LoginFragment();
        getFragmentManager().beginTransaction().add(R.id.frame,loginfrag).commit();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (Constants.blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
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
            boolean isConfirmVisible = Confirm.isActivityVisible();
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                try {
                    if (isConfirmVisible) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if ((networkInfo != null) && (networkInfo.getState() == NetworkInfo.State.CONNECTED)) {
                        } else {
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginSignUpActivity.this);
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
                // Ignore this exception. This is exactly what is desired
            } else {
                // unexpected, re-throw
                throw e;
            }
        }
    }

    public void Continousservercheck(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new IsNetworkActive().execute();
                handler.postDelayed(this, 1000);
            }
        }, 5000);
    }

    public class IsNetworkActive extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if(urlc.getResponseCode() == 200){
                    Activenetwork = true;
                } else {
                    Activenetwork = false;
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
            } else {
                alert.setVisibility(View.GONE);
            }
        }
    }

}

