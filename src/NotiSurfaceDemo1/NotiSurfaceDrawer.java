
package NotiSurfaceDemo1;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Shader;
import android.graphics.BlurMaskFilter.Blur;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import info.daylemk.notishiner.Logger;

public class NotiSurfaceDrawer implements Runnable {
    static final String TAG = "[NotiSurfaceDrawer]";

    /**
     * the FPS of the drawer
     */
    static final int FPS = 35;

    // use volatile and package permission
    volatile boolean running = true;

    private SurfaceHolder holder;
    private NotiSurfaceDemo demo;

    private long lastTime = 0;
    // Per Frame Last Time
    private int pflt;

    public NotiSurfaceDrawer(SurfaceHolder holder, NotiSurfaceDemo notiSurfaceDemo) {
        this.holder = holder;
        demo = notiSurfaceDemo;
        pflt = 1000 / FPS;
    }

    @Override
    public void run() {
        Logger.d(TAG + "drawer started");
        int width = NotiSurfaceData.screenWidth;
        int height = NotiSurfaceData.screenHeight;
        // Bitmap bitmap = Bitmap.createBitmap(demo.getSurfaceView().getWidth(),
        // demo
        // .getSurfaceView().getHeight(), Config.ARGB_8888);;
        // if (holder != null) {
        // Canvas canvas = holder.lockCanvas();
        // canvas.setBitmap(bitmap);
        // holder.unlockCanvasAndPost(canvas);
        // }
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Paint paintCircle = new Paint();
        BlurMaskFilter maskFilter = new BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID);
        paintCircle.setMaskFilter(maskFilter);
        NotiSurfaceData[] datas = demo.datas;

        // the circle center point
        float circle_x;
        float circle_y;

        // DEBUG
        // running = false;
        // here, we really going to draw
        while (running) {
            if (holder != null) {
                // note time first
                // lastTime = SystemClock.elapsedRealtime();
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
                circle_x = datas[item].circle_x;
                circle_y = datas[item].circle_y;

                // ////////////////////////
                // ------draw-----------//
                // //////////////////////
                Canvas canvas = holder.lockCanvas();
                
                //EDIT: check the canvas here
                if(canvas == null){
                    Logger.w(TAG + "the canvas is null??? retry");
                    continue;
                }

                // draw the background
                canvas.drawColor(datas[item].bg_color);
                // not this
                // paint.setColor(datas[item].bg_color);
                // canvas.drawRect(0, 0, width, height, paint);

                // ///// start draw /////////
                // from the biggest one
                for (int i = datas[item].biggest_point; i < NotiSurfaceData.POINT_NUM; i++) {
                    if (datas[item].points_radius[i] == 0f) {
                        // we reach the broader, break
                        break;
                    } else {
                        // else draw the point
                        paint.setColor(datas[item].points_color[i]);
                        canvas.drawCircle(circle_x, circle_y,
                                datas[item].points_radius[i], paint);
                    }
                }

                // if the biggest point is not 0, we need restart the draw
                if (datas[item].biggest_point != 0) {
                    for (int i = 0; i < datas[item].biggest_point; i++) {
                        // if the biggest point is not zero, means we have
                        // already circled once, so no need to check the zero
                        // again
                        paint.setColor(datas[item].points_color[i]);
                        canvas.drawCircle(circle_x, circle_y, datas[item].points_radius[i], paint);
                    }
                }
                // ////// end draw ////////

                // EDIT: draw the circle last
                // EDIT: use paintCircle instead
                paintCircle.setColor(datas[item].circle_color);
                canvas.drawCircle(circle_x, circle_y,
                        datas[item].circle_radius, paintCircle);
                // canvas.drawBitmap(bitmap, 0, 0, paint);

                holder.unlockCanvasAndPost(canvas);
                // ////////////////////////
                // ------draw-----------//
                // //////////////////////
                // set the this item is drew when we finished
                datas[item].drew = true;
                demo.setDoneDrawItem(item);

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

                long lastTemp = lastTime;
                long timeTemp = SystemClock.elapsedRealtime();
                long timeScape = timeTemp - lastTime;
                lastTime = timeTemp;
                if (timeScape < pflt) {
                    Logger.w(TAG + "timeScape : " + timeScape + ", stand : " + pflt
                            + ", sleep for : "
                            + (pflt - timeScape));
                    try {
                        Thread.sleep(pflt - timeScape);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // DEBUG
                Logger.w(TAG + "fps : " + 1000 / (SystemClock.elapsedRealtime() - lastTemp));
                // set last time after done
                // lastTime = SystemClock.elapsedRealtime();
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
