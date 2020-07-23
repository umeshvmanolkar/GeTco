package com.example.umesh.get;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "Message received";


    @Override
    public void onReceive(Context context, Intent intent) {

        getResultData();
        Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);

        Intent smsIntent = new Intent(context,MainActivity.class);
        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        smsIntent.putExtra("MessageNumber",messages.getOriginatingAddress());
        smsIntent.putExtra("Message",messages.getMessageBody());
        context.startActivity(smsIntent);
//        Toast.makeText(context,"Sms from-"+messages.getOriginatingAddress()+ "\n"+ messages.getMessageBody(),Toast.LENGTH_LONG).show();


    }



    }



