
package NotiSurfaceDemo1;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceView;

import java.util.Arrays;

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

    int bg_color = Color.BLACK;

    // /////////////////////////////////////
    // /// circle ///////
    // /////////////////////////////////////
    // the time of circle should take, unit:s
    static float time_circle = 1.5f;
    // the max of the circle, set on the run
    static float max_circle_radius = 0f;
    // the original each step of circle should taken, depends on the TIME_CIRCLE
    // and FPS
    // static float STEP_CIRCLE;
    // EDIT: use this now
    // the ratio should circle grow each time, form 0 to 1
    static float ratio_step_circle;
    float circle_radius = 0f;
    float circle_ratio = 0f;
    // we will set this to center of the screen in the init method
    float circle_x = 0f;
    float circle_y = 0f;
    int circle_color = Color.WHITE;
    // /////////////////////////////////////
    // /// circle ///////
    // /////////////////////////////////////

    int paint_color = Color.BLACK;

    // /////////////////////////////////////
    // /// points ///////
    // /////////////////////////////////////
    /**
     * the number of the points
     */
    // init this when init data
    static int POINT_NUM = 0;
    // the array of points radius
    // use array here for better performance
    // EDIT: move int to float
    float[] points_radius;
    // separate the color here, 'cause we mod the color less often
    int[] points_color;
    // the biggest point, we should let this always point the biggest point, so
    // we can find out which one should be verified more faster
    // EDIT: we should store the biggest point in the array
    int biggest_point = 0;
    // the time should a point taken, unit:s
    // EDIT: 0.4s is better :)
    static float time_point = 0.3f;
    // the each step width should point taken
    static float width_step_point = 0f;
    // the max point radius
    static float max_point_radius = 0f;
    // the point width, this should be final
    // this should
    // EDIT: the size is in dp, later should convert to px
    private static final int POINT_WIDTH = 50;
    // the real point width in px
    static float point_width_px;
    // the point width boarder, set it on the run, should be circle width add
    // point width
    static float point_width_boarder;

    // /////////////////////////////////////
    // /// points ///////
    // /////////////////////////////////////

    /**
     * clone the data of this dataTarget to this object
     * 
     * @param data
     */
    public void cloneData(NotiSurfaceData data) {
        // we don't need this
        // drew = dataTarget.drew;

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
            points_color[i] = data.points_color[i];
            points_radius[i] = data.points_radius[i];
        }

        // add the biggest point
        biggest_point = data.biggest_point;
    }

    void init() {
        initPoints();
    }

    /**
     * init the points
     */
    private void initPoints() {
        // init the points radius array
        if (points_radius == null) {
            points_radius = new float[POINT_NUM];
        }
        // init the points color array
        if (points_color == null) {
            points_color = new int[POINT_NUM];
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
        sb.append("circle_ratio:" + circle_ratio + ", ");
        sb.append("circle_x:" + circle_x + ", ");
        sb.append("circle_y:" + circle_y + ", ");
        sb.append("circle_color:" + circle_color + ", ");
        sb.append("biggest point:" + biggest_point + ", ");
        sb.append("points_radius:" + Arrays.toString(points_radius) + ", ");
        sb.append("points_color:" + Arrays.toString(points_color) + ", ");
        sb.append("paint_color:" + paint_color + ", ");
        return sb.toString();
    }

    /**
     * set the size of the screen
     * 
     * @param view
     */
    static void calScreen(SurfaceView view) {
        Resources res = view.getResources();
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

        // the point width should be point_width_px
        point_width_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, POINT_WIDTH,
                res.getDisplayMetrics());
        // the max point radius should be original radius added the POINT_WIDTH
        // EDIT: YES
        max_point_radius = (1 + (int) Math
                .sqrt(Math.pow(screenHeight, 2) + Math.pow(screenWidth, 2))) / 2 + point_width_px;
        // EDIT: we should let the max_point_radius - max_circle_radius equals
        // the POINT_WIDTH is power of n
        max_point_radius = ((int) ((max_point_radius - max_circle_radius) / point_width_px) + 1)
                * point_width_px + max_circle_radius;
        // set the point number
        // NOTE: this should be point_num = *** + 1; wait for confirm
        // EDIT: YES, should be +1
        // EDIT: NO, should be +2, 'cause we needs the second one go reach the
        // broader, so we can dismiss the last one
        // EDIT: none here
        // EDIT: +1
        // EDIT: we should mines the max_circle_radisu and we got the REAL
        // distance
        POINT_NUM = (int) ((max_point_radius - max_circle_radius) / point_width_px) + 1;
        // the width point should each step taken
        width_step_point = point_width_px / (time_point * NotiSurfaceDrawer.FPS);
        // set the width boarder
        point_width_boarder = point_width_px + max_circle_radius;

        Log.d(Logger.TAG, TAG + "points number : " + POINT_NUM);
    }
}
