package info.daylemk.notishiner;

import android.view.SurfaceHolder;

public abstract class NotiSurfaceObject {
    protected static final String TAG = "[NotiSurfaceObject]";

    protected NotiSurfaceView notiSurfaceView;
    protected SurfaceHolder holder;

    public NotiSurfaceObject(NotiSurfaceView notiSurfaceView) {
        this.notiSurfaceView = notiSurfaceView;
    }
    
    public NotiSurfaceView getSurfaceView (){
        return notiSurfaceView;
    }
    
    public void surfaceCreated(SurfaceHolder holder){
        // hold the holder
        this.holder = holder;
    }
    public void draw (){
     // notify all to go back to work
        // do not forget to sync
        synchronized (this) {
            this.notifyAll();
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder){
        // release the holder
        this.holder = null;
        this.notiSurfaceView = null;
    }
}
