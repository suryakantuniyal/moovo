package in.innobins.customer;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
public class ChangePassword extends Fragment implements View.OnTouchListener {
    Button changepassword;
    EditText loginpassword,confirmpassword,otp;
    String login_password, confirm_password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.change_password, container, false);
        changepassword = (Button)rootView.findViewById(R.id.changepassword);
        loginpassword = (EditText) rootView.findViewById(R.id.loginpassword);
        confirmpassword = (EditText) rootView.findViewById(R.id.confirmpassword);

        otp= (EditText) rootView.findViewById(R.id.otp);


        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String unVarifiedNumber = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_UNVARIFIED_NUMBER, "");
                String storedOtp= PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_OTP,"");
                Log.d("unVarifiedNumber", unVarifiedNumber);
                Log.d("storedOtp",storedOtp);

                login_password=loginpassword.getText().toString();
                confirm_password=confirmpassword.getText().toString();
                if (!UtilFunctions.isNetworkAvailable(getActivity().getBaseContext())) {
                    UtilFunctions.makeToast(getActivity().getBaseContext(), getString(R.string.noNetwork), Toast.LENGTH_SHORT);
                } else if (loginpassword.getText().toString().length() >= Constants.PasswordMinLength && loginpassword.getText().toString().length() <= Constants.passwordMaxLength) {
                        if (confirm_password.equals(login_password)) {
                            if (otp.getText().toString().length() != 0) {

                                if (otp.getText().toString().equals(storedOtp) ) {
                                    new newChangPassword().execute(unVarifiedNumber, loginpassword.getText().toString());
                                }else{
                                    UtilFunctions.makeToast(getActivity().getBaseContext(), "OTP Doesnot Match", Toast.LENGTH_SHORT);
                                }

                            } else {
                                UtilFunctions.makeToast(getActivity().getBaseContext(), "Enter Valid OTP", Toast.LENGTH_SHORT);
                            }
                        } else {
                            UtilFunctions.makeToast(getActivity().getBaseContext(), "Password Does not Match", Toast.LENGTH_SHORT);
                        }
                    } else {
                        UtilFunctions.makeToast(getActivity().getBaseContext(), "Password Length should be between 5 to 10", Toast.LENGTH_SHORT);
                    }

            }
        });
        return rootView;
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        getFragmentManager().popBackStack();
        return false;
    }


    private class newChangPassword extends AsyncTask<String,Void,Void> {
        boolean bool;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Change Password Request...");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                bool=ChangePassword(params[0], params[1]);
            }

            catch (Exception e){
                Log.d("Error Change Password", e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();

            if(bool){
                Constants.appState = Constants.MAIN_DRAWER_PART;
                SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor =session.edit();
                editor.putString(SharedPrefUtil.KEY_APPLICATION_STATE, Constants.MAIN_DRAWER_PART);
                editor.apply();
                Intent drawerActivity = new Intent(getActivity(),MainActivity.class);
                drawerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle extra = new Bundle();
                extra.putInt("FROM", new Integer(1));
                drawerActivity.putExtras(extra);
                startActivity(drawerActivity);
                getActivity().finish();
            }
            else{
                UtilFunctions.makeToast(getActivity().getBaseContext(), "Can'nt Change Password! Try Again!", Toast.LENGTH_SHORT);
            }

        }
    }
    public boolean ChangePassword(String phoneNumber,String password){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonresult = new JSONObject();
        try{
            jsonObject.put("phone", phoneNumber);
            jsonObject.put("password", password);
        }
        catch (Exception e){
            Log.d("error", "error in creating json object signup method");
        }
        RESTfulAPI resTfulAPI = new RESTfulAPI();
        jsonresult = resTfulAPI.getJSONfromurl("api/customer/user/forgot/","POST",jsonObject.toString());
        Log.d("LO",jsonresult.toString());
        if (jsonresult != null){
            try{
                if(jsonresult.getInt("success")==1){
                    SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor sessionEditor = session.edit();

                    sessionEditor.putString(SharedPrefUtil.KEY_USERNAME, jsonresult.getJSONObject("datasets").getString("name"));
                    sessionEditor.putString(SharedPrefUtil.KEY_USER_EMAIL, jsonresult.getJSONObject("datasets").getString("email"));
                    sessionEditor.putString(SharedPrefUtil.KEY_USER_PHONE, jsonresult.getJSONObject("datasets").getString("phoneNumber"));
                    sessionEditor.putBoolean("login", true);
                    sessionEditor.apply();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("API_KEY", jsonresult.getJSONObject("datasets").getString("API_KEY"));
                    editor.apply();
                    return true;
                }
            }
            catch (Exception e){
                Log.d("error in signup",e.toString());
            }
        }
        return false;
    }
}
