
package NotiSurfaceDemo1;

import android.util.Log;
import android.view.animation.OvershootInterpolator;

import info.daylemk.notishiner.Logger;

public class NotiSurfaceCaler implements Runnable {
    static final String TAG = "[NotiSurfaceCaler]";

    // use volatile and package permission
    volatile boolean running = true;

    private NotiSurfaceDemo demo;
    private OvershootInterpolator overshoot;

    private int notifyCount = 0;

    public NotiSurfaceCaler(NotiSurfaceDemo notiSurfaceDemo) {
        this.demo = notiSurfaceDemo;
        overshoot = new OvershootInterpolator(2f);
    }

    @Override
    public void run() {
        Logger.d(TAG + "the caler started");
        NotiSurfaceData[] datas = demo.datas;
        int screenWidth = NotiSurfaceData.screenWidth;
        int screenHeight = NotiSurfaceData.screenHeight;
        int center_x = screenWidth / 2;
        int center_y = screenHeight / 2;
        while (running) {
            if (demo == null) {
                Log.e(Logger.TAG, TAG + "the NotiSurfaceDemo is null??? die");
            }
            if (demo.shouldCal()) {
                // if we should notify the thread, let's do it
                // when the draw thread is down, and we just caled a item
                if (notifyCount == 1) {
                    synchronized (demo) {
                        demo.notify();
                    }
                }

                notifyCount++;

                // call this every time
                int item = demo.nextCalItem();
                int foreItem = demo.foreCalItem(item);
                Logger.d(TAG + "item : " + item + ", fore item : " + foreItem);
                // clone the data from foreItem to now item
                datas[item].cloneData(datas[foreItem]);

                Logger.d(TAG + "cal : " + item + ", draw : " + demo.doneDrawItem);

                // datas[item].bg_color += 100;
                datas[item].circle_x = center_x;
                datas[item].circle_y = center_y;
                datas[item].paint_color += 0x1111;
                if (datas[item].circle_ratio >= 1) {
                    //DEBUG
                    if(Common.DEBUG_CIRCLE){
                        datas[item].circle_ratio = 0f;                    
                    }
                    // do nothing
                } else {
                    // we add the animation of overshoot, so we need
                    // re-calculate the step
                    datas[item].circle_radius = overshoot
                            .getInterpolation((datas[item].circle_ratio + NotiSurfaceData.ratio_step_circle))
                            * NotiSurfaceData.max_circle_radius;
                    // don't forget let ratio grow
                    datas[item].circle_ratio += NotiSurfaceData.ratio_step_circle;
                }
                // set false, so we can know the item is not drew
                datas[item].drew = false;
                // set this
                demo.setDoneCalItem(item);
            } else {
                // if we shouldn't cal, wait until some threads wake me up
                Logger.d(TAG + "the cal should wait, cal : " + demo.doneCalItem + ", draw : "
                        + demo.doneDrawItem);
                try {
                    synchronized (demo) {
                        // TODO find it
                        // no need to notify
                        // EDIT: need it, if we first started, the drew is all
                        // true, and the drawer going to stop, we should wake it
                        if (demo.getDrawerThreadState().compareTo(Thread.State.WAITING) == 0) {
                            Log.w(Logger.TAG, TAG + "the drawer is waiting, wake it");
                            demo.notify();
                        }
                        demo.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // after wake, reset the count to 0
                notifyCount = 0;
            }
        }
        datas = null;
        demo = null;
        Logger.i(TAG + "the caler is dead");
    }
}
