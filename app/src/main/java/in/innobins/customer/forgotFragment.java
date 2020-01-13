package in.innobins.customer;

/**
 * Created by Harshit on 15-09-2015.
 */
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Random;

public class forgotFragment extends Fragment {
    EditText username;
    Button login;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.forgot_verify, container, false);
        username=(EditText) rootView.findViewById(R.id.username);
        login=(Button) rootView.findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                String user = username.getText().toString();

                if (user.length() == 10 || user.length() == 11) {
                    new SendOtp().execute(user);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"Invalid Number", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView ;
    }

    private class SendOtp extends AsyncTask<String,Void,Void> {
        boolean bool;
        boolean booly;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.numberVerification));
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            booly=CheckUserStatus(params[0]);
            if (booly==true) {
                bool = sendotpTouser(params[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
            if(bool){
                Log.d("error", "Inside Function!");
                // loading signup fragment as well as adding the transaction to backstack
                if (login.getText().toString().equals("GET OTP")){
                    ChangePassword changePassword = new ChangePassword();
                    FragmentTransaction fragmentTransaction=  getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame,changePassword);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    SignUpFragment signupFragment = new SignUpFragment();
                    FragmentTransaction fragmentTransaction=  getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame,signupFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true)
                        .setMessage("You are not Registered With Moovo. Please check your Mobile number and Try Again")
                        .setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                login.setText("PROCEED");
                                username.setText(null);
                                dialog.cancel();
                            }
                        }).show();
            }
        }

    }
    private boolean CheckUserStatus(String username){
        Log.d("status", "CheckUserStatus ");
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonresult = new JSONObject();
        try {
            jsonObject.put("phone", username);
        }catch (Exception e){
            Log.d("status", "CheckUserStatus ");
        }
        RESTfulAPI resTfulAPI = new RESTfulAPI();
        jsonresult = resTfulAPI.getJSONfromurl("api/customer/profile/check/", "POST", jsonObject.toString());
        Log.d("login response", jsonresult.toString());
        if (jsonresult != null){
            try{
                if(jsonresult.getInt("success") == 1){
                    if (login.getText().toString().equals("PROCEED")){
                        return true;
                    } else {
                        return false;
                    }
                }else{
                    if (!login.getText().toString().equals("PROCEED")){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            catch (Exception e){
                Log.d("error", e.toString());
            }

        }
        return  true;
    }



    private boolean sendotpTouser(String username){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonresult = new JSONObject();
        int START = 100000;
        int jb;
        int END = 999999;
        Random rand = new Random();
        long range = (long)END - (long)START + 1;
        long fraction = (long)(range * rand.nextDouble());
        int randomNumber =  (int)(fraction + START);
        SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor sessionEditor = session.edit();
        sessionEditor.putString(SharedPrefUtil.KEY_UNVARIFIED_NUMBER, username);
        sessionEditor.putString(SharedPrefUtil.KEY_OTP, Integer.toString(randomNumber));
        sessionEditor.apply();
        try{
            jsonObject.put("phone", username);
            jsonObject.put("otp", randomNumber);
        }
        catch (Exception e){
            Log.d("error", "Error in verifying number ");
        }
        RESTfulAPI resTfulAPI = new RESTfulAPI();
        jsonresult = resTfulAPI.getJSONfromurl("api/customer/signup/otp/","POST",jsonObject.toString());
//        Log.d("OTP",jsonresult.toString());
        try{
            if(jsonresult.getInt("success") == 1){
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

