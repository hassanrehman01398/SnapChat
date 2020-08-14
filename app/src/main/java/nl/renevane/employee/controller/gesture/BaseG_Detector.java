package nl.renevane.employee.controller.gesture;

import android.content.Context;
import android.view.MotionEvent;

/**
 Qais Safdary
 praktijk 1
 */
public abstract class BaseG_Detector {

    public static final float PRESSURE_THRESHOLD = 0.67f;
    public final Context mContext;
    public boolean GestureInProgress;
    public MotionEvent PrevEvent;
    public MotionEvent CurrEvent;
    public float CurrPressure;
    public float PrevPressure;
    public long TimeDelta;


    public BaseG_Detector(Context context) {
        mContext = context;
    }



    public abstract void handleStartProgressEvent(int actionCode, MotionEvent event);


    public abstract void handleInProgressEvent(int actionCode, MotionEvent event);


    public void updateStateByEvent(MotionEvent curr) {
        final MotionEvent prev = PrevEvent;

        // Reset CurrEvent
        if (CurrEvent != null) {
            CurrEvent.recycle();
            CurrEvent = null;
        }
        CurrEvent = MotionEvent.obtain(curr);


        // Delta time
        TimeDelta = curr.getEventTime() - prev.getEventTime();

        // Pressure
        CurrPressure = curr.getPressure(curr.getActionIndex());
        PrevPressure = prev.getPressure(prev.getActionIndex());
    }

    public void resetState() {
        if (PrevEvent != null) {
            PrevEvent.recycle();
            PrevEvent = null;
        }
        if (CurrEvent != null) {
            CurrEvent.recycle();
            CurrEvent = null;
        }
        GestureInProgress = false;
    }




    public void onTouchEvent(MotionEvent event) {
        final int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
        if (!GestureInProgress) {
            handleStartProgressEvent(actionCode, event);
        } else {
            handleInProgressEvent(actionCode, event);
        }
    }

}
