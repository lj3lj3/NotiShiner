
package info.daylemk.notishiner;

import android.util.Log;
/**
 * this logger will auto disable output for release build
 * @author DayLemK Liu
 *
 */
public class Logger {
    public static final String TAG = "NotiShiner";

    public static void e(String s) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, s);
    }
    
    public static void v(String s) {
        if (BuildConfig.DEBUG)
            Log.v(TAG, s);
    }
    
    public static void i(String s) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, s);
    }
    
    public static void w(String s) {
        if (BuildConfig.DEBUG)
            Log.w(TAG, s);
    }
    
    public static void d(String s) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, s);
    }
}