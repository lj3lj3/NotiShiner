
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

    public static final String STATE_IDEL = "state_idel";
    public static final String STATE_CREATING = "state_creating";
    public static final String STATE_CREATED = "state_created";
    public static final String STATE_SURFACE_CHANGING = "state_surface_changing";
    public static final String STATE_SURFACE_CHANGED = "state_surface_changed";
    public static final String STATE_DESTORYING = "state_destorying";
    public static final String STATE_DESTORYED = "state_destoryed";

    // the delay start for the screen on
    private static final int DELAY_START = 2000;

    private NotiSurfaceDemo demo;
    private MyHandler handler;

    private String state = STATE_IDEL;
    private boolean firstMsg = true;

    class MyHandler extends Handler {
        private static final String TAG = "[MyHandler]";

        static final int MSG_DELAY_START = 1;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELAY_START:
                    ready2Draw();
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
        state = STATE_SURFACE_CHANGING;

        Logger.d(TAG + "th surface changed");

        state = STATE_SURFACE_CHANGED;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        state = STATE_CREATING;

        Logger.d(TAG + "th surface created");
        demo = new NotiSurfaceDemo(this);
        demo.surfaceCreated(holder);

        state = STATE_CREATED;
    }

    public void ready2Draw() {
        // if the state is created, let's draw it, else, delay
        // EDIT: we should add surface changed
        if (!firstMsg) {
            if (state.equals(STATE_CREATED) || state.equals(STATE_SURFACE_CHANGED)) {
                demo.draw();
            }
        } else {
            firstMsg = false;
            Logger.w(TAG + "the draw state is : " + state + ", delayed and firstMsg : " + firstMsg);
            handler.sendMessageDelayed(handler.obtainMessage(MyHandler.MSG_DELAY_START),
                    DELAY_START);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        state = STATE_DESTORYING;

        Logger.d(TAG + "the surface is destroying");
        demo.surfaceDestroyed(holder);
        // clean the message queue
        handler.removeCallbacksAndMessages(null);
        demo = null;

        state = STATE_DESTORYED;
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
