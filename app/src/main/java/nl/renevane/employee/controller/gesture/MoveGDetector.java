package nl.renevane.employee.controller.gesture;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

/**

 Qais Safdary
 praktijk 1

 */
public class MoveGDetector extends BaseG_Detector {

    private static final PointF FOCUS_DELTA_ZERO = new PointF();
    private final OnMoveGestureListener Listener;
    private PointF FocusExternal = new PointF();
    private PointF FocusDeltaExternal = new PointF();

    public MoveGDetector(Context context, OnMoveGestureListener listener) {
        super(context);
        Listener = listener;
    }

    @Override
    public void handleStartProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                resetState(); // In case we missed an UP/CANCEL event

                PrevEvent = MotionEvent.obtain(event);
                TimeDelta = 0;

                updateStateByEvent(event);
                break;

            case MotionEvent.ACTION_MOVE:
                GestureInProgress = Listener.onMoveBegin(this);
                break;
        }
    }

    @Override
    public void handleInProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Listener.onMoveEnd(this);
                resetState();
                break;

            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);


                if (CurrPressure / PrevPressure > PRESSURE_THRESHOLD) {
                    final boolean updatePrevious = Listener.onMove(this);
                    if (updatePrevious) {
                        PrevEvent.recycle();
                        PrevEvent = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    public void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);

        final MotionEvent prev = PrevEvent;

        // Focus internal
        PointF mCurrFocusInternal = determineFocalPoint(curr);
        PointF mPrevFocusInternal = determineFocalPoint(prev);

        // Focus external
        // - Prevent skipping of focus delta when a finger is added or removed
        boolean mSkipNextMoveEvent = prev.getPointerCount() != curr.getPointerCount();
        FocusDeltaExternal = mSkipNextMoveEvent ? FOCUS_DELTA_ZERO : new PointF(mCurrFocusInternal.x - mPrevFocusInternal.x, mCurrFocusInternal.y - mPrevFocusInternal.y);

        // - Don't directly use mFocusInternal (or skipping will occur). Add
        // 	 unskipped delta values to FocusExternal instead.
        FocusExternal.x += FocusDeltaExternal.x;
        FocusExternal.y += FocusDeltaExternal.y;
    }


    private PointF determineFocalPoint(MotionEvent e) {
        // Number of fingers on screen
        final int pCount = e.getPointerCount();
        float x = 0f;
        float y = 0f;

        for (int i = 0; i < pCount; i++) {
            x += e.getX(i);
            y += e.getY(i);
        }

        return new PointF(x / pCount, y / pCount);
    }


    public PointF getFocusDelta() {
        return FocusDeltaExternal;
    }




    public static class SimpleOnMoveGestureListener implements OnMoveGestureListener {
        public boolean onMove(MoveGDetector detector) {
            return false;
        }

        public boolean onMoveBegin(MoveGDetector detector) {
            return true;
        }

        public void onMoveEnd(MoveGDetector detector) {
            // Do nothing, overridden implementation may be used
        }
    }
    public interface OnMoveGestureListener {
        boolean onMove(MoveGDetector detector);

        boolean onMoveBegin(MoveGDetector detector);

        void onMoveEnd(MoveGDetector detector);
    }


}
