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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sasuke on 11/9/15.
 */
public class CompletedOrder extends Fragment {
    JSONArray jsonArrayresult = new JSONArray();
    ArrayList<OrderItem> mOrderItems = new ArrayList<OrderItem>();
    ListView orderlist;
    ImageView Logo;
    String request_api_key = "API_KEY",jsonlist;
    TextView BookMoovo;
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View Rootview = inflater.inflate(R.layout.completeorder, null);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        request_api_key = sharedPreferences.getString("API_KEY",request_api_key);
        BookMoovo = (TextView)Rootview.findViewById(R.id.bookmoovo);
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
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("completed", true)){
            new GetUpcomingOrders().execute(jsonObject.toString());
        } else {
            jsonlist = sharedPreferences.getString("Jsonresultcompleted", "");
            try {
//                if (! sharedPreferences.getBoolean("populatedcom", true) ){
                    Populateview(new JSONArray(jsonlist));
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        orderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                try {
                    JSONArray jsonargs = new JSONArray(jsonlist);
                    args.putString("Bundlestring",jsonargs.getJSONObject(position).toString());
                    args.putString("Status", "Completed");
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

    public void Populateview(JSONArray jsonArray){
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("populatedcom", true);
        editor.apply();
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
            Log.d("array", jsonArray.toString());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOrderItems = new ArrayList<>();
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
            complete_url = "api/customer/order/completed/";
            jsonObject = resTfulAPI.getJSONfromurl(complete_url,"POST",params[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if (jsonObject != null){
                try {
                    if (jsonObject.getInt("success")==1){
                        Log.d("Completed",jsonObject.toString());
                        jsonArrayresult = jsonObject.getJSONArray("datasets");
                        Log.d("JSonArray", String.valueOf(jsonArrayresult.length()));
                        Populateview(jsonArrayresult);
                        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Jsonresultcompleted",jsonArrayresult.toString());
                        editor.putBoolean("completed", true);
                        editor.apply();
                        jsonlist = jsonArrayresult.toString();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false)
                                .setMessage("Authentication Problem")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Logout.logoutRequest(getActivity())){
                                            Intent loginSignUp = new Intent(getActivity(), in.innobins.customer.LoginSignUpActivity.class);
                                            startActivity(loginSignUp);
                                        };
                                        dialog.cancel();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Unable to connect server", Toast.LENGTH_SHORT).show();
                }
            } else {
                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                jsonlist = sharedpreferences.getString("Jsonresultcompleted", "");
                try {
                    Populateview(new JSONArray(jsonlist));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
