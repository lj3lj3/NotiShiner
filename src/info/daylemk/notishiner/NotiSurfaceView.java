package info.daylemk.notishiner;

import NotiSurfaceDemo1.NotiSurfaceDemo;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NotiSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "[NotiSurfaceView]";
    
    private NotiSurfaceDemo demo;
    
    public NotiSurfaceView(Context context) {
        super(context);
        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        Logger.d(TAG + "th surface changed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Logger.d(TAG + "th surface created");
        demo = new NotiSurfaceDemo(this);
        demo.surfaceCreated(holder);
        demo.draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.d(TAG + "th surface destoryed");
        // call the destroy method and set it 2 null
        demo.surfaceDestroyed(holder);
        demo = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Logger.d(TAG + "touch : " + event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d(TAG + "keyCode : " + keyCode);
        
        return super.onKeyDown(keyCode, event);
    }
}
