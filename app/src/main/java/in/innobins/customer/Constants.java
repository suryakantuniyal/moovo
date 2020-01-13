package in.innobins.customer;

/**
 * Created by Harshit on 12-09-2015.
 */
import android.view.KeyEvent;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Abhishek on 8/2/2015.
 */
public class Constants {
    static int passwordMaxLength = 10;
    static int PasswordMinLength=5;
    static final String companyPhoneNumber = "9999045752";
    static String driverId ;
    //all the fields with prefix order are Order Details
    static String orderId,orderEmail,orderName,orderPhoneNumber,orderEstimatedFare,orderEstimatedDistance,orderDateTime,orderLabour ;
    static String orderStartLatitude,orderStartLongitude,orderEndLatitude,orderEndLongitude,orderFromAddress,orderToAddress ;
    static LatLng orderFromLatLng,orderToLatLng;
    //
    static String appState ;
    //
    static boolean orderAvailability ;
    static String finalFare,finalDistance;
    static Long reachingTimeStart=0L;



    static final String STATE_ASK_ORDER = "ask order";
    static final String MAIN_DRAWER_PART="main activity";
    static final String  STATE_LOGGED_OUT = "logged out";
    static final String  STATE_REACHING = "reaching";
    static final String STATE_LOADING = "loading";
    static final  String STATE_RUNNING="running";
    static final String STATE_UNLOADING = "unloading";
    static final  String STATE_WAITING = "waiting";
    static final String STATE_END = "end";

    static int waitingTime=0;
    static int loadingTime=0;
    static int unloadingTime=0;
    static  boolean clocksAndTimer;


    //Used for blocking volume keys
    public static final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));


}
