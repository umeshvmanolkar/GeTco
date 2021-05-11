package com.example.umesh.get;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
// import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    //request codes for permissions

    private static final int REQUEST_SMS = 125;
    private static final int REQUEST_CONTCTS = 225;
    private static final int REQUEST_GROUP_PERMISSIONS = 325;

    private static final int TXT_SMS = 1;
    private static final int TXT_CONTACTS = 2;

    //calling of permission util class
    private PermissionUtil permissionUtil;

    //set password activity
    Button set_change_password;
    String text_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        boolean firststart = prefs.getBoolean("firststart",true);

        if (firststart) {
            showStartDialog();
        }


        //set/reset password
        set_change_password = (Button) findViewById(R.id.ChangePassword);
        set_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChangePassword.class);
                startActivity(intent);
            }
        });


        // displaying incoming sms details
        Bundle extras = getIntent().getExtras();

        if (extras != null)

        {
            try {


                String address = extras.getString("MessageNumber");
                String message = extras.getString("Message");


                //splitting of incoming message
                String[] temp = message.split(",");
                String splittedpassword = temp[0];
                String splittedcontactname = temp[1];

                //Getting saved password
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
                text_password = sharedPreferences.getString(getString(R.string.TEXT_PASSWORD), "");

                //Verify password and search for contact
                if (text_password.equals(splittedpassword)) {
                    String str = splittedcontactname;
                    Cursor contacts = getApplicationContext().getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);


                    while (contacts.moveToNext()) {
                     boolean isContains;

                        String contactName = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        if (contactName.length() < 15) {
                            if (contactName.contains(str)) {
                                String conget = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                //Sending searched contact
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(address, null, contactName + "-\n" + conget, null, null);
                                finish();

                            } else {

                                finish();
                            }
                        }
                        else
                        {
                            finish();
                        }

                    }

                }
                //if password is Wrong
                else {
                    if (splittedpassword != text_password) {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(address, null, "You have entered wrong password", null, null);
                        finish();
                    }
                }


            } catch (Exception e) {
                System.out.println("exception found");
            } finally {
                System.out.println("I always run");
            }
        }


        permissionUtil = new PermissionUtil(this);


    }

    private void showStartDialog()
    {
        new AlertDialog.Builder(this)
                .setTitle("Note")
                .setMessage("1.Set the password with minimum 4 characters. \n \n 2.Allow the follwing permissions because Getco needs these permissions to access the contacts and SMS")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, SetPassword.class);
                startActivity(intent);


            }
        })
                .create().show();

        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firststart",false);
        editor.apply();
    }




    //check permission is granted or not
    private int checkPermission(int permission) {
        int status = PackageManager.PERMISSION_DENIED;

        switch (permission) {
            case TXT_SMS:
                status = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
                break;

            case TXT_CONTACTS:
                status = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
                break;
        }
        return status;
    }


    //requesting new permission
    private void requestPermission(int permission) {
        switch (permission) {
            case TXT_SMS:
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_SMS}, REQUEST_SMS);
                break;

            case TXT_CONTACTS:
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTCTS);
                break;

        }
    }


    //Request permission buttons *************
    public void readsms(View view) {
        if (checkPermission(TXT_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (!permissionUtil.checkPermissionPreference("sms")) {
                requestPermission(TXT_SMS);
                permissionUtil.updatePermissionPreferences("sms");
            }
        }
    }

    public void readcontacts(View view) {
        if (checkPermission(TXT_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (!permissionUtil.checkPermissionPreference("contacts")) {
                requestPermission(TXT_CONTACTS);
                permissionUtil.updatePermissionPreferences("contacts");
            }
        }

    }


    private void requestGroupPermission(ArrayList<String> permissions)
    {
        String[] permissionList = new String[permissions.size()];
        permissions.toArray(permissionList);

        ActivityCompat.requestPermissions(MainActivity.this,permissionList,REQUEST_GROUP_PERMISSIONS);
    }


    public void allowpermissions(View view)
    {
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        ArrayList<String> permissionsAvailable = new ArrayList<>();
        permissionsAvailable.add(Manifest.permission.READ_CONTACTS);
        permissionsAvailable.add(Manifest.permission.READ_SMS);

        for (String permission : permissionsAvailable)
        {
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)permissionsNeeded.add(permission);

        }

        requestGroupPermission(permissionsNeeded);

    }
}




