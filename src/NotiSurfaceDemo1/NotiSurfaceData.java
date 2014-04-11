
package NotiSurfaceDemo1;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceView;

import info.daylemk.notishiner.Logger;

/**
 * TODO use direct buffer for better speed
 * 
 * @author DayLemK Liu
 */
public class NotiSurfaceData {
    private static final String TAG = "[NotiSurfaceData]";

    // the screen width and height
    static int screenWidth = 0;
    static int screenHeight = 0;

    // use volatile boolean. 'cause we get this in more than one threads
    // set the drew to true, so our caler can go
    // EDIT: set drew to false, the draw item will go, and wake the cal thread
    volatile boolean drew = false;

    int bg_color = Color.YELLOW;

    // the time of circle should take, unit:s
    static int time_circle = 2;
    // the max of the circle, set on the run
    static float max_circle_radius = 0f;
    // the original each step of circle should taken, depends on the TIME_CIRCLE
    // and fps
//    static float STEP_CIRCLE;
    // the ratio should circle grow, form 0 to 1
    static float ratio_step_circle;
    float circle_radius = 0f;
    float circle_ratio = 0f;
    float circle_x = 200f;
    float circle_y = 200f;
    int circle_color = Color.WHITE;

    int paint_color = Color.BLACK;

    static int POINT_NUM = 0;
    Point[] points;
    static int POINT_RADIUS = 10;

    /**
     * clone the data of this dataTarget to this obejct
     * 
     * @param data
     */
    public void cloneData(NotiSurfaceData data) {
        // we don't need this
        // drawed = dataTarget.drawed;

        bg_color = data.bg_color;

        circle_radius = data.circle_radius;
        // add ratio
        circle_ratio = data.circle_ratio;
        circle_x = data.circle_x;
        circle_y = data.circle_y;
        circle_color = data.circle_color;

        paint_color = data.paint_color;

        clonePoints(data);
    }

    private void clonePoints(NotiSurfaceData data) {
        initPoints();
        // clone the data
        for (int i = 0; i < POINT_NUM; i++) {
            points[i].x = data.points[i].x;
            points[i].y = data.points[i].y;
        }
    }

    void initPoints() {
        // init the points array
        if (points == null) {
            points = new Point[POINT_NUM];
        }
        // init the points
        for (int i = 0; i < POINT_NUM; i++) {
            if (points[i] == null) {
                points[i] = new Point();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("width: " + screenWidth + ", ");
        sb.append("height: " + screenHeight + ", ");
        sb.append("circle_max_radius: " + max_circle_radius + ", ");
        sb.append("bg_color:" + bg_color + ", ");
        sb.append("circle_radius:" + circle_radius + ", ");
        sb.append("circle_x:" + circle_x + ", ");
        sb.append("circle_y:" + circle_y + ", ");
        sb.append("circle_color:" + circle_color + ", ");
        sb.append("paint_color:" + paint_color + ", ");
        return sb.toString();
    }

    /**
     * set the size of the screen
     * 
     * @param view
     */
    static void calScreen(SurfaceView view) {
        screenWidth = view.getWidth();
        screenHeight = view.getHeight();
        Log.d(Logger.TAG, TAG + "the size(wh) : " + screenWidth + ", " + screenHeight);

        int small = Math.min(screenHeight, screenWidth);
        // radius of circle is 3/4 of half screen
        max_circle_radius = small / 2 * 3 / 4;
        // use 1 as float
        ratio_step_circle = 1f / (time_circle * NotiSurfaceDrawer.FPS);
        Log.d(Logger.TAG, TAG + "MAX_CIRCEL_RADIUS : " + max_circle_radius + ", RATIO_CIRCLE : "
                + ratio_step_circle);

        // set the point number
        int pointRadius = (1 + (int) Math
                .sqrt(Math.pow(screenHeight, 2) + Math.pow(screenWidth, 2))) / 2;
        POINT_NUM = pointRadius / POINT_RADIUS;
    }
}
