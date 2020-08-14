package nl.renevane.employee.controller.gesture;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

/**

 Qais Safdary
 praktijk 1

 */
//this class is extending base class to call some of the methods of the base classs
public class MoveGDetector extends BaseG_Detector {

    private static final PointF focus_delta_zero = new PointF();
    private final OnMoveGestureListener Listener;
    private PointF focus_external = new PointF();
    private PointF focus_delta_external = new PointF();

    public MoveGDetector(Context context, OnMoveGestureListener listener) {
        super(context);
        Listener = listener;
    }

    @Override
    public void handleStartProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                resetState(); // In case we missed an UP/CANCEL event

                previous_event = MotionEvent.obtain(event);
                delta_time = 0;

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


                if (current_pressure / previous_pressure > PRESSURE_THRESHOLD) {
                    final boolean updatePrevious = Listener.onMove(this);
                    if (updatePrevious) {
                        previous_event.recycle();
                        previous_event = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    public void updateStateByEvent(MotionEvent current) {
        super.updateStateByEvent(current);

        final MotionEvent prev = previous_event;

        PointF mcurrentFocusInternal = determineFocalPoint(current);
        PointF mPrevFocusInternal = determineFocalPoint(prev);

        boolean mSkipNextMoveEvent = prev.getPointerCount() != current.getPointerCount();
        focus_delta_external = mSkipNextMoveEvent ? focus_delta_zero : new PointF(mcurrentFocusInternal.x - mPrevFocusInternal.x, mcurrentFocusInternal.y - mPrevFocusInternal.y);


        focus_external.x += focus_delta_external.x;
        focus_external.y += focus_delta_external.y;
    }


    private PointF determineFocalPoint(MotionEvent event) {

        final int pCount = event.getPointerCount();
        float x_axis = 0f;
        float y_axis = 0f;

        for (int i = 0; i < pCount; i++) {
            x_axis  += event.getX(i);
            y_axis  += event.getY(i);
        }

        return new PointF(x_axis / pCount, y_axis / pCount);
    }


    public PointF getFocusDelta() {
        return focus_delta_external;
    }




    public static class SimpleOnMoveGestureListener implements OnMoveGestureListener {
        public boolean onMove(MoveGDetector detector) {
            return false;
        }

        public boolean onMoveBegin(MoveGDetector detector) {
            return true;
        }

        public void onMoveEnd(MoveGDetector detector) {

        }
    }
    public interface OnMoveGestureListener {
        boolean onMove(MoveGDetector detector);

        boolean onMoveBegin(MoveGDetector detector);

        void onMoveEnd(MoveGDetector detector);
    }


}
