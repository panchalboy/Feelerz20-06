package com.jassimalmunaikh.feelerz;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.commons.text.StringEscapeUtils;

import java.util.Locale;
import java.util.Map;

public class FCMClient extends FirebaseMessagingService {

    String channelId = "";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }


    private void sendNotification(@NonNull RemoteMessage remoteMessage) {

        NotificationChannel channel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Feelerz";
            String description = "Social Media App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            channelId = channel.getId();
        }

        Map<String,String> data = remoteMessage.getData();
        Object[] values = data.values().toArray();


    Intent intent = new Intent(this, DashboardActivity.class);
    intent.putExtra("open_notification",true);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT);

//        val isLeftToRight= TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())== View.LAYOUT_DIRECTION_LTR
//        boolean isleftToright = getResources().getBoolean(R.bool.is_left_to_right);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, this.channelId)
                .setSmallIcon(R.drawable.feelerz3)
                .setContentTitle(values[0].toString())
                .setContentText(Html.fromHtml(StringEscapeUtils.unescapeJava(values[1].toString())))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        UserDataCache.GetInstance().latestFCMToken = s;
        Utilities.getInstance().updateFCMToken(s);
    }
}
