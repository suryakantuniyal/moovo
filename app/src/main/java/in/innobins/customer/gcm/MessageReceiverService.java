package in.innobins.customer.gcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import in.innobins.customer.RatingActivity;
import in.innobins.customer.TrackMoovo;

/**
 * Created by Abhishek on 10/10/2015.
 */
public class MessageReceiverService extends FirebaseMessagingService {

    private static final String TAG = "GCMMsgRecService";
    static  String action;
    String orderid,driverid,from;
    private LocalBroadcastManager broadcaster;
    Context context;
    String orderId,driverId;

    public static TrackMoovo instance;
    /*private static final Map<String, GcmCommand> MESSAGE_RECEIVERS;
    static {
        // Known messages and their GCM message receivers
        //all the known messages are kept in  Hash Map
        //with verb as key and object of handler as value

        Map <String, GcmCommand> receivers = new HashMap<String, GcmCommand>();
        receivers.put("test", new TestCommand());
        receivers.put("announcement", new AnnouncementCommand());
        receivers.put("update", new SyncCommand());
        // TODO: add  one more class for Tracking command
        receivers.put("extendedNotification", new SyncUserCommand());
        receivers.put("notification", new NotificationCommand());
        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }*/

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);

    }
    public static String sendMessage(){
        String newaction ;
        newaction = action;
        Log.d("newaction",newaction);
        return newaction;
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {

        context = this;
        super.onMessageReceived(message);
        from = message.getFrom();
        Map data = message.getData();
        Log.d("datafc",String.valueOf(data));


        if (message.getData().size() > 0) {

            Intent intent = new Intent(this, RatingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
       }
    }
}




