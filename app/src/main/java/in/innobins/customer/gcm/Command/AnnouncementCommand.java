package in.innobins.customer.gcm.Command;

import in.innobins.customer.LandingActivity;
import in.innobins.customer.R;
import in.innobins.customer.TrackMoovo;
import in.innobins.customer.gcm.GcmCommand;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Created by Abhishek on 10/10/2015.
 */
public class AnnouncementCommand extends GcmCommand {
    private static final String TAG = "AnnouncementCommand";
    JSONObject textData = new JSONObject();
    @Override
    public void execute(Context context, String type, JSONObject extraData) {

        String message = null;
        String imageUrl = null;
        Log.i(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
        try {
            message = extraData.getString("text");
            imageUrl = extraData.getString("imageUrl");
            Log.d("aaanouc", String.valueOf(extraData));
            textData = new JSONObject(extraData.getString("text"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (textData.getString("orderId") != null){
                TrackyourMoovo(textData.getString("textMessage"),imageUrl,context);
            } else {
                Log.d("aaanouc","orderidnull");
                sendBigImageNotification(message, imageUrl, context);
            }
        } catch (JSONException e) {

            Log.d("aaanouc", "not for tracking");
            sendBigImageNotification(message, imageUrl, context);
        }
    }

    private void sendBigImageNotification(String message, String imageUrl,Context context) {
        Bitmap remote_picture = null;

// Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new
                NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle(message);
        notiStyle.setSummaryText(message);

        try {
            remote_picture = BitmapFactory.decodeStream(
                    (InputStream) new URL(imageUrl).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

// Add the big picture to the style.
        notiStyle.bigPicture(remote_picture);

// Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(context, LandingActivity.class);

// This ensures that the back button follows the recommended
// convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

// Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(LandingActivity.class);

// Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification myNotification = new NotificationCompat.Builder(context)

                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(message)
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(notiStyle).build();
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, myNotification);
    }

    private void TrackyourMoovo(String message, String imageUrl,Context context) {
        Bitmap remote_picture = null;
        Bitmap Icon_pic = null;
// Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new
                NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle("Track your Moovo");
        notiStyle.setSummaryText(message);
        Notification.BigTextStyle bigTextStyle = new
                Notification.BigTextStyle();
        try {
            remote_picture = BitmapFactory.decodeStream(
                    (InputStream) new URL(imageUrl).getContent());
            Icon_pic = BitmapFactory.decodeStream((InputStream) new URL("http://moovo.in/img/favicon/apple-icon-57x57.png").getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        notiStyle.bigPicture(remote_picture);
        Intent resultIntent = new Intent(context, TrackMoovo.class);
        Log.d("aaanouc",textData.toString());
        try {
            resultIntent.putExtra("orderid", textData.getString("orderId"));
            resultIntent.putExtra("contact", textData.getString("driverContact"));
            resultIntent.putExtra("latitude", textData.getDouble("latitude"));
            resultIntent.putExtra("longitude", textData.getDouble("longitude"));
            resultIntent.putExtra("driverName", textData.getString("driverName"));
            resultIntent.putExtra("truckNo", "");
            resultIntent.putExtra("truckname", textData.getString("vehicle"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LandingActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification myNotification = new NotificationCompat.Builder(context)

                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setLargeIcon(Icon_pic)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("Track your Moovo")
                .setSubText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(notiStyle).build();
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, myNotification);
    }
}
