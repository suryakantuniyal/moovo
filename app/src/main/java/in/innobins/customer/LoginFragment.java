package in.innobins.customer;

/**
 * Created by Harshit on 12-09-2015.
 */
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


public class LoginFragment extends Fragment {
    String Message;
    TextView login,newSignUp;
    EditText username,password;
    TextView forgotpassword;
    CheckBox mCbShowPwd;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment,container,false);
        //setting
        login=(TextView) rootView.findViewById(R.id.login);
        newSignUp=(TextView)rootView.findViewById(R.id.newsignup);
        username=(EditText) rootView.findViewById(R.id.username);
        password=(EditText) rootView.findViewById(R.id.loginpassword);
        forgotpassword=(TextView)rootView.findViewById(R.id.forgotpassword);
        mCbShowPwd = (CheckBox) rootView.findViewById(R.id.cbShowPwd);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilFunctions.isNetworkAvailable(getActivity())) {
                    String user = username.getText().toString();
                    String pass = password.getText().toString();
//                    if (user.length() == 10 || user.length() == 11) {
//                        if (pass.length() >= Constants.PasswordMinLength && pass.length() <= Constants.passwordMaxLength) {
                            new LogInRequest().execute(user, pass);
//                        } else {
//                            UtilFunctions.makeToast(getActivity(), getString(R.string.invalidPassword), Toast.LENGTH_SHORT);
////                        }
//                    } else {
//                        UtilFunctions.makeToast(getActivity(), getString(R.string.invalidUsername), Toast.LENGTH_SHORT);
//                    }
                } else {
                    UtilFunctions.makeToast(getActivity(), getString(R.string.noNetwork), Toast.LENGTH_SHORT);
                }
            }
        });
        newSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // loading signup fragment as well as adding the transaction to backstack
                verifyFragment verifyFragment = new verifyFragment();
                FragmentTransaction fragmentTransaction=  getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,verifyFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // loading signup fragment as well as adding the transaction to backstack
                forgotFragment forgotFrag = new forgotFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, forgotFrag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        return rootView ;
    }


    public boolean onTouch(View view, MotionEvent motionEvent) {
        getFragmentManager().popBackStack();
        return false;
    }
    private class LogInRequest extends AsyncTask<String,Void,Void> {
        boolean bool;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
           // progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.LoginRequest));
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            bool=loginRequest(params[0], params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
            if(bool){
                //User Has Been Authenticated By the Server
                Constants.appState = Constants.MAIN_DRAWER_PART;
                SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(getActivity());
                //   SharedPreferences session = getActivity().getSharedPreferences(SharedPrefUtil.PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =session.edit();
                editor.putString(SharedPrefUtil.KEY_APPLICATION_STATE, Constants.MAIN_DRAWER_PART);
                editor.commit();
//                Constants.orderAvailability = false ;
                Intent drawerActivity = new Intent(getActivity(),MainActivity.class);
                drawerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle extra = new Bundle();
                extra.putInt("FROM", new Integer(1));
                drawerActivity.putExtras(extra);
                startActivity(drawerActivity);
                getActivity().finish();
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }

            else{
                Toast.makeText(getActivity().getApplicationContext(),Message,Toast.LENGTH_LONG).show();
            }
        }

    }
    private boolean loginRequest(String username, String password){
        JSONObject jObject2=new JSONObject();
        JSONObject jObject;
//        byte[] data = new byte[0];
//        try {
//            data = password.getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        password = Base64.encodeToString(data, Base64.DEFAULT);
        try{
            jObject2.put("phone",username);
            jObject2.put("password", password);
        }
        catch (Exception e){
            Log.d("error", "error in creating login request login request method ");
        }
//        String url = jObject2.toString();
//        try {
//            url = URLEncoder.encode(url, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            Log.d("error", "error in loginrequest while encoding url ");
//        }
//        url = "login/new/?data=";
//        Log.d("url",url);
//        jObject= UtilFunctions.getJSONFromUrl(url);
        RESTfulAPI resTfulAPI = new RESTfulAPI();
        jObject = resTfulAPI.getJSONfromurl("api/customer/login/","POST",jObject2.toString());
        if (jObject != null){
            Log.d("loginresp", jObject.toString());
            try{
                if (jObject != null){
                    if(jObject.getInt("success")==1 && !jObject.getJSONObject("datasets").getString("Message").equals("Wrong Password")){
                        SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        // SharedPreferences session=getActivity().getSharedPreferences(SharedPrefUtil.PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor sessionEditor = session.edit();
                        sessionEditor.putString(SharedPrefUtil.KEY_USERNAME, jObject.getJSONObject("datasets").getString("name"));
//                      Constants.driverId = jObject.get("driverId").toString();
                        sessionEditor.putString(SharedPrefUtil.KEY_USER_EMAIL, jObject.getJSONObject("datasets").getString("email"));
                        sessionEditor.putString(SharedPrefUtil.KEY_USER_PHONE, jObject.getJSONObject("datasets").getString("phoneNumber"));
                        //in future if we need to store password as well
                        //sessionEditor.putString("password", password);
                        sessionEditor.putBoolean("login", true);
                        sessionEditor.apply();
//                      sessionEditor.commit();
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("API_KEY", jObject.getJSONObject("datasets").getString("API_KEY"));
                        editor.apply();
                        return true;
                    } else if (jObject.getInt("success")==1 && jObject.getJSONObject("datasets").getString("Message").equals("Wrong Password")){
                        Message = "Wrong Password";
                        return false;
                    }
                    else{
                        Message = "Username does not match";
                        return false;
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true)
                            .setTitle("Server Problem")
                            .setMessage("Sorry for delay.We are unable to connect to the server due to network problem.Please check your network and try again")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

