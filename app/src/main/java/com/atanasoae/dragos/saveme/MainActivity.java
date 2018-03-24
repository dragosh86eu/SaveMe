package com.atanasoae.dragos.saveme;

/*
 * Project "Save Me"
 * Created by Dragos Atanasoae
 * Email: dragos.atanasoae@gmail.com
 * University "1 Decembrie 1918" Alba-Iulia
 * MIT License
 * Copyright (c) 2018 Dragos Atanasoae
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    DB_Controller controller;
    private boolean doubleBackToExitPressedOnce = false;
    private CameraManager cameraManager;
    private String CM_Id;
    private ImageButton Switch;
    TextView showNumber;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-9034611610433224~5825021415");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (cameraManager != null) {
                CM_Id = cameraManager.getCameraIdList()[0];
            }
        } catch(CameraAccessException e) {
            e.printStackTrace();
        }

        Switch = findViewById(R.id.btnFlashlightSOS);
        Boolean isTorchOn = false;

        Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sos();
            }
        });



        /* Open Map window and get current location on the map
         * */
        ImageButton openWhereIAm = findViewById(R.id.btnWhereIAm);
        openWhereIAm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


        /* Open page send emergency SMS with current  coordinates(latitude & longitude)
         * */
        ImageButton sendToSMS = findViewById(R.id.btnSendMyLocation);
        sendToSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SendLocationSMS.class);
                startActivity(intent);
            }
        });


        /* Emergency call family phone number (we can change the phone number from settings page)
         * */
        ImageButton familyCall = findViewById(R.id.btnCallFamily);
        showNumber = findViewById(R.id.textNumber);

        controller = new DB_Controller(this, "", null, 1);

        controller.show_phone_number(showNumber);

        familyCall.setOnClickListener(new View.OnClickListener() {


            String familyEmergencyNumber = showNumber.getText().toString();


            @Override
            public void onClick(View view) {

                if (familyEmergencyNumber == null){
                    Toast.makeText(MainActivity.this,"Unable to call, please add a family phone number",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(intent);
                } else {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+ familyEmergencyNumber));
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this,"Unable to call, please change app permission",Toast.LENGTH_SHORT).show();
                        buildAlertMessageNoPermissionCall();
                        return;
                    } else {
                        Toast.makeText(MainActivity.this,"Calling family",Toast.LENGTH_SHORT).show();
                    }
                    startActivity(callIntent);

                }

            }
        });



        /* Make Emergency call
         * */
        ImageButton emergencyCallServices = findViewById(R.id.btnEmergencyCall);
        emergencyCallServices.setOnClickListener(new View.OnClickListener() {

            String emergencyCallNumber = getString(R.string.services_emergency_call);

            @Override
            public void onClick(View view) {
                Intent callEIntent = new Intent(Intent.ACTION_CALL);
                callEIntent.setData(Uri.parse("tel:"+ emergencyCallNumber));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Unable to call, please change app permission",Toast.LENGTH_SHORT).show();
                    buildAlertMessageNoPermissionCall();
                    return;
                } else {
                    Toast.makeText(MainActivity.this,"Calling",Toast.LENGTH_SHORT).show();
                }
                startActivity(callEIntent);
            }
        });



        /* Open settings page
         * */
        Button sendToSettings = findViewById(R.id.btnSettings);
        sendToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });


    }

    /* SOS emergency flashlight
     * */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sos() {
        String myString = "1001001";
        Handler handler = new Handler();

        for (int x = 0; x < myString.length(); x++) {
            if (myString.charAt(x) == '0') {
                handler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void run() {
                        turnOff();
                    }
                }, 300);

            } else {
                handler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void run() {
                        turnOn();
                    }
                }, 300);

            }

            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void run() {
                    turnOff();
                }
            }, 300);
        }

        turnOff();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void turnOn() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                cameraManager.setTorchMode(CM_Id, true);
                Switch.setBackgroundResource(R.color.colorButtonTag);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void turnOff() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                cameraManager.setTorchMode(CM_Id, false);
                Switch.setBackgroundResource(R.color.colorButton);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /* Create Alert Dialog for change app permission CALL_PHONE
     * */
    protected void buildAlertMessageNoPermissionCall() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please change PHONE permission for app Save Me")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            startActivity(new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            System.exit(0);

            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit !!!",
                Toast.LENGTH_SHORT).show();

    }


}
