
package NotiSurfaceDemo1;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import info.daylemk.notishiner.Logger;

public class NotiSurfaceDrawer implements Runnable {
    static final String TAG = "[NotiSurfaceDrawer]";

    // use volatile and package permission
    volatile boolean running = true;

    private SurfaceHolder holder;
    private NotiSurfaceDemo demo;
    
    private long lastTime = 0;

    public NotiSurfaceDrawer() {
    }

    public NotiSurfaceDrawer(SurfaceHolder holder, NotiSurfaceDemo notiSurfaceDemo) {
        this.holder = holder;
        demo = notiSurfaceDemo;
    }

    @Override
    public void run() {
        Logger.d(TAG + "drawer started");
        int width = demo.getSurfaceView().getWidth();
        int height = demo.getSurfaceView().getHeight();
        Logger.d(TAG + "width : " + demo.getSurfaceView().getWidth());
        Logger.d(TAG + "height : " + demo.getSurfaceView().getHeight());
        // Bitmap bitmap = Bitmap.createBitmap(demo.getSurfaceView().getWidth(),
        // demo
        // .getSurfaceView().getHeight(), Config.ARGB_8888);;
        // if (holder != null) {
        // Canvas canvas = holder.lockCanvas();
        // canvas.setBitmap(bitmap);
        // holder.unlockCanvasAndPost(canvas);
        // }
        Paint paint = new Paint();
        Paint paintDefault = new Paint();
        NotiSurfaceData[] datas = demo.datas;
        // DEBUG
        // running = false;
        // here, we really going to draw
        while (running) {
            if (holder != null) {
                // call this every time
                int item = demo.nextDrawItem();

                if (item == NotiSurfaceDemo.NOT_AVALIABLE) {
                    // if the item is current not available to draw, take a rest
                    Log.i(Logger.TAG, TAG + "the item is not available to draw, sleep a while");
                    Logger.d(TAG + "cal : " + demo.doneCalItem + ", draw : " + demo.doneDrawItem);
                    try {
                        // if we are out of items, let's notify the caler
                        synchronized (demo) {
                            // if the caler is waiting, wake it
                            if (demo.getCalerThreadState().compareTo(Thread.State.WAITING) == 0) {
                                Log.w(Logger.TAG, TAG + "wake the caler, before we sleep");
                                demo.notify();
                            }
                            // TODO sleep or wait
                            // use wait
                            // Thread.sleep(10);
                            demo.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // not break, man
                    continue;
                }
                Logger.d(TAG + "drawer item : " + item);

                //////////////////////////
                // ------draw----------//
                ////////////////////////
                Canvas canvas = holder.lockCanvas();
                
                paint.setColor(datas[item].bg_color);
                canvas.drawRect(0, 0, width, height, paint);

                paint.setColor(datas[item].circle_color);
                canvas.drawCircle(datas[item].circle_x, datas[item].circle_y,
                        datas[item].circle_radius, paint);
                // canvas.drawBitmap(bitmap, 0, 0, paint);
                
                holder.unlockCanvasAndPost(canvas);
                //////////////////////////
                // ------draw----------//
                ////////////////////////
                // set the this item is drew when we finished
                datas[item].drew = true;
                demo.setDoneDrawItem(item);
                
                //DEBUG
                long timeTemp = SystemClock.elapsedRealtime();
                Logger.w(TAG + "fps : " + 1000/(timeTemp - lastTime));
                lastTime = timeTemp;

                Logger.i(TAG + "drawing : " + datas[item]);

                // if the caler thread is waiting, we should check it
                if (demo.getCalerThreadState().compareTo(Thread.State.WAITING) == 0) {
                    Logger.d(TAG + "the caler is waiting, let's check it");
                    // if true, the cal is not full, wake it
                    if (demo.shouldCal()) {
                        Logger.d(TAG + "try wake the caler");
                        synchronized (demo) {
                            demo.notify();
                        }
                    }
                }
            } else {
                Log.e(Logger.TAG, TAG + "the holder is null??? killed");
                // if the holder is null, kill yourself
                break;
            }
        }
        holder = null;
        demo = null;
        Logger.i(TAG + "the drawer is dead");
    }
}
