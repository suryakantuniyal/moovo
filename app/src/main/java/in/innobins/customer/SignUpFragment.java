package in.innobins.customer;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by Harshit on 12-09-2015.
 */
public class SignUpFragment extends Fragment implements View.OnTouchListener {
    Button signup;
    EditText password,otp ,name,email;
    String storedOtp = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_fragment, container, false);
        signup = (Button)rootView.findViewById(R.id.signup);
        email = (EditText) rootView.findViewById(R.id.email);
        password = (EditText) rootView.findViewById(R.id.loginpassword);
        name = (EditText) rootView.findViewById(R.id.name);
        otp= (EditText) rootView.findViewById(R.id.otp);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String unVarifiedNumber = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_UNVARIFIED_NUMBER, "");
                storedOtp= PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_OTP,"");
                if (!UtilFunctions.isNetworkAvailable(getActivity().getBaseContext())) {
                    UtilFunctions.makeToast(getActivity().getBaseContext(), getString(R.string.noNetwork), Toast.LENGTH_SHORT);
                } else if (name.getText().toString().length() != 0) {
                    if (password.getText().toString().length() >= Constants.PasswordMinLength && password.getText().toString().length() <= Constants.passwordMaxLength) {
                        if (email.getText().toString().length() != 0) {
                            if (isValidEmail(email.getText().toString())) {

                                if (otp.getText().toString().length() != 0) {
                                    if (otp.getText().toString().equals(storedOtp) ) {
                                        new newSignup().execute(unVarifiedNumber, name.getText().toString(), email.getText().toString(), password.getText().toString());
                                    }else{
                                        UtilFunctions.makeToast(getActivity().getBaseContext(), "OTP Does not Match", Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    UtilFunctions.makeToast(getActivity().getBaseContext(), "OTP can't be blank!", Toast.LENGTH_SHORT);
                                }
                            }else{
                                UtilFunctions.makeToast(getActivity().getBaseContext(), "Invalid Email Id !!", Toast.LENGTH_SHORT);
                            }
                        } else {
                            UtilFunctions.makeToast(getActivity().getBaseContext(), "Email can't be blank!", Toast.LENGTH_SHORT);
                        }
                    } else {
                        UtilFunctions.makeToast(getActivity().getBaseContext(), "Password Length should be between 5 to 10", Toast.LENGTH_SHORT);
                    }
                }else{
                    UtilFunctions.makeToast(getActivity().getBaseContext(), "Enter Name!!", Toast.LENGTH_SHORT);
                }
            }
        });
        return rootView;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        getFragmentManager().popBackStack();
        return false;
    }


    private class newSignup extends AsyncTask<String,Void,Void> {
        boolean bool;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("SignUp Request...");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            try{

                bool=signup(params[0],params[1],params[2],params[3]);
            }

            catch (Exception e){
                Log.d("error", "error in sending signup detail");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            signup.setTextColor(Color.WHITE);
            if(bool){
                //if signup is successfull
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
            }
            else{
                UtilFunctions.makeToast(getActivity().getBaseContext(), "Already Registered On Moovo!!", Toast.LENGTH_SHORT);
            }

        }
    }
    public boolean signup(String contact,String name,String email,String password){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonresult = new JSONObject();
//        byte[] data = new byte[0];
//        try {
//            data = password.getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        password = Base64.encodeToString(data, Base64.DEFAULT);
        try{
            jsonObject.put("name", name);
            jsonObject.put("password", password);
            jsonObject.put("phone", contact);
            jsonObject.put("email",email);
        }
        catch (Exception e){
            Log.d("error", "error in creating jsonobject signup method");
        }
        RESTfulAPI resTfulAPI = new RESTfulAPI();
        try{
            jsonresult = resTfulAPI.getJSONfromurl("api/customer/signup/","POST",jsonObject.toString());
        }
        catch (Exception e){
            Log.d("error ","in signup");
        }
        if (jsonresult != null){
            Log.d("jobject", jsonresult.toString());
            try{
                if(jsonresult.getInt("success")==1){
                    //   SharedPreferences session=getActivity().getSharedPreferences(SharedPrefUtil.PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor sessionEditor = session.edit();
                    sessionEditor.putString(SharedPrefUtil.KEY_USERNAME, name);
                    sessionEditor.putString(SharedPrefUtil.KEY_USER_PHONE,contact);
                    sessionEditor.putString(SharedPrefUtil.KEY_USER_EMAIL,email);
                    //if need to store password in the future
                    // sessionEditor.putString("password", password);
                    sessionEditor.putBoolean(SharedPrefUtil.KEY_LOGGED_IN, true);
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
                Log.d("error","in signup");
            }
        }
        return false;

    }
//    public void onRadioButtonClick(View v) {
//        if(buttonChecked!=null){
//            buttonChecked.setChecked(false);
//        }
//        RadioButton button = (RadioButton) v;
//        buttonChecked=button;
//        buttonChecked.setChecked(true);
//    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final SmsManager sms = SmsManager.getDefault();
            boolean isConfirmVisible = LoginSignUpActivity.isActivityVisible();
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                String unVarifiedNumber = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_UNVARIFIED_NUMBER, "");
                storedOtp= PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_OTP,"");
                try {
                    if (isConfirmVisible) {
                        Bundle bundle = intent.getExtras();
                        SmsMessage[] smsMessages = null;
                        String messages = "";
                        if (bundle != null)
                        {
                            Object [] pdus = (Object[]) bundle.get("pdus");
                            smsMessages = new SmsMessage[pdus.length];
                            String Sender = null;
                            for (int i = 0; i < smsMessages.length; i++)
                            {
                                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                Sender = smsMessages[i].getOriginatingAddress();
                                messages += "SMS From: " + smsMessages[i].getOriginatingAddress();
                                messages += " : ";
                                messages += smsMessages[i].getMessageBody();
                                messages += "\n";
                            }
                            Log.d("message", Sender+" "+storedOtp);
                            if (Sender != null && Sender.equals("DM-iMOOVO")){
                                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage("Getting OTP for verification");
                                progressDialog.show();
                                otp.setText(storedOtp);
                                progressDialog.dismiss();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
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
