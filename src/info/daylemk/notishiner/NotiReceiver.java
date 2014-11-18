package info.daylemk.notishiner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class NotiReceiver extends BroadcastReceiver {
    private static final String TAG = "[NotiReceiver]";
    
    public static final String ACTION = "info.daylemk.bc";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(Logger.TAG, TAG + "received a broadcast : " + intent);
        //android.provider.Telephony.SMS_RECEIVED
        String action = intent.getAction();
        if(action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
            Bundle extras = intent.getExtras();
            if(extras != null){
                Object[] pdus = (Object[]) extras.get("pdus");
                int length = pdus.length;
                SmsMessage[] msgs = new SmsMessage[length];
                StringBuilder smsLog = new StringBuilder();
                for (int i = 0; i < length; i ++){
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    smsLog.append("sms from : " + msgs[i].getOriginatingAddress());
                    smsLog.append("\n " + msgs[i].getMessageBody());
                    smsLog.append("\n");
                }
                Logger.v(TAG + smsLog);
                Toast.makeText(context, smsLog, Toast.LENGTH_LONG).show();
            }
        } else if (action.equals(ACTION)){
//            Toast.makeText(context, "the bc received", Toast.LENGTH_LONG).show();
            // use the old intent to create the new intent
            Intent intentOut = new Intent(intent);
            intentOut.setClass(context, NotiService.class);
            context.startService(intentOut);
        }
    }

}
