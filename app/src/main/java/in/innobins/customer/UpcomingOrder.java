package in.innobins.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sasuke on 11/9/15.
 */
public class UpcomingOrder extends Fragment {
    JSONArray jsonArrayresult = new JSONArray();
    ArrayList<OrderItem> mOrderItems = new ArrayList<OrderItem>();
    ListView orderlist;
    String jsonlist;
    String request_api_key = "API_KEY";
    TextView BookMoovo;
    ImageView Logo;
    boolean populated = false;
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View Rootview = inflater.inflate(R.layout.upcomingorder, null);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        request_api_key = sharedPreferences.getString("API_KEY",request_api_key);
        BookMoovo = (TextView)Rootview.findViewById(R.id.bookmoovo);
        Log.d("created again","upcoming");
        Logo = (ImageView)Rootview.findViewById(R.id.logo);
        Logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragment().getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContent,homeFragment).commit();
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key",request_api_key);
            jsonObject.put("phone", PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_USER_PHONE, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("string", jsonObject.toString());
        orderlist = (ListView)Rootview.findViewById(R.id.orderList);
        Log.d("status", String.valueOf(sharedPreferences.getBoolean("upcoming",true)));
        if (!sharedPreferences.getBoolean("upcoming",true)){
            new GetUpcomingOrders().execute(jsonObject.toString());
        } else {
            jsonlist = sharedPreferences.getString("Jsonresultupcoming","");
            try {
//                if (! sharedPreferences.getBoolean("populatedup", true) ){
                    Populateview(new JSONArray(jsonlist));
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("jsonlist", jsonlist);
        }
        orderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                try {
                    JSONArray jsonargs = new JSONArray(jsonlist);
                    args.putString("Bundlestring",jsonargs.getJSONObject(position).toString());
                    args.putString("Status", "Upcoming");
                    Fragment fragment = new Upcomingorderdetails();
                    fragment.setArguments(args);
                    String fragmentname = "Upcoming";
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.mainContent,fragment).addToBackStack(null).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return Rootview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOrderItems = new ArrayList<>();
    }

    public void Populateview(JSONArray jsonArray){
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("populatedup", true);
        editor.apply();
        Log.d("length", String.valueOf(jsonArray.length()));
        if (jsonArray.length() == 0){
            orderlist.setVisibility(View.GONE);
            BookMoovo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentManager fragmentManager = getParentFragment().getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.mainContent, homeFragment).commit();
                }
            });
        } else {
            for (int i=0;i<jsonArray.length();i++){
                try {
                    mOrderItems.add(new OrderItem(jsonArray.getJSONObject(i).getString("dateTime"),jsonArray.getJSONObject(i).getString("orderId"),jsonArray.getJSONObject(i).getString("pickUp")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(),mOrderItems);
            orderlist.setAdapter(customListAdapter);
        }
    }

    public class GetUpcomingOrders extends AsyncTask<String, Void, Void>{
        String jsonstring,search_s,complete_url;
        JSONObject jsonObject = new JSONObject();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        protected void onPreExecute() {
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setMessage("   Fetching Data....");
        }
        @Override
        protected Void doInBackground(String... params) {
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            complete_url = "api/customer/order/unassigned/";
            jsonObject = resTfulAPI.getJSONfromurl(complete_url,"POST",params[0]);
            Log.d("jsnValue",String.valueOf(jsonObject));
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if (jsonObject != null) {
                try {
                    Log.d("Upcoming", jsonObject.toString());
                    if (jsonObject.getInt("success") == 1) {
                        jsonArrayresult = jsonObject.getJSONArray("datasets");
                        Log.d("JSonArray", String.valueOf(jsonArrayresult.length()));
                        Populateview(jsonArrayresult);
                        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Jsonresultupcoming", jsonArrayresult.toString());
                        editor.putBoolean("upcoming", true);
                        editor.apply();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //            SharedPreferences sharedpreferences = getActivity().getSharedPreferences("OrderList", Context.MODE_PRIVATE);
                //            jsonlist = sharedpreferences.getString("Jsonresultupcoming", "LOL");
                jsonlist = jsonArrayresult.toString();
            } else {
                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                jsonlist = sharedpreferences.getString("Jsonresultupcoming","");
                try {
                    Populateview(new JSONArray(jsonlist));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("jsonlist", jsonlist);
            }
        }
    }
}
