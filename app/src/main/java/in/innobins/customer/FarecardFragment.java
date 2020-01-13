package in.innobins.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sasuke on 7/9/15.
 */
public class FarecardFragment extends Fragment{
    ListView cityname;
    TextView city,Trucks,Prerate, Postrate,Transitrate, Waitingrate,Title;
    LinearLayout Citylist, Truckslist;
    String pricedetails;
    ImageView Togglebutton;
    public  static  String TAG = "FarecardFragment";
    private Toolbar toolbar;
    JSONArray jsonArray;
    JSONObject jsonObject;
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View Rootview = inflater.inflate(R.layout.farecardfragment, parent, false);
        Title = (TextView)Rootview.findViewById(R.id.title);
        Title.setText("fare card");
        Togglebutton = (ImageView)Rootview.findViewById(R.id.togglebutton);
        Togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(MainActivity.mDrawerPane);
            }
        });
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        pricedetails = sharedPreferences.getString("PriceArray","");
        Log.d("farearray",pricedetails);
        try {
            jsonArray = new JSONArray(pricedetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        city = (TextView)Rootview.findViewById(R.id.city);
        Trucks = (TextView)Rootview.findViewById(R.id.truck);
        Postrate = (TextView)Rootview.findViewById(R.id.postrate);
        Prerate = (TextView)Rootview.findViewById(R.id.prerate);
        Transitrate = (TextView)Rootview.findViewById(R.id.transitrate);
        Waitingrate = (TextView)Rootview.findViewById(R.id.waitingrate);
        Citylist = (LinearLayout)Rootview.findViewById(R.id.citylist);
        Truckslist = (LinearLayout)Rootview.findViewById(R.id.trucklist);

//        city.setText(jsonArray.getJSONObject(0).getString(""));
        try {
            Trucks.setText(jsonArray.getJSONObject(0).getString("vehicle"));
            Postrate.setText("\u20B9 "+String.valueOf(jsonArray.getJSONObject(0).getInt("rate1"))+"    per/kms");
            Prerate.setText("\u20B9 "+String.valueOf(jsonArray.getJSONObject(0).getInt("basefare")));
            Transitrate.setText("\u20B9 "+String.valueOf(jsonArray.getJSONObject(0).getInt("TransitTimeRate"))+"    per/min");
            Waitingrate.setText("\u20B9 "+String.valueOf(jsonArray.getJSONObject(0).getInt("waitingTimeRate"))+"    per/min");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder citybuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater cityInflater = LayoutInflater.from(getActivity());
        final View cityview = cityInflater.inflate(R.layout.citylist, null);
        cityname = (ListView)cityview.findViewById(R.id.citylistview);
        citybuilder.setCancelable(true)
                .setView(cityview);
        final Dialog citydialog = citybuilder.create();
        Citylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                citydialog.show();
                final ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Cities, android.R.layout.simple_list_item_1);
                cityname.setAdapter(adapter);
                cityname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        city.setText(adapter.getItem(position).toString());
                        citydialog.cancel();
                    }
                });
            }
        });
        Truckslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                citydialog.show();
                final ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Minitrucks, android.R.layout.simple_list_item_1);
                cityname.setAdapter(adapter);
                cityname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Trucks.setText(adapter.getItem(position).toString());
                        citydialog.cancel();
                        try {
                            Showpricedetails(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        return Rootview;
    }

    public void Showpricedetails(int position) throws JSONException {
        Postrate.setText("\u20B9 "+String.valueOf(jsonArray.getJSONObject(position).getInt("rate1"))+"    per/kms");
        Prerate.setText("\u20B9 "+String.valueOf(jsonArray.getJSONObject(position).getInt("basefare")));
        Transitrate.setText("\u20B9 "+String.valueOf(jsonArray.getJSONObject(position).getInt("TransitTimeRate"))+"    per/min");
        Waitingrate.setText("\u20B9 "+String.valueOf(jsonArray.getJSONObject(position).getInt("waitingTimeRate"))+"    per/min");
    }

}
