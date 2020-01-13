package in.innobins.customer.gcm;

import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceIdService;

import in.innobins.customer.SharedPrefUtil;

/**
 * Created by Abhishek on 10/10/2015.
 */
public class InstanceListenerService extends FirebaseInstanceIdService {
    private static final String TAG = "InstanceIDLService";
    static SharedPreferences mSharedprefrences;
    static SharedPreferences.Editor mEditor;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {

     mSharedprefrences = getSharedPreferences(SharedPrefUtil.PREFERENCES,MODE_PRIVATE);
     String registrationToken = mSharedprefrences.getString(SharedPrefUtil.FCM_TOKEN,"");

        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, in.innobins.customer.gcm.RegistrationIntentService.class);
        startService(intent);
    }
    // [END refresh_token]
}
