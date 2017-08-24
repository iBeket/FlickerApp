package com.example.milos.flickerapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.milos.flickerapp.MainActivity.baseURL;


/**
 * Created by Milos on 22-Aug-17.
 */

public class JSONService extends Service {

    private static String TAG = "JsonService";
    private JSONPareser pareser;
    private JSONPareser pareserNew;
    private String jsonStrOld;
    private String jsonStrNew;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public synchronized void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
        readWebPage();
    }

    public void readWebPage() {
        pareser = new JSONPareser();
        pareserNew = new JSONPareser();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                jsonStrOld = pareser.makeServiceCall(baseURL);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ScheduledExecutorService scheduleTaskExecutor = Executors.newSingleThreadScheduledExecutor();

                scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        jsonStrNew = pareserNew.makeServiceCall(baseURL);
                        if (!jsonStrOld.equals(jsonStrNew)) {
                            addNotification();
                            jsonStrOld = jsonStrNew;
                        }
                    }
                },0,5, TimeUnit.MINUTES);
            }
        }.execute();
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                        .setContentTitle("FlickrApp")
                        .setContentText("Someone posted new image, check it out")
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        int id = (int) (Math.random() * Integer.MAX_VALUE);
        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }
}
