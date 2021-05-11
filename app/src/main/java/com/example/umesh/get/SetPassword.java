package com.example.umesh.get;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SetPassword extends AppCompatActivity {

    EditText password;
    EditText confirmPassword;
    Button savepasswords;

    private static final int REQUEST_GROUP_PERMISSIONS = 325;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        password = (EditText) findViewById(R.id.edittextpassword);
        confirmPassword = (EditText) findViewById(R.id.edittxtconfirmpassword);
        savepasswords = (Button) findViewById(R.id.savepassword);

        savepasswords.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                String p = password.getText().toString();
                String pp = confirmPassword.getText().toString();

                SharedPreferences sharedPreferences = SetPassword.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.TEXT_PASSWORD), confirmPassword.getText().toString());
                editor.apply();
                try {

                    if (p.isEmpty() || pp.isEmpty()) {
                        Toast.makeText(SetPassword.this, "PLease enter pasword ", Toast.LENGTH_SHORT).show();
                    } else if (p.length() < 4 || pp.length() < 4) {
                        Toast.makeText(SetPassword.this, "PLease 4 digit ", Toast.LENGTH_SHORT).show();

                    } else if (p.equals(pp)) {

                        Intent intent = new Intent(SetPassword.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(SetPassword.this, "Your password has been save ", Toast.LENGTH_SHORT).show();


                        ArrayList<String> permissionsNeeded = new ArrayList<>();
                        ArrayList<String> permissionsAvailable = new ArrayList<>();
                        permissionsAvailable.add(Manifest.permission.READ_CONTACTS);
                        permissionsAvailable.add(Manifest.permission.READ_SMS);

                        for (String permission : permissionsAvailable) {
                            if (ContextCompat.checkSelfPermission(SetPassword.this, permission) != PackageManager.PERMISSION_GRANTED)
                                permissionsNeeded.add(permission);

                        }

                        requestGroupPermission(permissionsNeeded);

                    } else if (p != pp) {
                        Toast.makeText(SetPassword.this, "Password dosent match", Toast.LENGTH_SHORT).show();
                    }


                }
                catch (Exception e) {
                    System.out.println("exception found");
                } finally {
                    System.out.println("I always run");
                }
            }


        });

    }


    private void requestGroupPermission(ArrayList<String> permissions)
    {
        String[] permissionList = new String[permissions.size()];
        permissions.toArray(permissionList);

        ActivityCompat.requestPermissions(SetPassword.this,permissionList,REQUEST_GROUP_PERMISSIONS);
    }





}
