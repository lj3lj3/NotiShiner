
package info.daylemk.notishiner;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class NotiService extends Service {
    private static final String TAG = "[NotiService]";

    private NotiSurfaceView view;
    private ScreenReceiver screenReceiver;

    // public NotiService() {
    // super(TAG);
    // }

    // @Override
    // protected void onHandleIntent(Intent intent) {
    // // TODO Auto-generated method stub
    // String action = intent.getAction();
    // if (action.equals(NotiReceiver.ACTION)) {
    // Log.v(Logger.TAG, TAG + "the bc received");
    // // KeyguardManager km = (KeyguardManager)
    // // this.getSystemService(Context.KEYGUARD_SERVICE);
    // // km.
    //
    // // PowerManager pm = (PowerManager)
    // // this.getSystemService(Context.POWER_SERVICE);
    // // WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
    // // | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
    // // wakeLock.acquire(5000);
    // // Logger.d(TAG + "acquire");
    // // Toast.makeText(this, "showing", Toast.LENGTH_SHORT).show();
    // // try {
    // // Thread.sleep(5000);
    // // } catch (InterruptedException e) {
    // // // TODO Auto-generated catch block
    // // e.printStackTrace();
    // // } finally {
    // //
    // // }
    //
    // // wakeLock.release();
    // // Logger.d(TAG + "drop");
    //
    // addView();
    // }
    // }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (view != null) {
            WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
            wm.removeView(view);
        } else {
            Logger.w(TAG + "the view in null???");
        }

        unregisterReceiver();

        Logger.d(TAG + "the service is destroying");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(NotiReceiver.ACTION)) {
            Log.v(Logger.TAG, TAG + "the bc received");
            // KeyguardManager km = (KeyguardManager)
            // this.getSystemService(Context.KEYGUARD_SERVICE);
            // km.

            // PowerManager pm = (PowerManager)
            // this.getSystemService(Context.POWER_SERVICE);
            // WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
            // | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
            // wakeLock.acquire(5000);
            // Logger.d(TAG + "acquire");
            // Toast.makeText(this, "showing", Toast.LENGTH_SHORT).show();
            // try {
            // Thread.sleep(5000);
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // } finally {
            //
            // }

            // wakeLock.release();
            // Logger.d(TAG + "drop");

            addView();
            // register the screen receiver
            registerScreenReceiver();
        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            // TODO we unregister the receiver here first
            Logger.i(TAG + "the screen is on");
            // EDIT: not unreister now
            // unregisterReceiver();

            view.ready2Draw();
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Logger.i(TAG + "the screen is off, stop yourself");
            stopSelf();
        } else {
            // EDIT: try stop here
            // EDIT: not
//            stopSelf();
//            Logger.i(TAG + "stop the service on the start");
        }

        // if it's get killed, don't run it
        return START_NOT_STICKY;
    }

    private View addView() {
        NotiSurfaceView view = new NotiSurfaceView(getApplicationContext());
        this.view = view;
        // view.setBackgroundColor(Color.CYAN);
        WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        // RelativeLayout.LayoutParams.MATCH_PARENT,
        // RelativeLayout.LayoutParams.MATCH_PARENT);
        // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ViewGroup.LayoutParams viewGroupParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams winParams = new WindowManager.LayoutParams();
        // winParams.width = 50;
        winParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        // winParams.height = 50;
        winParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        // winParams.alpha = 0.8f;
        winParams.format = PixelFormat.RGBA_8888;
        winParams.gravity = Gravity.CENTER;
        // winParams.layoutAnimationParameters = LayoutAnimationController.
        winParams.packageName = this.getPackageName();
        // winParams.rotationAnimation = WindowManager.LayoutParams.ro;
        winParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        winParams.windowAnimations = android.R.style.Animation_InputMethod;
        winParams.flags =
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        // |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        ;
        Logger.d(TAG + "here");
        wm.addView(view, winParams);
        return view;
    }

    private void registerScreenReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenReceiver = new ScreenReceiver();
        this.registerReceiver(screenReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (screenReceiver != null) {
            this.unregisterReceiver(screenReceiver);
            screenReceiver = null;
        } else {
            Logger.w(TAG + "the screen receiver is null when unreigster the receiver");
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}
