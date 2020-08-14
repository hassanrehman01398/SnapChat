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
                // At least the second finger is on screen now

                resetState(); // In case we missed an UP/CANCEL event
                PrevEvent = MotionEvent.obtain(event);
                TimeDelta = 0;

                updateStateByEvent(event);

                // See if we have a sloppy gesture
                SloppyGesture = isSloppyGesture(event);
                if (!SloppyGesture) {
                    // No, start gesture now
                    GestureInProgress = Listener.onRotateBegin(this);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!SloppyGesture) {
                    break;
                }

                // See if we still have a sloppy gesture
                SloppyGesture = isSloppyGesture(event);
                if (!SloppyGesture) {
                    // No, start normal gesture now
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
                // Gesture ended but
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

                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (CurrPressure / PrevPressure > PRESSURE_THRESHOLD) {
                    final boolean updatePrevious = Listener.onRotate(this);
                    if (updatePrevious) {
                        PrevEvent.recycle();
                        PrevEvent = MotionEvent.obtain(event);
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
        double diffRadians = Math.atan2(PrevFingerDiffY, PrevFingerDiffX) - Math.atan2(CurrFingerDiffY, CurrFingerDiffX);
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
            // Do nothing, overridden implementation may be used
        }
    }
}
