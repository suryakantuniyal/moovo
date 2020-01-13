package in.innobins.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sasuke on 8/9/15.
 */
public class ProfileFragment extends Fragment {
    public static String TAG = "ProfileFragement";
    TextView changepassword, name, home,work,email,Title, signincontact;
    Button Logout,Save;
    EditText Currentpass, Newpass, Reenterpass;
    String request_api_key = "API_KEY";
    ImageView Togglebutton;
    LinearLayout changepass, addhome, addwork;
    Dialog changepassdialog;
    boolean bool;
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View Rootview = inflater.inflate(R.layout.profilefragment, parent, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        request_api_key = sharedPreferences.getString("API_KEY",request_api_key);
        Title = (TextView)Rootview.findViewById(R.id.title);
        Title.setText("my profile");
        Togglebutton = (ImageView)Rootview.findViewById(R.id.togglebutton);
        Togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(MainActivity.mDrawerPane);
            }
        });
        changepassword = (TextView)Rootview.findViewById(R.id.signpassword);
        signincontact=(TextView)Rootview.findViewById(R.id.signcontact);
        String stooredContact = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_USER_PHONE, "");
        signincontact.setText(stooredContact);
        name = (TextView)Rootview.findViewById(R.id.personalname);
        String stooredUserName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_USERNAME, "");
        name.setText(stooredUserName);
        email = (TextView)Rootview.findViewById(R.id.personalemail);
        String stooredEmail = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_USER_EMAIL, "");
        email.setText(stooredEmail);
        home = (TextView)Rootview.findViewById(R.id.addhome);
        work = (TextView)Rootview.findViewById(R.id.addwork);
        Logout = (Button)Rootview.findViewById(R.id.logout);

        changepass = (LinearLayout)Rootview.findViewById(R.id.signpassworddetails);
        addhome = (LinearLayout)Rootview.findViewById(R.id.addhomedetails);
        addwork = (LinearLayout)Rootview.findViewById(R.id.addworkdetails);
        AlertDialog.Builder logout = new AlertDialog.Builder(getActivity());

        logout.setCancelable(true)
                .setMessage("Are you sure want to Sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bool= in.innobins.customer.Logout.logoutRequest(getActivity());
                        if (bool==true){
                            Intent loginSignUp = new Intent(getActivity(), in.innobins.customer.LoginSignUpActivity.class);
                            startActivity(loginSignUp);
//                            finish();

                        };
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final Dialog logoutdialog = logout.create();
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutdialog.show();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.changepassword, null);
        Currentpass = (EditText)view.findViewById(R.id.currentpass);
        Newpass = (EditText)view.findViewById(R.id.newpassword);
        Reenterpass = (EditText)view.findViewById(R.id.reenterpass);
        Save = (Button)view.findViewById(R.id.save);
        changepassdialog = builder.setCancelable(true).setView(view).create();
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepassdialog.show();
                Save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Newpass.getText().toString().equals("")||!Currentpass.getText().toString().equals("")||!Reenterpass.getText().toString().equals("")){
                            if (Newpass.getText().toString().equals(Reenterpass.getText().toString())){
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("key",request_api_key);
                                    jsonObject.put("currentPassword", Currentpass.getText().toString());
                                    jsonObject.put("newPassword", Newpass.getText().toString());
                                    String storedContact = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SharedPrefUtil.KEY_USER_PHONE, "");
                                    jsonObject.put("phone", storedContact);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new ChangePassword().execute(jsonObject.toString());
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setMessage("Password Mismatched.Both new and re-enter should be same  Check and try again");
                                Dialog wrong = builder.create();
                                wrong.show();
                            }
                        } else {
                            Toast.makeText(getActivity(),"All fields are Required", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        return Rootview;
    }

    public class ChangePassword extends AsyncTask<String ,Void, Void>{
    String message;
    JSONObject jsonresult;
    ProgressDialog pd = new ProgressDialog(getActivity());
    protected void onPreExecute() {
        pd.setMessage("   Processing...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);
    }
    @Override
    protected Void doInBackground(String... params) {
        RESTfulAPI resTfulAPI = new RESTfulAPI();
        String complete_url = "api/customer/profile/changepassword/";
        jsonresult = resTfulAPI.getJSONfromurl(complete_url, "POST",params[0]);
        Log.d("JSONRESULT", jsonresult.toString());
        return null;
    }
        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            changepassdialog.cancel();
            try {
                if (jsonresult.getInt("success")==1){
                    message = jsonresult.getJSONObject("Response").getString("message");
                    if (message.equals("Wrong Password")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setMessage("Wrong password.Please check the current password and try again");
                        Dialog wrong = builder.create();
                        wrong.show();
                    } else if (message.equals("Password changed Successfully")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setMessage("Password changed Successfully");
                        Dialog wrong = builder.create();
                        wrong.show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false)
                            .setMessage("Authentication Problem")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (in.innobins.customer.Logout.logoutRequest(getActivity())){
                                        Intent loginSignUp = new Intent(getActivity(), in.innobins.customer.LoginSignUpActivity.class);
                                        startActivity(loginSignUp);
                                    };
                                    dialog.cancel();
                                }
                            }).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("fragment B");
    }
}
