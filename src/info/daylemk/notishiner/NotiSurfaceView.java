
package info.daylemk.notishiner;

import NotiSurfaceDemo1.NotiSurfaceDemo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NotiSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "[NotiSurfaceView]";

    // the delay start for the screen on
    private static final int DELAY_START = 1000;

    private NotiSurfaceDemo demo;
    private MyHandler handler;

    class MyHandler extends Handler {
        private static final String TAG = "[MyHandler]";

        static final int MSG_DELAY_START = 1;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELAY_START:
                    demo.draw();
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }

    public NotiSurfaceView(Context context) {
        super(context);
        this.getHolder().addCallback(this);
        handler = new MyHandler();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        Logger.d(TAG + "th surface changed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logger.d(TAG + "th surface created");
        demo = new NotiSurfaceDemo(this);
        demo.surfaceCreated(holder);
        handler.sendMessageDelayed(handler.obtainMessage(MyHandler.MSG_DELAY_START), DELAY_START);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.d(TAG + "th surface destoryed");
        demo.surfaceDestroyed(holder);
        // clean the message queue
        handler.removeCallbacksAndMessages(null);
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
