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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static com.atanasoae.dragos.saveme.MapsActivity.PERMISSION_ALL;

public class SettingsActivity extends AppCompatActivity{

    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    Switch onOffService;
    DB_Controller controller;
    EditText inputPhoneNumber;
    TextView showNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        inputPhoneNumber = findViewById(R.id.editPhoneNumber);
        showNumber = findViewById(R.id.showFamilyPhone);
        ImageButton GPSonOff = findViewById(R.id.btnOnOffGPS);
        onOffService = findViewById(R.id.switch1);

        controller = new DB_Controller(this, "", null, 1);



        GPSonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(1);
            }
        });


        controller.show_phone_number(showNumber);


        Button save_profile = findViewById(R.id.save_settings);
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


    }


    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "If your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }



    public void btn_click(View view) {
        switch (view.getId()){
            case R.id.btnAddPhone:
                try {
                    if(showNumber.getText().toString().equals("")){
                        controller.insert_phone_number(inputPhoneNumber.getText().toString());
                        controller.show_phone_number(showNumber);
                        Toast.makeText(SettingsActivity.this, "Phone number added successfully!", Toast.LENGTH_SHORT).show();
                    }else{
                        controller.update_phone_number(showNumber.getText().toString(), inputPhoneNumber.getText().toString());
                        controller.show_phone_number(showNumber);
                        Toast.makeText(SettingsActivity.this, "Phone number updated successfully!", Toast.LENGTH_SHORT).show();
                    }

                } catch (SQLException e){
                    Toast.makeText(SettingsActivity.this, "Already exist", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnDeletePhone:
                controller.delete_phone_number(inputPhoneNumber.getText().toString());
                controller.show_phone_number(showNumber);
                Toast.makeText(SettingsActivity.this, "Phone number deleted successfully!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
