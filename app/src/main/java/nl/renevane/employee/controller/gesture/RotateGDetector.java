package nl.renevane.employee.controller.gesture;

import android.content.Context;
import android.view.MotionEvent;

/**
 * Qais Safdary
 * praktijk 1
*/
public class RotateGDetector extends TwoFingerGDetector {

    private final OnRotateGestureListener Listener;
    private boolean SloppyGesture;


    public RotateGDetector(Context context, OnRotateGestureListener listener) {
        super(context);
        Listener = listener;
    }

    @Override
    public void handleStartProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_DOWN:

                resetState();
                previous_event = MotionEvent.obtain(event);
                delta_time = 0;

                updateStateByEvent(event);


                SloppyGesture = isSloppyGesture(event);
                if (!SloppyGesture) {

                    GestureInProgress = Listener.onRotateBegin(this);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!SloppyGesture) {
                    break;
                }

                SloppyGesture = isSloppyGesture(event);
                if (!SloppyGesture) {

                    GestureInProgress = Listener.onRotateBegin(this);
                }

                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (!SloppyGesture) {
                    break;
                }

                break;
        }
    }

    @Override
    public void handleInProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_UP:

                updateStateByEvent(event);

                if (!SloppyGesture) {
                    Listener.onRotateEnd(this);
                }

                resetState();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (!SloppyGesture) {
                    Listener.onRotateEnd(this);
                }

                resetState();
                break;

            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);

                if (current_pressure / previous_pressure> PRESSURE_THRESHOLD) {
                    final boolean updatePrevious = Listener.onRotate(this);
                    if (updatePrevious) {
                        previous_event.recycle();
                        previous_event = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    @Override
    public void resetState() {
        super.resetState();
        SloppyGesture = false;
    }


    public float getRotationDegreesDelta() {
        double diffRadians = Math.atan2(prevoius_finger_diff_Y, prevoius_finger_diff_X) - Math.atan2(current_finger_diff_Y, current_finger_diff_X);
        return (float) (diffRadians * 180 / Math.PI);
    }


    public interface OnRotateGestureListener {
        boolean onRotate(RotateGDetector detector);

        boolean onRotateBegin(RotateGDetector detector);

        void onRotateEnd(RotateGDetector detector);
    }


    public static class SimpleOnRotateGestureListener implements OnRotateGestureListener {
        public boolean onRotate(RotateGDetector detector) {
            return false;
        }

        public boolean onRotateBegin(RotateGDetector detector) {
            return true;
        }

        public void onRotateEnd(RotateGDetector detector) {

        }
    }
}
