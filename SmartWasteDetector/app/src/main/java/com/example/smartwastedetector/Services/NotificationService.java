package com.example.smartwastedetector.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smartwastedetector.GarbageLocations;
import com.example.smartwastedetector.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NotificationService extends Service {


    String CHANNEL_ID = "Waste";
    DatabaseReference garbageLocationsDB = FirebaseDatabase.getInstance().getReference();
    NotificationManagerCompat notificationManager;
    String userId;

    @Override
    public void onCreate() {
//        if(FirebaseAuth.getInstance().getCurrentUser().getUid() != null){

            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final Intent resultIntent = new Intent(getApplicationContext(), GarbageLocations.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            garbageLocationsDB.child("Detected").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    final Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.appicon);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "Garbage detected";
                        String description = "Order Notifications";
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                        channel.setDescription(description);

                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);


                    }

                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    NotificationCompat.Builder builder;
                    notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    final HashMap<String, Object> notified = new HashMap<String, Object>();
                    Log.d("NOTIFIED ",String.valueOf(map.get("Notified")));

                    if(!Boolean.parseBoolean(String.valueOf(map.get("Notified")))){
                        Log.d("NOTIFIED ",String.valueOf(map.get("Notified")));
                        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.pin)
//                                .setLargeIcon(icon)
                                .setContentTitle("Garbage located ")
                                .setGroup(CHANNEL_ID)
                                .setContentIntent(resultPendingIntent)
                                .addAction(R.drawable.pin, "New garbage location detected", resultPendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH);
                        builder.setSound(alarmSound);
                        notificationManager.notify(1, builder.build());

                        notified.put("Notified",true);
                        garbageLocationsDB.child("Detected").child(dataSnapshot.getKey()).updateChildren(notified);

                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


//        }

        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }


    public NotificationService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}

