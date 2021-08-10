package com.moheeeetgupta.ladybuddy;


import android.Manifest;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;


public class ScreenOnOffReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;
    public final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";
    private int powerBtnTapCount = 0;
    FusedLocationProviderClient fusedLocationProviderClient;
    String Value1, Value2, Value3, Value4, Value;

    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction ();
        if (Intent.ACTION_SCREEN_OFF.equals (action)) {
            powerBtnTapCount++;
            Log.d (SCREEN_TOGGLE_TAG, "Screen is turn off." + powerBtnTapCount);
        } else if (Intent.ACTION_SCREEN_ON.equals (action)) {
            powerBtnTapCount++;
            Log.d (SCREEN_TOGGLE_TAG, "Screen is turn on." + powerBtnTapCount);
        }
        if ((powerBtnTapCount % 4 == 0 ) && (powerBtnTapCount/4) % 2 == 1) {
////            SmsActivity s=new SmsActivity ();
////            s.tryIt ();
//            // Get instance of Vibrator from current Context
//            Vibrator v = (Vibrator) context.getSystemService (VIBRATOR_SERVICE);
//
//// Vibrate for 400 milliseconds
//            v.vibrate(1000);
//            //Getting intent and PendingIntent instance
//            Intent pintent=new Intent(context.getApplicationContext (),ScreenOnOffReceiver.class);
//            PendingIntent pi=PendingIntent.getActivity(context.getApplicationContext (), 0, pintent,0);
//            //Get the SmsManager instance and call the sendTextMessage method to send message
//            SmsManager sms= SmsManager.getDefault();
//            sms.sendTextMessage("6299009237", null, "Hello ladybuddy", pi,null);
//            sms.sendTextMessage("9304579433", null, "Hello ladybuddy", pi,null);
            //Getting the value of shared preference back
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (context.getApplicationContext ());

            SharedPreferences getShared = context.getSharedPreferences ("demo", MODE_PRIVATE);
            Value1 = getShared.getString ("phone1", "").trim ();

            Value2 = getShared.getString ("phone2", "").trim ();

            Value3 = getShared.getString ("phone3", "").trim ();

            Value4 = getShared.getString ("phone4", "").trim ();

            Value = getShared.getString ("msg", "I am in danger, please come fast...").trim ();

            tryIt (context);

            Log.d ("jkjkl", Value1 + " " + Value2 + " " + Value3 + " " + Value4 + " " + Value + " ");

        }
        if(powerBtnTapCount % 8==0){
            startSiren();
        }
    }

    public void tryIt(Context context) {
        SendLocationMessage (context);
//         Get instance of Vibrator from current Context
//        Vibrator v;
//        v=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
//        // Vibrate for 500 milliseconds
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//        } else {
//            //deprecated in API 26
//            v.vibrate(500);
//        }
//

        //Calling function


        Intent intent = new Intent (Intent.ACTION_CALL);
        String phoneNumber = Value1;
        intent.setData (Uri.parse ("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission (context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity (intent);

    }

    public  void startSiren(){
        mediaPlayer = MediaPlayer.create (fusedLocationProviderClient.getApplicationContext (), R.raw.police_siren);
        mediaPlayer.start ();
        mediaPlayer.setLooping (true);
    }

    private void SendLocationMessage(final Context context) {
        if (ActivityCompat.checkSelfPermission (context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation ().addOnCompleteListener (new OnCompleteListener<Location> () {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize Location
                Location location = task.getResult ();
                if (location != null) {

                    try {
                        //Initialize Geocoder
                        Geocoder geocoder = new Geocoder (context, Locale.getDefault ());
                        //Initialize adress list
                        List<Address> addresses = geocoder.getFromLocation (
                                location.getLatitude (), location.getLongitude (), 1
                        );
                        String phoneNumber1 = Value1;
                        String phoneNumber2 =Value2;
                        String phoneNumber3 = Value3;
                        String phoneNumber4 = Value4;
                        String Message = Value;
                        if (!Value1.equals ("") || !Value2.equals ("") || !Value3.equals ("") || !Value4.equals ("")) {
                            if (!Value1.equals ("")) {
                                SmsManager smsManager = SmsManager.getDefault ();
                                smsManager.sendTextMessage (phoneNumber1, null, Message + "I am at " + addresses.get (0).getLatitude () +
                                        "," + addresses.get (0).getLongitude () + ", " + addresses.get (0).getCountryName () +
                                        "," + addresses.get (0).getLocality () + ", " + addresses.get (0).getAddressLine (0), null, null);
                            }

                            if (!Value2.equals ("")) {
                                SmsManager smsManager = SmsManager.getDefault ();
                                smsManager.sendTextMessage (phoneNumber2, null, Message + "I am at " + addresses.get (0).getLatitude () +
                                        "," + addresses.get (0).getLongitude () + ", " + addresses.get (0).getCountryName () +
                                        "," + addresses.get (0).getLocality () + ", " + addresses.get (0).getAddressLine (0), null, null);
                            }

                            if (!Value3.equals ("")) {
                                SmsManager smsManager = SmsManager.getDefault ();
                                smsManager.sendTextMessage (phoneNumber3, null, Message + "I am at " + addresses.get (0).getLatitude () +
                                        "," + addresses.get (0).getLongitude () + ", " + addresses.get (0).getCountryName () +
                                        "," + addresses.get (0).getLocality () + ", " + addresses.get (0).getAddressLine (0), null, null);
                            }
                            if (!Value4.equals ("")) {
                                SmsManager smsManager = SmsManager.getDefault ();
                                smsManager.sendTextMessage (phoneNumber4, null, Message + "I am at " + addresses.get (0).getLatitude () +
                                        "," + addresses.get (0).getLongitude () + ", " + addresses.get (0).getCountryName () +
                                        "," + addresses.get (0).getLocality () + ", " + addresses.get (0).getAddressLine (0), null, null);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace ();
                    }

                }
            }
        });
    }
}