package in.innobins.customer.gcm.Command;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;



import in.innobins.customer.LandingActivity;
import in.innobins.customer.R;
import in.innobins.customer.gcm.GcmCommand;

/**
 * Created by Abhishek on 10/10/2015.
 */
public class NotificationCommand extends GcmCommand {
    private static final String TAG ="NotificationCommand";
    @Override
    public void execute(Context context, String type, JSONObject extraData) {
        String message = null ;
        Log.i(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
        try {
           message  = extraData.getString("text");
            Log.d("notificationmsg",message);
        }
        catch(Exception e){
            e.printStackTrace();

        }
        assert message != null;
        if (message.equals("update")){
            Log.d("UPDATE","UPDATE");
            UpdateApp(message, context);
        }else {
            Log.d("sendnoti",message);
            sendNotification(message,context);
        }
    }

    private void UpdateApp(String message,Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=in.moovo.Moovo"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MOOVO")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void sendNotification(String message,Context context) {
        Intent intent = new Intent(context, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MOOVO")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
