package in.innobins.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnClickListener, AdapterView.OnItemClickListener {
    public static String TAG = "HomeFragment";
    public static GoogleMap mMap;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    CameraUpdate cameraUpdate;
    int time, distance;
    JSONArray jsonPriceArray = new JSONArray();
    JSONArray driverlocation = new JSONArray();
    JSONObject jsonPriceObject = new JSONObject();
    GoogleApiClient googleApiClient;
    MarkerOptions markerOptionstrucks;
    Marker markertrucks;
    static TextView pickup, dropoff;
    /********** Declarations for Animation **********/
    static android.support.v7.widget.CardView dropOffCard, pickUpCard;
    public static LinearLayout Bookcontainer, Dropoffcontainer, Mapfragment, Marker, Toolbar, Farebreakcontainer;
    Integer TruckscontainerHeight, TruckscontainerHeightstatic;
    Integer BookcontainerHeight, BookcontainerHeightstatic;
    static Integer FarebreakcontainerHeight, FarebreakcontainerHeightstatic;
    static Integer ToolBarHeight, ToolBarHeightstatic;
    Integer Mapfragmentheight;
    static Integer DropoffcontainerHeight, DropoffcontainerHeightstatic;
    static boolean sliderIsUp, Down = false, farecardIsUp = false, setDropoff = false, heightset = false, showfirsttime = true;
    /************************End Of Animation Declarations**************/
    LocationRequest mLocationRequest;
    LocationSettingsRequest mLocationSettingsRequest;
    String backStateName;
    boolean Isdestroy = true;
    Timer timer = new Timer();
    public static String Timed;
    private static final String LOG_TAG = "GPA ";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "NkHb13BxRBiZ0JSyxLbAU";
    TextView Title, Eccotime, Acetime, Tatatime, Forteentime, Seventeentime, Championtime;
    String TruckName = "NONE", TruckDimension = null, TruckCapacity = null, request_api_key = API_KEY, Dropoffpoint;
    int TruckRate, TruckBaseFare, TransitRate, WaitingRate, ClosedbodyRate, FreewaitingTime, LabourRate;
    //    ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
//    ArrayList<String> startEndPlace = new ArrayList<String>();
    Context context;
    LatLng latLng;
    int Doubleclick = 0;
    JSONObject jsonMintime = new JSONObject();
    String bookingtime = null, bookingdate = null, bookingdatetime = null, estimateddistance;
    Button NOW, LATER, Dropoff;
    Double startlat, startlng, endlat, endlng;
    Location currentlocation;
    String currentaddress;
    public static Double startlatitude, startlongitude;
    //    AutoCompleteTextView pickup,dropoff;
    ImageView ECCO, ACE, TATA, FORTEEN, SEVENTEEN, CHAMPION;
    static boolean pickordrop = true;
    ProgressDialog pdbook;
    TextView tname, tbasefare, tcapcaity, tdimension, trate, textToolHeader;
    String jsonwaypoints, startaddr, endaddr;
    ImageView Getmylocation, Togglebutton;
    Dialog farebreakdialog = null;
    AppCompatActivity parentActivity;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LatLngBounds.Builder bounds;
    JSONArray jsonArrayTataace = new JSONArray();
    JSONArray jsonArrayChampion = new JSONArray();
    JSONArray jsonArrayEcco = new JSONArray();
    JSONArray jsonArraytata = new JSONArray();
    JSONArray jsonArrayseven = new JSONArray();
    JSONArray jsonArrayFour = new JSONArray();
    RelativeLayout maplayout;

   /* public static final int MULTIPLE_PERMISSIONS = 5;
    String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CALL_PHONE};
*/

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Activity activity) {


        super.onAttach(activity);
        Log.d("lifecycle", "On Fragment Attach");
        parentActivity = (AppCompatActivity) activity;


    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View Rootview = inflater.inflate(R.layout.homefragment, parent, false);

        int permission_call = PermissionChecker.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE);
        int permission_location = PermissionChecker.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION);

    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permission_call == PermissionChecker.PERMISSION_GRANTED && permission_location == PermissionChecker.PERMISSION_GRANTED ) {
               // loadMap();

                setUpMapIfNeeded();

            } else if (checkPermissions()) {
// checkPermission();

               // loadMap();
            }
        } else {
            setUpMapIfNeeded();
            //loadMap();
        }*/

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        request_api_key = sharedPreferences.getString("API_KEY", request_api_key);
        Title = (TextView) Rootview.findViewById(R.id.title);
        Title.setText("INNO CUSTOMER");
        Togglebutton = (ImageView) Rootview.findViewById(R.id.togglebutton);
        Togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(MainActivity.mDrawerPane);
            }
        });
        buildGoogleApiClient();
        Log.d("lifecycle", "On create view ");
        Marker = (LinearLayout) Rootview.findViewById(R.id.marker);
        Mapfragment = (LinearLayout) Rootview.findViewById(R.id.mapfragment);
        Toolbar = (LinearLayout) Rootview.findViewById(R.id.toolbar);
        Bookcontainer = (LinearLayout) Rootview.findViewById(R.id.book);
        dropOffCard = (CardView) Rootview.findViewById(R.id.dropOffCard);
        pickUpCard = (CardView) Rootview.findViewById(R.id.pickup);
        maplayout = (RelativeLayout) Rootview.findViewById(R.id.homefragment);
        //******************************************************************
        FragmentManager fragmentManager = getFragmentManager();
        pdbook = new ProgressDialog(getActivity());
        new getpricedetails().execute();
        //  GetDriverLocations();
//        Togglebutton = (ImageView)Rootview.findViewById(R.id.togglebutton);
//        Togglebutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.mDrawerLayout.openDrawer(MainActivity.mDrawerPane);
//            }
//        });

        pickup = (TextView) Rootview.findViewById(R.id.autocomplete_places_from);
        dropoff = (TextView) Rootview.findViewById(R.id.autocomplete_places_to);

        Getmylocation = (ImageView) Rootview.findViewById(R.id.getmylocation);
        Getmylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()), 16);
                    mMap.animateCamera(cameraUpdate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ECCO = (ImageView) Rootview.findViewById(R.id.ecco);
        NOW = (Button) Rootview.findViewById(R.id.now);
        LATER = (Button) Rootview.findViewById(R.id.later);
        ACE = (ImageView) Rootview.findViewById(R.id.ace);
        CHAMPION = (ImageView) Rootview.findViewById(R.id.champ);
        TATA = (ImageView) Rootview.findViewById(R.id.tata);
        FORTEEN = (ImageView) Rootview.findViewById(R.id.forteen);
        SEVENTEEN = (ImageView) Rootview.findViewById(R.id.seventeen);
        Eccotime = (TextView) Rootview.findViewById(R.id.eccotime);
        Acetime = (TextView) Rootview.findViewById(R.id.acetime);
        Championtime = (TextView) Rootview.findViewById(R.id.championtime);
        Tatatime = (TextView) Rootview.findViewById(R.id.tatatime);
        Forteentime = (TextView) Rootview.findViewById(R.id.forteentime);
        Seventeentime = (TextView) Rootview.findViewById(R.id.seventeentime);
        LayoutInflater inflater1 = LayoutInflater.from(getActivity());
        View view = inflater1.inflate(R.layout.customlist, null);
        pickup.setText("Getting Address.....");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater farebreakInflater = LayoutInflater.from(getActivity());
        final View farebreak = farebreakInflater.inflate(R.layout.faredetails, null);
        builder.setView(farebreak);
        tbasefare = (TextView) farebreak.findViewById(R.id.truckbasefare);
        tcapcaity = (TextView) farebreak.findViewById(R.id.capacity);
//        tdimension = (TextView)farebreak.findViewById(R.id.dimension);
        tname = (TextView) farebreak.findViewById(R.id.truckname);
        trate = (TextView) farebreak.findViewById(R.id.truckpostfare);
        farebreakdialog = builder.create();
        ECCO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("double", String.valueOf(Doubleclick));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putInt("remtime", jsonMintime.getInt("eccotime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.commit();
                if (Doubleclick == 0) {
                    farebreakdialog.show();
                }
                ShowavailableTrucks(jsonArrayEcco);
                Doubleclick = 0;
                Showtrucks(Doubleclick);
            }
        });
        CHAMPION.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("double", String.valueOf(Doubleclick));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putInt("remtime", jsonMintime.getInt("championtime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.commit();
                if (Doubleclick == 1) {
                    farebreakdialog.show();
                }
                ShowavailableTrucks(jsonArrayChampion);
                Doubleclick = 1;
                Showtrucks(Doubleclick);
            }
        });
        ACE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("double", String.valueOf(Doubleclick));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putInt("remtime", jsonMintime.getInt("acetime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.commit();
                if (Doubleclick == 2) {
                    farebreakdialog.show();
                }
                ShowavailableTrucks(jsonArrayTataace);
                Doubleclick = 2;
                Showtrucks(Doubleclick);
            }
        });

        TATA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("double", String.valueOf(Doubleclick));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putInt("remtime", jsonMintime.getInt("tatatime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.commit();
                if (Doubleclick == 3) {
                    farebreakdialog.show();
                }
                ShowavailableTrucks(jsonArraytata);
                Doubleclick = 3;
                Showtrucks(Doubleclick);
            }
        });
        FORTEEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("double", String.valueOf(Doubleclick));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putInt("remtime", jsonMintime.getInt("forteenthtime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.commit();
                if (Doubleclick == 4) {
                    farebreakdialog.show();
                }
                ShowavailableTrucks(jsonArrayFour);
                Doubleclick = 4;
                Showtrucks(Doubleclick);
            }
        });
        SEVENTEEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("double", String.valueOf(Doubleclick));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putInt("remtime", jsonMintime.getInt("seventeenthtime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.commit();
                if (Doubleclick == 5) {
                    farebreakdialog.show();
                }
                ShowavailableTrucks(jsonArrayseven);
                Doubleclick = 5;
                Showtrucks(Doubleclick);
            }
        });
        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickordrop = true;
                Fragment fragment = new PlaceautocompleteFragment();

                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.nothing, R.anim.slide_down).add(R.id.mainContent, fragment, PlaceautocompleteFragment.TAG).addToBackStack(null).commit();
                Log.d("BackStack Prediction", String.valueOf(fm.getBackStackEntryCount()));
                MainActivity.CHILD_FRAGMENT_STATE = true;
            }
        });
        dropoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickordrop = false;
                Fragment fragment = new PlaceautocompleteFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.nothing, R.anim.slide_down).add(R.id.mainContent, fragment, PlaceautocompleteFragment.TAG).addToBackStack(null).commit();
                Log.d("BackStack Prediction", String.valueOf(fm.getBackStackEntryCount()));
                MainActivity.CHILD_FRAGMENT_STATE = true;
            }

        });

        AlertDialog.Builder datetime = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View datetimeview = layoutInflater.inflate(R.layout.datetimepicker, null);
        datetime.setView(datetimeview);
        final Calendar today = Calendar.getInstance();
        bookingdate = today.get(Calendar.YEAR) + "/" + ((int) today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH);
        bookingtime = today.getTime().getHours() + ":" + today.getTime().getMinutes();
        bookingdatetime = bookingdate + " " + bookingtime;
        Log.d("time", today.get(Calendar.YEAR) + " " + ((int) today.get(Calendar.MONTH) + 1) + " " + today.get(Calendar.DAY_OF_MONTH) + " " + today.getTime().getHours() + " " + today.getTime().getMinutes());
        DatePicker datepicker = (DatePicker) datetimeview.findViewById(R.id.datePicker);
        datepicker.init(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Log.d("datechanged", (year + "/" + monthOfYear + "/" + dayOfMonth).toString());
                        bookingdate = (year + "/" + monthOfYear + "/" + dayOfMonth).toString();
                    }
                });
        TimePicker timepicker = (TimePicker) datetimeview.findViewById(R.id.timePicker);
        timepicker.setEnabled(true);
        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d("datechanged", (hourOfDay + ":" + minute).toString());
                bookingtime = (hourOfDay + ":" + minute).toString();
            }
        });
        datetime.setCancelable(false)
                .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookingdatetime = bookingdate + " " + bookingtime;
                        new calculatedistance().execute();
                        Log.d("bookingdatetime", bookingdatetime);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog datetimedialog = datetime.create();
        LATER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("LATERNOW", false);
                editor.apply();
                if (pickup.getText().toString().equals("") | pickup.getText().toString().equals("Getting Address.....")) {
                    Toast.makeText(getActivity(), "SET PICKUP POINT", Toast.LENGTH_SHORT).show();
                } else if (dropoff.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "SET DROPOFF POINT", Toast.LENGTH_SHORT).show();
                } else if (TruckName.equals("NONE")) {
                    Toast.makeText(getActivity(), "PLEASE CHOOSE A MINI-TRUCK", Toast.LENGTH_SHORT).show();
                } else {
                    startaddr = pickup.getText().toString();
                    endaddr = dropoff.getText().toString();
                    datetimedialog.show();
                }
            }
        });
        NOW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("LATERNOW", true);
                editor.apply();
                if (pickup.getText().toString().equals("") | pickup.getText().toString().equals("Getting Address.....")) {
                    Toast.makeText(getActivity(), "SET PICKUP POINT", Toast.LENGTH_SHORT).show();
                } else if (dropoff.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "SET DROPOFF POINT", Toast.LENGTH_SHORT).show();
                } else if (TruckName.equals("NONE")) {
                    Toast.makeText(getActivity(), "PLEASE CHOOSE A MINI-TRUCK", Toast.LENGTH_SHORT).show();
                } else {
                    startaddr = pickup.getText().toString();
                    endaddr = dropoff.getText().toString();
                    new calculatedistance().execute();
                }
            }
        });
        return Rootview;
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setUpMapIfNeeded();
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
*/

/*
    private boolean checkPermissions() {

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
                setUpMapIfNeeded();
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;

    }
*/

    public void Showtrucks(int value) {
        Log.d("showtruckscalled", String.valueOf(value));
        ECCO.setImageResource(R.drawable.ecconew);
        ACE.setImageResource(R.drawable.acenew);
        CHAMPION.setImageResource(R.drawable.championnew);
        TATA.setImageResource(R.drawable.tatanew);
        FORTEEN.setImageResource(R.drawable.forteennew);
        SEVENTEEN.setImageResource(R.drawable.seventeennew);
        if (value == 0) {
            ECCO.setImageResource(R.drawable.eecoactive);
        } else if (value == 1) {
            CHAMPION.setImageResource(R.drawable.champactive);
        } else if (value == 2) {
            ACE.setImageResource(R.drawable.aceactive);
        } else if (value == 3) {
            TATA.setImageResource(R.drawable.tataactive);
        } else if (value == 4) {
            FORTEEN.setImageResource(R.drawable.forteenactive);
        } else if (value == 5) {
            SEVENTEEN.setImageResource(R.drawable.seventeenactive);
        }
        try {
            jsonPriceObject = jsonPriceArray.getJSONObject(value);
            TruckName = jsonPriceObject.getString("vehicle");
            TruckCapacity = jsonPriceObject.getString("capacity");
            TruckDimension = jsonPriceObject.getString("dimensions");
            TransitRate = jsonPriceObject.getInt("TransitTimeRate");
            TruckBaseFare = jsonPriceObject.getInt("basefare");
            TruckRate = jsonPriceObject.getInt("rate1");
            WaitingRate = jsonPriceObject.getInt("waitingTimeRate");
            ClosedbodyRate = jsonPriceObject.getInt("closedBodyRate");
            FreewaitingTime = jsonPriceObject.getInt("waitingtime");
            LabourRate = jsonPriceObject.getInt("labourRate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        trate.setText("\u20B9 " + String.valueOf(TruckRate));
        tname.setText(TruckName);
        tcapcaity.setText(TruckDimension);
        tbasefare.setText("\u20B9 " + String.valueOf(TruckBaseFare));
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("lifecycle", "On View Created");
    }


    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("lifecycle", "On Activity Created");
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("lifecycle", "fragment resumed");
        pickordrop = true;
        Down = false;
        if (Doubleclick != 0)
            Doubleclick = 0;
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("lifecycle", "On stop");
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        try {
            latLng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
            startlatitude = currentlocation.getLatitude();
            startlongitude = currentlocation.getLongitude();
            Log.d("loc",startlatitude+","+startlongitude);
//            new Getdriverloc().execute();
            new setAddress().execute();
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
            mMap.moveCamera(cameraUpdate);
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    latLng = cameraPosition.target;
                    startlatitude = latLng.latitude;
                    startlongitude = latLng.longitude;
                    Log.d("lat-long",startlatitude+"-"+startlongitude);
                    new setAddress().execute();
                }
            });
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            bounds = new LatLngBounds.Builder();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("lifecycle","On pause");
        if (mMap != null)
            mMap = null;
        timer.cancel();
        timer.purge();
        Isdestroy = false;
    }

    @Override
    public void onDestroyView() {
        Log.d("lifecycle", "on View Destroy");
        Isdestroy = false;
        timer.cancel();
        timer.purge();
        try {
            pickordrop = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            super.onDestroyView();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {

    }

    public class setAddress extends AsyncTask<Void, Void, Void> {
        String addr;
        JSONObject jsonObject;
        @Override
        protected Void doInBackground(Void... params) {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            JSONParser jsonParser = new JSONParser();
            addr = jsonParser.getJSONFromUrl("http://maps.google.com/maps/api/geocode/json?address="+latitude+","+longitude+ "&sensor=false");
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            if (addr != null){
                try {
                    jsonObject = new JSONObject(addr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Log.d("address",jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
                    if (pickordrop) {
                        pickup.setText(jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
                        pickup.setGravity(Gravity.CENTER);
                    } else {
                        dropoff.setText(jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
                        dropoff.setGravity(Gravity.CENTER);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Error While Connecting to server.Check your Network and try again",Toast.LENGTH_LONG).show();
            }
        }
    }

    public class calculatedistance extends AsyncTask<String, Void, Void> {
        Double estdistance;
        JSONObject jsonObject = new JSONObject();
        String json;
        protected void onPreExecute() {
            pdbook.setMessage("    Processing...");
            pdbook.show();
            pdbook.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            String start = null, endpoint = null;
            try {
                start = URLEncoder.encode(startaddr, "UTF-8");
                endpoint = URLEncoder.encode(endaddr.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            json = jsonParser.getJSONFromUrl("https://maps.googleapis.com/maps/api/directions/json?origin=" + start + "&destination=" + endpoint);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (json != null){
                jsonwaypoints = json;
//            Log.d("JSONdistance", json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    time = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");
                    distance = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
                    estimateddistance = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                    estdistance = Double.valueOf(distance / 1000);
                    Log.d("LOLWA", time + " " + distance + " " + estdistance);
                    startlat = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lat");
                    startlng = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lng");
                    endlat = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getDouble("lat");
                    endlng = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getDouble("lng");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObject.put("estimatedDistance", Math.round(estdistance * 10 / 10));
                    jsonObject.put("vehicle", TruckName);
                    jsonObject.put("duration", time);
                    jsonObject.put("key", request_api_key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new getPriceestimation().execute(jsonObject.toString());
            } else {
                Toast.makeText(getActivity(), "Network problem", Toast.LENGTH_SHORT).show();
                pdbook.dismiss();
            }
        }
    }

    public class getpricedetails extends AsyncTask<String, Void, Void> {
        JSONObject jsonresult;
        ProgressDialog pd = new ProgressDialog(getActivity());

        protected void onPreExecute() {
            pd.setMessage("   Loading...");
            pd.show();
            pd.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("key", request_api_key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonresult = resTfulAPI.getJSONfromurl("api/customer/price/", "POST", jsonObject.toString());
           Log.d("JSONRESULT", jsonresult.toString());


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            if (jsonresult != null){
                try {
                    if (jsonresult.getInt("success") == 1) {
                        jsonPriceArray = jsonresult.getJSONArray("datasets");
                        Log.d("array", jsonPriceArray.toString());
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                        if (sharedPreferences.getString("PriceArray", "lol").length() != jsonPriceArray.length()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("PriceArray", jsonPriceArray.toString());
                            editor.apply();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false)
                                .setMessage("Authentication Problem")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Logout.logoutRequest(getActivity())) {
                                            Intent loginSignUp = new Intent(getActivity(), in.innobins.customer.LoginSignUpActivity.class);
                                            startActivity(loginSignUp);
                                        }
                                        ;
                                        dialog.cancel();
                                    }
                                }).show();

                    }
                    Showtrucks(Doubleclick);
                    new Getdriverloc().execute();
                    GetDriverLocations();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
//                Toast.makeText(getActivity(), "Error While Connecting to server.Check your Network and try again",Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                try {
                    jsonPriceArray = new JSONArray(sharedPreferences.getString("PriceArray",""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Getdriverloc extends AsyncTask<String, Void, Void> {
        JSONObject jsonresult;
        JSONObject jsonObjectdriver = new JSONObject();
        @Override
        protected Void doInBackground(String... params) {
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            Calendar currenttime = Calendar.getInstance();
            Timed = currenttime.get(Calendar.YEAR) + "/" + ((int) currenttime.get(Calendar.MONTH) + 1) + "/" + currenttime.get(Calendar.DAY_OF_MONTH) + " " + currenttime.getTime().getHours() + ":" + (int) (currenttime.getTime().getMinutes() );
            Log.d("timed",Timed);
            String search_s = null;
            try {
                Log.d("lat-lng", startlatitude+" "+startlongitude);
                jsonObjectdriver.put("key", request_api_key);
                jsonObjectdriver.put("dateTime", Timed);
                jsonObjectdriver.put("latitude",startlatitude);
                jsonObjectdriver.put("longitude",startlongitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            try {
//                search_s = URLEncoder.encode(jsonObject.toString(), "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            Log.d("JSONOBJECTfordriver", jsonObjectdriver.toString());
            String complete_url = "api/driver/current/location/";
            jsonresult = resTfulAPI.getJSONfromurl(complete_url, "POST", jsonObjectdriver.toString());
            Log.d("jsonres", String.valueOf(jsonresult));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
                if (jsonresult != null && Isdestroy) {
                    Log.d("driverjsonresult", jsonresult.toString());
                    try {
                        if (jsonresult.getInt("success") ==1) {
                            driverlocation = jsonresult.getJSONArray("datasets");
                            Log.d("array", driverlocation.toString());
                            if (driverlocation.length() != 0) {
                                jsonArrayEcco = driverlocation.getJSONObject(0).getJSONArray("ecco");
                                jsonArrayChampion = driverlocation.getJSONObject(0).getJSONArray("champion");
                                jsonArrayFour = driverlocation.getJSONObject(0).getJSONArray("forteenth");
                                jsonArrayseven = driverlocation.getJSONObject(0).getJSONArray("seventeenth");
                                jsonArraytata = driverlocation.getJSONObject(0).getJSONArray("tata");
                                jsonArrayTataace = driverlocation.getJSONObject(0).getJSONArray("ace");
                                jsonMintime = jsonresult.getJSONObject("mintime");
                                Log.d("trucktime", String.valueOf(jsonMintime));
                                if (MainActivity.isActivityVisible() || showfirsttime) {
                                    if (jsonMintime.getInt("eccotime") != 99) {
                                        Eccotime.setText(jsonMintime.getInt("eccotime") + " min");
                                        Eccotime.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColorStatusbar));
                                    } else {
                                        Eccotime.setText("No Truck");
                                        Eccotime.setTextColor(Color.BLACK);
                                    }
                                    if (jsonMintime.getInt("acetime") != 99) {
                                        Acetime.setText(jsonMintime.getInt("acetime") + " min");
                                        Acetime.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColorStatusbar));
                                    } else {
                                        Acetime.setText("no Truck");
                                        Acetime.setTextColor(Color.BLACK);
                                    }
                                    if (jsonMintime.getInt("tatatime") != 99) {
                                        Tatatime.setText(jsonMintime.getInt("tatatime") + " min");
                                        Tatatime.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColorStatusbar));
                                    } else {
                                        Tatatime.setText("No Truck");
                                        Tatatime.setTextColor(Color.BLACK);
                                    }
                                    if (jsonMintime.getInt("championtime") != 99) {
                                        Championtime.setText(jsonMintime.getInt("championtime") + " min");
                                        Championtime.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColorStatusbar));
                                    } else {
                                        Championtime.setText("No Truck");
                                        Championtime.setTextColor(Color.BLACK);
                                    }
                                    if (jsonMintime.getInt("forteenthtime") != 99) {
                                        Forteentime.setText(jsonMintime.getInt("forteenthtime") + " min");
                                        Forteentime.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColorStatusbar));
                                    } else {
                                        Forteentime.setText("No Truck");
                                        Forteentime.setTextColor(Color.BLACK);
                                    }
                                    if (jsonMintime.getInt("seventeenthtime") != 99) {
                                        Seventeentime.setText(jsonMintime.getInt("seventeenthtime") + " min");
                                        Seventeentime.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColorStatusbar));
                                    } else {
                                        Seventeentime.setText("No Truck");
                                        Seventeentime.setTextColor(Color.BLACK);
                                    }
                                    if (Doubleclick == 0) {
                                        ShowavailableTrucks(jsonArrayEcco);
                                    } else if (Doubleclick == 1) {
                                        ShowavailableTrucks(jsonArrayChampion);
                                    } else if (Doubleclick == 2) {
                                        ShowavailableTrucks(jsonArrayTataace);
                                    } else if (Doubleclick == 3) {
                                        ShowavailableTrucks(jsonArraytata);
                                    } else if (Doubleclick == 4) {
                                        ShowavailableTrucks(jsonArrayFour);
                                    } else if (Doubleclick == 5) {
                                        ShowavailableTrucks(jsonArrayseven);
                                    }
                                }
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setCancelable(false)
                                    .setMessage("Authentication Problem")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Logout.logoutRequest(getActivity())) {
                                                Intent loginSignUp = new Intent(getActivity(), LoginSignUpActivity.class);
                                                startActivity(loginSignUp);
                                            }
                                            ;
                                            dialog.cancel();
                                        }
                                    }).show();

                        }
                    } catch (JSONException e) {
                        Log.d("Error", e.toString());
                    }
                } else if (jsonresult != null){

                } else {
//                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public class getPriceestimation extends AsyncTask<String, Void, Void> {
        JSONObject jsonresult;
        String search_s = null, complete_url = null;
        String json;
        int surcharge, estdisfare, estnetfare;

        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            jsonresult = new JSONObject();
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            complete_url = "api/customer/predict/price/";
            jsonresult = resTfulAPI.getJSONfromurl(complete_url, "POST", params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdbook.dismiss();
            try {
                if (jsonresult != null){
                    if (jsonresult.getInt("success") == 1) {
                        Log.d("yooooooooooo", jsonresult.toString());
                        jsonresult = jsonresult.getJSONArray("datasets").getJSONObject(0);
                        estdisfare = jsonresult.getInt("estimatedDistFare");
                        estnetfare = jsonresult.getInt("estimatedNetFare");
                        surcharge = jsonresult.getInt("estimatedGoogleTraficSurcharge");
                        Bundle bundle = new Bundle();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("from", pickup.getText().toString());
                            jsonObject.put("to", dropoff.getText().toString());
                            jsonObject.put("estnetfare", estnetfare);
                            jsonObject.put("estdistfare", estdisfare);
                            jsonObject.put("surcharge", surcharge);
                            jsonObject.put("estimateddist", estimateddistance);
                            jsonObject.put("datetime", bookingdatetime);
                            String dat=jsonObject.getString("datetime");
                            Log.d("date",dat);
                            jsonObject.put("truckname", TruckName);
                            jsonObject.put("jsonwaypoints", jsonwaypoints);
                            jsonObject.put("startlat", startlat);
                            jsonObject.put("startlng", startlng);
                            jsonObject.put("endlat", endlat);
                            jsonObject.put("endlng", endlng);
                            jsonObject.put("basefare", TruckBaseFare);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bundle.putString("datasets", jsonObject.toString());
                        Intent ConfirmActivity = new Intent(getActivity().getApplicationContext(), Confirm.class);
                        ConfirmActivity.putExtra("datasets", jsonObject.toString());
//                        Truckscontainer.setVisibility(View.VISIBLE);
                        getActivity().finish();
                        timer.cancel();
                        timer.purge();
                        showfirsttime = false;
                        startActivity(ConfirmActivity);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false)
                                .setMessage("Authentication Problem")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Logout.logoutRequest(getActivity())) {
                                            Intent loginSignUp = new Intent(getActivity(), in.innobins.customer.LoginSignUpActivity.class);
                                            startActivity(loginSignUp);
                                        }
                                        ;
                                        dialog.cancel();
                                    }
                                }).show();

                    }
                } else {
//                    Toast.makeText(getActivity(),"Server Error", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                    JSONArray jsonArray = new JSONArray(sharedPreferences.getString("PriceArray",""));
                    if (distance <= 3){
                        estdisfare = jsonArray.getJSONObject(Doubleclick).getInt("basefare");
                    } else {
                        estdisfare = jsonArray.getJSONObject(Doubleclick).getInt("basefare") + jsonArray.getJSONObject(Doubleclick).getInt("rate1");
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("from", pickup.getText().toString());
                        jsonObject.put("to", dropoff.getText().toString());
                        jsonObject.put("estnetfare", estdisfare + jsonArray.getJSONObject(Doubleclick).getInt("TransitTimeRate")*time/60);
                        jsonObject.put("estdistfare", estdisfare);
                        jsonObject.put("surcharge", jsonArray.getJSONObject(Doubleclick).getInt("TransitTimeRate")*time/60);
                        jsonObject.put("estimateddist", estimateddistance);
                        jsonObject.put("datetime", bookingdatetime);
                        jsonObject.put("truckname", TruckName);
                        jsonObject.put("jsonwaypoints", jsonwaypoints);
                        jsonObject.put("startlat", startlat);
                        jsonObject.put("startlng", startlng);
                        jsonObject.put("endlat", endlat);
                        jsonObject.put("endlng", endlng);
                        jsonObject.put("basefare", TruckBaseFare);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent ConfirmActivity = new Intent(getActivity().getApplicationContext(), Confirm.class);
                    ConfirmActivity.putExtra("datasets", jsonObject.toString());
                    getActivity().finish();
                    startActivity(ConfirmActivity);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void GetDriverLocations() {
        final Handler handler = new Handler();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try{
                    new Getdriverloc().execute();
                }catch (Exception e){
                e.printStackTrace();
                }
            }
        };
       timer.schedule(timerTask,0,15000);
    }

/*
    private TimerTask timerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUpMapIfNeeded();
                        new Getdriverloc().execute();
                    }
                });
            }
        };
    }
*/

    public void ShowavailableTrucks(final JSONArray jsonarrayt){
        Log.d("jsfortruc",jsonarrayt.toString()+"  "+jsonarrayt.length());
        mMap.clear();
        if (jsonarrayt.length() != 0 ) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < jsonarrayt.length(); i++) {
                try {
                    builder.include(new LatLng(jsonarrayt.getJSONObject(i).getDouble("latitude"), jsonarrayt.getJSONObject(i).getDouble("longitude")));
                    MarkerOptions Truckmarker = new MarkerOptions().position(new LatLng(jsonarrayt.getJSONObject(i).getDouble("latitude"), jsonarrayt.getJSONObject(i).getDouble("longitude")))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.trckfrnt));
                        mMap.addMarker(Truckmarker);
//                    markerOptionstrucks.position(new LatLng(jsonarrayt.getJSONObject(i).getDouble("latitude"), jsonarrayt.getJSONObject(i).getDouble("longitude")));
//                    markerOptionstrucks.icon(BitmapDescriptorFactory.fromResource(R.drawable.truckicon));
//                    mMap.addMarker(markerOptionstrucks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            builder.include(new LatLng(startlatitude,startlongitude));
            LatLngBounds bounds = builder.build();
            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu;
//            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            cu = CameraUpdateFactory.zoomTo(14);
            mMap.animateCamera(cu);
            Log.d("zoomofMap", String.valueOf(mMap.getMaxZoomLevel()));
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }
}