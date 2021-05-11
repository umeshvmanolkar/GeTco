package com.example.umesh.get;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by umesh on 15/01/2018.
 */

public class PermissionUtil {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PermissionUtil(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preference),Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void updatePermissionPreferences(String permission)
    {
        switch (permission)
        {
            case "sms":
                editor.putBoolean(context.getString(R.string.permission_sms),true);
                editor.commit();
                break;

            case "contacts":
                editor.putBoolean(context.getString(R.string.permission_contacts),true);
                editor.commit();
                break;
        }
    }

    public boolean checkPermissionPreference(String permission)
    {
        boolean isShown = false;
        switch (permission)
        {
            case "sms":
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_sms),false);
                break;

            case "contacts":
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_contacts),false);
                break;
        }

        return isShown;
    }
}
