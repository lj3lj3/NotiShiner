
package NotiSurfaceDemo1;

import android.graphics.Color;
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
                    // DEBUG
                    if (Common.DEBUG_CIRCLE) {
                        datas[item].circle_ratio = 0f;
                        break;
                    }

                    // Stage 2
                    // we show the color circle
                    //
                    for (int i = 0; i < NotiSurfaceData.POINT_NUM; i++) {
                        // if the radius not equals zero, we should go
                        // EDIT: this should be 0, 'cause we start with 0, and
                        // we just check if the radius is inited or not
                        if (datas[item].points_radius[i] != 0f) {
                            // make the point grow
                            datas[item].points_radius[i] += NotiSurfaceData.width_step_point;
                            continue;
                        }

                        // we just started, init it
                        if (i == 0) {
                            // set the radius up
                            // EDIT: set the init radius as the max circle
                            // radius, so we can draw next to it
                            datas[item].points_radius[i] = NotiSurfaceData.max_circle_radius;
                            // init the color
                            datas[item].points_color[i] = randomColor();

                            Logger.d(TAG + "the first point : radius : "
                                    + datas[item].points_radius[i] + ", color : "
                                    + datas[item].points_color[i]);
                            // no need for other stuff
                            break;
                        } else {
                            // we have some blank items
                            // if the last one is bigger than point width, we
                            // should create the new one, and set it's radius
                            // and color
                            float radiusTemp;
                            if ((radiusTemp = datas[item].points_radius[i - 1]
                                    - NotiSurfaceData.point_width_boarder) > 0) {
                                Logger.d(TAG + "new point : " + i + ", radiusTemp : " + radiusTemp
                                        + ", color : " + datas[item].points_color[i]);
                                datas[item].points_radius[i] = radiusTemp
                                        + NotiSurfaceData.max_circle_radius;
                                datas[item].points_color[i] = randomColor();
                            }
                        }
                    }

                    // check if the biggest point is big enough
                    int biggest_point = datas[item].biggest_point;
                    if (datas[item].points_radius[biggest_point] > NotiSurfaceData.max_point_radius) {
                        // circle this point radius
                        // EDIT: we should added the circle radius to the point
                        // radius
                        datas[item].points_radius[biggest_point] = datas[item].points_radius[biggest_point]
                                - NotiSurfaceData.max_point_radius
                                + NotiSurfaceData.max_circle_radius;
                        // set the new color???
                        // EDIT: try it
                        datas[item].points_color[biggest_point] = randomColor();
                        // increase the biggest point
                        biggest_point++;
                        // if the point num reach the broader, reset it
                        if (biggest_point == NotiSurfaceData.POINT_NUM) {
                            biggest_point = 0;
                        }
                        // set it back
                        datas[item].biggest_point = biggest_point;

                        Logger.d(TAG + "the biggest point changed : " + biggest_point);
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

    /**
     * get a random colors the every each color parameters begin with 128
     * 
     * @return
     */
    int randomColor() {
        int red = (int) (Math.random() * 256);
        int green = (int) (Math.random() * 256);
        int blue;
        // we should not let the three basic color all bigger than 128 or
        // smaller than 128
        if (red > 128 && green > 128) {
            blue = (int) (Math.random() * 128);
        } else if (red < 128 && green < 128) {
            blue = (int) (Math.random() * 128) + 128;
        } else {
            blue = (int) (Math.random() * 256);
        }
        return Color.rgb(red, green, blue);
    }
}
