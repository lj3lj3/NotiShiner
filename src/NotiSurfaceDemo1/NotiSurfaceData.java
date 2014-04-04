
package NotiSurfaceDemo1;

import android.graphics.Color;

public class NotiSurfaceData {
    
    // use volatile boolean. 'cause we get this in more than one threads
    // set the drew to true, so our caler can go
    //EDIT: set drew to false, the draw item will go, and wake the cal thread
    volatile boolean drew = false;

    int bg_color = Color.YELLOW;

    int circle_radius = 250;
    int circle_x = 200;
    int circle_y = 200;
    int circle_color = Color.WHITE;

    int paint_color = Color.WHITE;

    /**
     * clone the data of this dataTarget to this obejct
     * 
     * @param data
     */
    public void cloneData(NotiSurfaceData data) {
        // we don't need this
//        drawed = dataTarget.drawed;
        
        bg_color = data.bg_color;

        circle_radius = data.circle_radius;
        circle_x = data.circle_x;
        circle_y = data.circle_y;
        circle_color = data.circle_color;

        paint_color = data.paint_color;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("bg_color:" + bg_color + ", ");
        sb.append("circle_radius:" + circle_radius + ", ");
        sb.append("circle_x:" + circle_x + ", ");
        sb.append("circle_y:" + circle_y + ", ");
        sb.append("circle_color:" + circle_color + ", ");
        sb.append("paint_color:" + paint_color + ", ");
        return sb.toString();
    }
}
