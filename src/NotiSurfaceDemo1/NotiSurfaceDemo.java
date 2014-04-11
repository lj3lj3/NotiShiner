
package NotiSurfaceDemo1;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import info.daylemk.notishiner.Logger;
import info.daylemk.notishiner.NotiSurfaceObject;
import info.daylemk.notishiner.NotiSurfaceView;

public class NotiSurfaceDemo extends NotiSurfaceObject {
    private static final String TAG = "[NotiSurfaceDemo1]";

    private NotiSurfaceDrawer drawer;
    private Thread drawerThread;

    private NotiSurfaceCaler caler;
    private Thread calerThread;

    private static final int MAX_ITEM_COUNT = 3;
    // we access this at drawer thread
    /* private */static final int NOT_AVALIABLE = -1;
    // the calling item, set after cal
    int doneCalItem = 0;
    // the drawing item, set after draw
    // EDIT: every time we going to draw, will move to next, and draw
    int doneDrawItem = MAX_ITEM_COUNT - 1;

    NotiSurfaceData[] datas = new NotiSurfaceData[3];

    public NotiSurfaceDemo(NotiSurfaceView notiSurfaceView) {
        super(notiSurfaceView);
    }

    /**
     * get the next cal item <br/>
     * use default access permission
     * 
     * @return
     */
    int nextCalItem() {
        return doneCalItem == MAX_ITEM_COUNT - 1 ? 0 : doneCalItem + 1;
    }

    /**
     * set the done cal item
     */
    void setDoneCalItem() {
        doneCalItem = doneCalItem == MAX_ITEM_COUNT - 1 ? 0 : doneCalItem + 1;
    }

    void setDoneCalItem(int i) {
        doneCalItem = i;
    }

    /**
     * get the fore cal item
     * 
     * @param i
     * @return
     */
    int foreCalItem(int i) {
        return i == 0 ? MAX_ITEM_COUNT - 1 : --i;
    }

    void setDoneDrawItem() {
        doneDrawItem = doneDrawItem == MAX_ITEM_COUNT - 1 ? 0 : doneDrawItem + 1;
    }

    void setDoneDrawItem(int i) {
        doneDrawItem = i;
    }

    /**
     * get the next draw item <br/>
     * use default access permission
     */
    int nextDrawItem() {
        int nextDrawItemTemp = doneDrawItem == MAX_ITEM_COUNT - 1 ? 0 : doneDrawItem + 1;
        if (datas[nextDrawItemTemp].drew)
            return NOT_AVALIABLE;
        // if the next item is not drew, go on
        // doneDrawItem = nextDrawItemTemp;
        return nextDrawItemTemp;
        /*
         * // if the calculating item is the same as drawing item, return //
         * NOT_AVAILABLE, tell // drawer to wait; if (nowDrawItem == nowCalItem)
         * return NOT_AVALIABLE; // use ++nowDrawItem, for GOD sake return
         * nowDrawItem == MAX_ITEM_COUNT - 1 ? nowDrawItem = 0 : ++nowDrawItem;
         */
    }

    // if the abs value is 2, means we are going to meet the drawer item at next
    // cal, so wait.
    boolean shouldCal() {
        // DEBUG
        for (int i = 0; i < MAX_ITEM_COUNT; i++) {
            Logger.d(TAG + i + " :" + datas[i].drew);
        }
        // if the fore draw item is drawed, we are going to cal
        // EDIT: use now draw item
        // int foreDrawItemTemp = nowDrawItem == 0 ? MAX_ITEM_COUNT - 1 :
        // nowDrawItem - 1;
        if (datas[doneDrawItem].drew)
            return true;
        return false;
        // if the now cal item is drawed, mean we are not meet the drawer, go on
        /*
         * // if the now cal item going to meet draw item,stop if (nowCalItem ==
         * nowDrawItem) return false; return true;
         */
        /*
         * int nowCalItemTemp = nowCalItem; if(nowCalItemTemp < nowDrawItem){
         * nowCalItemTemp += MAX_ITEM_COUNT; } return nowCalItemTemp -
         * nowDrawItem == 2 ? false : true;
         */
    }

    private void initData() {
        // 1. calculate the screen size, and any other things depends on the
        // size of screen
        NotiSurfaceData.calScreen(notiSurfaceView);

        int dataLength = datas.length;

        Logger.v(TAG + "init data, length : " + dataLength);

        // 2. init the the data array, and everything needs in the data object
        for (int i = 0; i < dataLength; i++) {
            datas[i] = new NotiSurfaceData();
            datas[i].initPoints();
        }
    }

    /**
     * set the background of the canvas
     * <br/> should be called after <b>NotiSurfaceData.calScreen</b>
     * @param holder
     */
    private void setBackground() {
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.BLACK);
//        Paint paintTmp = new Paint();
//        paintTmp.setColor(Color.BLACK);
//        // the witdh and height should be ok now
//        canvas.drawRect(0, 0, NotiSurfaceData.screenWidth, NotiSurfaceData.screenHeight, paintTmp);
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * get the drawer thread state
     * 
     * @return
     */
    Thread.State getDrawerThreadState() {
        if (drawerThread != null) {
            return drawerThread.getState();
        }
        return Thread.State.TERMINATED;
    }

    /**
     * get the drawer thread state
     * 
     * @return
     */
    Thread.State getCalerThreadState() {
        if (calerThread != null) {
            return calerThread.getState();
        }
        return Thread.State.TERMINATED;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // don't forget to call the super method
        super.surfaceCreated(holder);
        initData();
        // set the background, so the user will know we are running
        setBackground();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);

        // set it directly
        caler.running = false;
        drawer.running = false;
        // wait for thread dead
        synchronized (this) {
            this.notifyAll();
        }
        try {
            calerThread.join();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        Logger.d(TAG + "caler thread dead");
        // this place, notify should be ok
        synchronized (this) {
            this.notify();
        }
        try {
            drawerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.d(TAG + "drawer thread dead");
    }

    @Override
    public void draw() {
        super.draw();

        caler = new NotiSurfaceCaler(this);
        calerThread = new Thread(caler, NotiSurfaceCaler.TAG);
        calerThread.start();
        // try {
        // synchronized (this) {
        // calerThread.wait();
        // }
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        drawer = new NotiSurfaceDrawer(holder, this);
        drawerThread = new Thread(drawer, NotiSurfaceDrawer.TAG);
        drawerThread.start();
        // try {
        // synchronized (drawerThread) {
        // drawerThread.wait();
        // }
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }
}
