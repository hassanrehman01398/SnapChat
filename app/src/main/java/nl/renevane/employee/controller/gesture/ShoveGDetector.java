package nl.renevane.employee.controller.gesture;

import android.content.Context;
import android.view.MotionEvent;

/**
 Qais Safdary
 praktijk 1
 */
public class ShoveGDetector extends TwoFingerGDetector {

    private final OnShoveGestureListener Listener;
    private float PrevAverageY;
    private float CurrAverageY;
    private boolean SloppyGesture;

    public ShoveGDetector(Context context, OnShoveGestureListener listener) {
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

                    GestureInProgress = Listener.onShoveBegin(this);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!SloppyGesture) {
                    break;
                }


                SloppyGesture = isSloppyGesture(event);
                if (!SloppyGesture) {

                    GestureInProgress = Listener.onShoveBegin(this);
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
                    Listener.onShoveEnd(this);
                }

                resetState();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (!SloppyGesture) {
                    Listener.onShoveEnd(this);
                }

                resetState();
                break;

            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);

                if (current_pressure / previous_pressure> PRESSURE_THRESHOLD
                        && Math.abs(getShovePixelsDelta()) > 0.5f) {
                    final boolean updatePrevious = Listener.onShove(this);
                    if (updatePrevious) {
                        previous_event.recycle();
                        previous_event = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    @Override
    public void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);

        final MotionEvent prev = previous_event;
        float py0 = prev.getY(0);
        float py1 = prev.getY(1);
        PrevAverageY = (py0 + py1) / 2.0f;

        float cy0 = curr.getY(0);
        float cy1 = curr.getY(1);
        CurrAverageY = (cy0 + cy1) / 2.0f;
    }

    @Override
    public boolean isSloppyGesture(MotionEvent event) {
        boolean sloppy = super.isSloppyGesture(event);
        if (sloppy)
            return true;

        double angle = Math.abs(Math.atan2(current_finger_diff_Y, current_finger_diff_X));

        return !((0.0f < angle && angle < 0.35f)
                || 2.79f < angle && angle < Math.PI);
    }


    public float getShovePixelsDelta() {
        return CurrAverageY - PrevAverageY;
    }

    @Override
    public void resetState() {
        super.resetState();
        SloppyGesture = false;
        PrevAverageY = 0.0f;
        CurrAverageY = 0.0f;
    }


    public interface OnShoveGestureListener {
        boolean onShove(ShoveGDetector detector);

        boolean onShoveBegin(ShoveGDetector detector);

        void onShoveEnd(ShoveGDetector detector);
    }


    public static class SimpleOnShoveGestureListener implements OnShoveGestureListener {
        public boolean onShove(ShoveGDetector detector) {
            return false;
        }

        public boolean onShoveBegin(ShoveGDetector detector) {
            return true;
        }

        public void onShoveEnd(ShoveGDetector detector) {

        }
    }
}
