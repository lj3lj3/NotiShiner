
package NotiSurfaceDemo1;

import android.util.Log;

import info.daylemk.notishiner.Logger;

public class NotiSurfaceCaler implements Runnable {
    static final String TAG = "[NotiSurfaceCaler]";

    // use volatile and package permission
    volatile boolean running = true;

    private NotiSurfaceDemo demo;

    private int notifyCount = 0;

    public NotiSurfaceCaler(NotiSurfaceDemo notiSurfaceDemo) {
        this.demo = notiSurfaceDemo;
    }

    @Override
    public void run() {
        Logger.d(TAG + "the caler started");
        NotiSurfaceData[] datas = demo.datas;
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

//                datas[item].bg_color += 100;
                datas[item].paint_color += 10;
                datas[item].circle_radius -= 1;
                // set false, so we can know the item is not drawed
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
