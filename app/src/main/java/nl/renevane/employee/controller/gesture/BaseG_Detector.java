package nl.renevane.employee.controller.gesture;

import android.content.Context;
import android.view.MotionEvent;

/**
 Qais Safdary
 praktijk 1
 */
public abstract class BaseG_Detector {

    public static final float PRESSURE_THRESHOLD = 0.67f;
    public long delta_time;
    public final Context mContext;
    public float current_pressure;
    public boolean GestureInProgress;
    public MotionEvent previous_event;
    public MotionEvent current_event;
   
    public float previous_pressure;
  


    public BaseG_Detector(Context context) {
        mContext = context;
    }



    public abstract void handleStartProgressEvent(int actionCode, MotionEvent event);


    public abstract void handleInProgressEvent(int actionCode, MotionEvent event);


    public void updateStateByEvent(MotionEvent current) {
        final MotionEvent previous = previous_event;

        // Reset current_event
        if (current_event != null) {
            current_event.recycle();
            current_event = null;
        }
        current_event = MotionEvent.obtain(current);


        //To get  Delta time we will subtract current time by prevoius time
        delta_time = current.getEventTime() -previous.getEventTime();

        // Pressure
        current_pressure = current.getPressure(current.getActionIndex());
        previous_pressure = previous.getPressure(previous.getActionIndex());
    }
    //This will reset states like previous and current state
    public void resetState() {
        GestureInProgress = false;

        if (current_event != null) {
            current_event.recycle();
            current_event = null;
        }
        if (previous_event != null) {
            previous_event.recycle();
            previous_event = null;
        }


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
