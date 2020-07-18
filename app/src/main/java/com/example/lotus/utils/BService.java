package com.example.lotus.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;


import com.example.lotus.R;
import com.example.lotus.ui.SplashActivity;
import com.example.lotus.ui.home.HomeActivity;
import com.example.lotus.ui.notification.NotificationActivity;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;



public class BService extends Service {

    public Socket mSocket;

    public BService that = this;
    SharedPreferences prefs;
    Context ctx;
    static SharedPreferences.Editor configEditor;

    public BService() {
    }

    @Override
    public void onCreate() {


        ctx = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean status = prefs.getBoolean("activityStarted",true);
        Log.d("inside onCreate",status+"");

        if(!status){
            {
                try {
                    Log.d("inside if","oncreate");
                    configEditor = prefs.edit();
                    configEditor.putBoolean("serviceStopped", false);
                    configEditor.commit();
                    Log.d("connecting to server","inside oncreate");
                    mSocket = IO.socket("https://enigmatic-brushlands-5771.herokuapp.com/");
                    mSocket.connect();
                    mSocket.emit("type","service");
                    mSocket.on("oc",onNm);
                    mSocket.on("av",onAv);


                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            Log.d("inside else","oncreate");
            this.onDestroy();
        }

    }

    public void createNotification(String title,String text, Context ctx ){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text);
        Intent resultIntent = new Intent(ctx, NotificationActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(SplashActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {


        ctx = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean status = prefs.getBoolean("activityStarted",true);
        Log.d("inside onStartCommand",status+"");

        if(!status){
            {
                Log.d("inside if","onstartCommand");
                Log.d("service started",status+"");
            }
        } else {
            Log.d("inside else","onstartcommand");
            this.onDestroy();
        }



        return super.onStartCommand(intent,flags,startId);
    }

    private Emitter.Listener onNm = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.d("Inside onOccupied","got num");
            int num;
            try {
                num = data.getInt("num");
            } catch (JSONException e) {
                return;
            }


            Set<String> occupied = prefs.getStringSet("occupied", new HashSet<String>());
            occupied.add(num+"");
            configEditor = prefs.edit();
            configEditor.putStringSet("occupied", occupied);
            configEditor.commit();



            Log.d("value of num", num + "");

//            that.createNotification("Button Pressed",""+num+" Pressed");


        }
    };

    private Emitter.Listener onAv = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.d("onAv","recieved something");

            int num;
            try {
                num = data.getInt("av");
                Log.d("onAvailable",num+"");
            } catch (JSONException e) {
                return;
            }

            Set<String> occupied = prefs.getStringSet("occupied",new HashSet<String>());
            if(occupied.contains(num+"")){
                Log.d("inside if",num+"");
                occupied.remove(num + "");
            }
            configEditor = prefs.edit();
            configEditor.putStringSet("occupied",occupied);
            configEditor.commit();
            Log.d("inside onAv", prefs.getStringSet("occupied", new HashSet<String>()).toString());

//            that.createNotification("Button Available", "" + num + " available");
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        Log.d("inside service destroy","destroy service");
        Log.d("service","disconnecting");

        Log.d("service","disconnected");
        mSocket.off("oc", onNm);
        mSocket.off("av", onAv);
        configEditor = prefs.edit();
        configEditor.putBoolean("serviceStopped",true);
        configEditor.commit();

    }
}