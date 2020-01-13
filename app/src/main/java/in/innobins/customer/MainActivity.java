package in.innobins.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import in.innobins.customer.gcm.RegistrationIntentService;


public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    Boolean flag = false;
    ListView mDrawerList;
    int count = 0;
    static RelativeLayout mDrawerPane;
    static DrawerLayout mDrawerLayout;
    private ListView leftDrawerList;
    TextView text, stPhone;
    private static boolean activityVisible = true;
    private Toolbar toolbar;
    public static FragmentManager fragmentManager;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    static GoogleMap mMap;
    FragmentTransaction fragmentTransaction;
    boolean Activenetwork = true, Setoff = false;
    LinearLayout alert;
    String backStateName,FRAGMENT_STATE;
    static Boolean CHILD_FRAGMENT_STATE= false;
    private ActionBarDrawerToggle drawerToggle;
    public static int width,height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
        setContentView(R.layout.activity_main);
//        Continousservercheck();
        String stooredUserName = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(SharedPrefUtil.KEY_USERNAME, "");
        String stooredPhone = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(SharedPrefUtil.KEY_USER_PHONE, "");
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.PrimaryColorStatusbar));
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        SharedPreferences sharedPreferences = getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("running",false);
        editor.putBoolean("upcoming", false);
        editor.putBoolean("completed", false);
        editor.putBoolean("populatedup",false);
        editor.putBoolean("populatedrun",false);
        editor.putBoolean("populatedcom", false);
        editor.apply();
        text=(TextView) findViewById(R.id.userName);
        text.setText(stooredUserName);
        stPhone=(TextView) findViewById(R.id.desc);
        stPhone.setText(stooredPhone);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // TODO: Change GCM Working(point 1) when moving the following Registration to any other part
        Intent tokenSending = new Intent(this, RegistrationIntentService.class);
        startService(tokenSending);
        if (findViewById(R.id.mainContent) != null) {

            if (savedInstanceState != null) {
                return;
            }
            fragmentManager = getSupportFragmentManager();
            HomeFragment homefragment = new HomeFragment();
            backStateName = homefragment.getClass().getName();
            fragmentManager.beginTransaction().add(R.id.mainContent, homefragment,HomeFragment.TAG).commit();
            FRAGMENT_STATE = homefragment.getClass().getName();
            flag = true;
        }

        mNavItems.add(new NavItem("Profile",R.drawable.ic_settings_applications_black_48dp));
        mNavItems.add(new NavItem("Book A Truck",R.drawable.bookicon));
        mNavItems.add(new NavItem("My Bookings",R.drawable.ic_history_black_48dp));
        mNavItems.add(new NavItem("Farecards",R.drawable.farecardicon));
        mNavItems.add(new NavItem("Call Support",R.drawable.supporticon));
        mNavItems.add(new NavItem("About",R.drawable.about));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    String number = "tel:7838850643";
                    Intent telephonecall = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    startActivity(telephonecall);
                    mDrawerLayout.closeDrawer(mDrawerPane);
                } else if (position == 1 && flag) {
                    mDrawerLayout.closeDrawer(mDrawerPane);
                } else {
                    selectItemFromDrawer(position);
                }
            }
        });
    }

    public void openDrawer(){
        mDrawerLayout.openDrawer(mDrawerPane);
    }
    //*************************************//

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    //***************************************//

    private void selectItemFromDrawer(int position) {
        mDrawerLayout.closeDrawer(mDrawerPane);
        Fragment fragment =new  HomeFragment();
        Class fragmentClass = null;
        String fragmentTag = null;
        boolean bool;
        switch (position) {
            case 0:
                fragmentClass = ProfileFragment.class;
                fragmentTag  = ProfileFragment.TAG;
                flag = false;
                break;
            case 1:
                fragmentClass = HomeFragment.class;
                fragmentTag = HomeFragment.TAG;
                flag = true;
                break;
            case 2:
                fragmentClass =MybookingFragment.class;
                fragmentTag = MybookingFragment.TAG;
                flag =false;
                break;
            case 3:
                fragmentClass = FarecardFragment.class;
                fragmentTag = FarecardFragment.TAG;
                flag =false;
                break;
            case 5:

                fragmentClass = AboutFragment.class;
                fragmentTag = AboutFragment.TAG;
                flag = false;
                break;
            default:
                fragmentClass = HomeFragment.class;
                fragmentTag = HomeFragment.TAG;
                flag = true;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FRAGMENT_STATE = fragment.getClass().getName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.d("BACTSTACK",String.valueOf(fragmentManager.getBackStackEntryCount()));
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out).replace(R.id.mainContent, fragment,fragmentTag).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
         Log.e(TAG,"onbasck yess");
        Fragment fragmentVi = fragmentManager.findFragmentByTag(HomeFragment.TAG);
          if (fragmentVi != null && fragmentVi.isVisible()){
              Log.e(TAG, "Home fragment visible");
              if (HomeFragment.dropOffCard.getVisibility() == View.VISIBLE && HomeFragment.Bookcontainer.getVisibility() == View.VISIBLE){
                    Log.e(TAG,"Problematic condition");
                  HomeFragment.dropoff.setText("");
              } else {
                  super.onBackPressed();
              }
          }

         else{
              if (FRAGMENT_STATE.equals(backStateName) || fragmentManager.getBackStackEntryCount() != 0){
                  super.onBackPressed();
              } else {
                  Fragment fragment = new HomeFragment();
                  Class fragmentClass = HomeFragment.class;
                  try {
                      fragment = (Fragment) fragmentClass.newInstance();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
                  FragmentManager fragmentManager = getSupportFragmentManager();
                  Log.d("FRAGMENT STATE",FRAGMENT_STATE+" "+backStateName);
                  Log.d("BACTSTACKlol",String.valueOf(fragmentManager.getBackStackEntryCount()));
                  FRAGMENT_STATE = fragment.getClass().getName();
                  fragmentManager.beginTransaction().replace(R.id.mainContent, fragment,HomeFragment.TAG).commit();
              }

          }


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.e("back","yesssss"+1);
            onBackPressed();

        }
        return true;
    }



    public void Continousservercheck(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Server", "Checking is network active or not");
                new IsNetworkActive().execute();
                handler.postDelayed(this,2000);
            }
        },5000);
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
                Toast.makeText(getApplicationContext(),"Unable to connect Server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConfirmVisible = MainActivity.isActivityVisible();
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                try {
                    if (isConfirmVisible) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if ((networkInfo != null) && (networkInfo.getState() == NetworkInfo.State.CONNECTED)) {
                        } else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
        Log.d("life", "activity resumed");
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

}
