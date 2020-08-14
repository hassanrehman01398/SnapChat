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
                // At least the second finger is on screen now

                resetState(); // In case we missed an UP/CANCEL event
                PrevEvent = MotionEvent.obtain(event);
                TimeDelta = 0;

                updateStateByEvent(event);

                SloppyGesture = isSloppyGesture(event);
                if (!SloppyGesture) {
                    // No, start gesture now
                    GestureInProgress = Listener.onShoveBegin(this);
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

                if (CurrPressure / PrevPressure > PRESSURE_THRESHOLD
                        && Math.abs(getShovePixelsDelta()) > 0.5f) {
                    final boolean updatePrevious = Listener.onShove(this);
                    if (updatePrevious) {
                        PrevEvent.recycle();
                        PrevEvent = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    @Override
    public void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);

        final MotionEvent prev = PrevEvent;
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

        // If it's not traditionally sloppy, we check if the angle between fingers
        // is acceptable.
        double angle = Math.abs(Math.atan2(CurrFingerDiffY, CurrFingerDiffX));
        //about 20 degrees, left or right
        return !((0.0f < angle && angle < 0.35f)
                || 2.79f < angle && angle < Math.PI);
    }

    /**
     * Return the distance in pixels from the previous shove event to the current
     * event.
     *
     * @return The current distance in pixels.
     */
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
            // Do nothing, overridden implementation may be used
        }
    }
}
