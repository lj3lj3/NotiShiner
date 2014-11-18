
package info.daylemk.notishiner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_ON) || action.equals(Intent.ACTION_SCREEN_OFF)) {
            Intent intentOut = new Intent(intent);
            intentOut.setClass(context.getApplicationContext(), NotiService.class);
            context.startService(intentOut);
        }
    }
}
