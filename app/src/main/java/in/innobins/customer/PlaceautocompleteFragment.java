package in.innobins.customer;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sasuke on 22/9/15.
 */
public class PlaceautocompleteFragment extends Fragment {
    AutoCompleteTextView autoCompleteTextView;
    public static String TAG = "PlaceAutoFragment";
    ListView Predictionlist;
    TextView Result,textView;
    ArrayList<String> arrayList;
    LatLngBounds mBounds;
    LatLng Southwest, Northeast;
    GoogleApiClient mGoogleApiClient;
    AutocompleteFilter mAutocompleteFilter;
    ImageView bBack,Clear,Upgoogle,Downgoogle ;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDgaZ9p9yGm9fL3rpg-KbrXmdbDyHNYZW4";
    android.support.v7.app.ActionBar actionBar ;
    AppCompatActivity parentActivity ;
    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
//
//    @Override
//    public void onAttach(Activity activity) {
//        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
//        actionBar.hide();
//        super.onAttach(activity);
//        parentActivity=(AppCompatActivity)activity;
//    }

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
        final View Rootview = inflater.inflate(R.layout.placeautocomplete, parent, false);
        autoCompleteTextView = (AutoCompleteTextView)Rootview.findViewById(R.id.autocomplete_places);
        bBack =(ImageView)Rootview.findViewById(R.id.togglebutton);
        Upgoogle = (ImageView)Rootview.findViewById(R.id.upgoogle);
        Downgoogle = (ImageView)Rootview.findViewById(R.id.downgoogle);
        Clear = (ImageView)Rootview.findViewById(R.id.clear);
        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upgoogle.setVisibility(View.VISIBLE);
                Downgoogle.setVisibility(View.GONE);
                autoCompleteTextView.setText("");
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            }
        });
        if (HomeFragment.pickordrop){
            autoCompleteTextView.setText(HomeFragment.pickup.getText());
        }
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            }
        });
        Predictionlist = (ListView)Rootview.findViewById(R.id.prediction_list);
        Southwest = new LatLng(28.30, 76.40);
        Northeast = new LatLng(28.50, 77.40);
        mBounds = new LatLngBounds(Southwest,Northeast);
        buildGoogleApiClient();
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Upgoogle.setVisibility(View.GONE);
                Downgoogle.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                new Getplacesprediction().execute(s.toString());
            }
        });
        return Rootview;
    }


    private ResultCallback<AutocompletePredictionBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<AutocompletePredictionBuffer>() {
        @Override
        public void onResult(AutocompletePredictionBuffer autocompletePredictions) {
            if (!autocompletePredictions.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Error", "Place query did not complete. Error: " + autocompletePredictions.getStatus().toString());
                autocompletePredictions.release();
                return;
            }
            // Get the Place object from the buffer.
            final AutocompletePrediction place = autocompletePredictions.get(0);
            //Log.d("Placeprediction", place.getDescription());
            autocompletePredictions.release();
        }
    };

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    public class Getplacesprediction extends AsyncTask<String,Void, Void>{
        String json;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj;
        JSONArray predsJsonArray;
        @Override
        protected Void doInBackground(String... params) {
            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + API_KEY);
                sb.append("&components=country:in");
                sb.append("&input=" + URLEncoder.encode(params[0], "utf8"));
                json = jsonParser.getJSONFromUrl(sb.toString());
//                Log.d("json", json);
                if (json != null){
                    jsonObj= new JSONObject(json);
                    predsJsonArray = jsonObj.getJSONArray("predictions");
                    arrayList = new ArrayList(predsJsonArray.length());
                    for (int i = 0; i < predsJsonArray.length(); i++) {
                        System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                        System.out.println("==============================fragment==============================");
                        arrayList.add(predsJsonArray.getJSONObject(i).getString("description"));
                    }
                } else {
                    arrayList = null;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (arrayList != null){
                final PlacepredictionAdapter placepredictionAdapter = new PlacepredictionAdapter(getActivity(),arrayList);
                Predictionlist.setAdapter(placepredictionAdapter);
                Predictionlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                        Geocoder coder = new Geocoder(getActivity());
                        List<Address> address = null;
                        try {
                            address = coder.getFromLocationName(String.valueOf(parent.getItemAtPosition(position)), 5);
                            Log.d("address",address.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        assert address != null;
                        Address location = address.get(0);
                        if (HomeFragment.pickordrop) {
                            HomeFragment.pickup.setText(String.valueOf(parent.getItemAtPosition(position)));
                            HomeFragment.pickup.setGravity(Gravity.CENTER);
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            HomeFragment.startlatitude = location.getLatitude();
                            HomeFragment.startlongitude = location.getLongitude();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                            HomeFragment.mMap.moveCamera(cameraUpdate);
                        } else {
                            HomeFragment.dropoff.setText(String.valueOf(parent.getItemAtPosition(position)));
                            HomeFragment.dropoff.setGravity(Gravity.CENTER);
                            MainActivity mainActivity = new MainActivity();
                            mainActivity.Setoff = true;
                        }
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Error While Connecting to server.Check your Network and try again",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
