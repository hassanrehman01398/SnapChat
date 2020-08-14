package nl.renevane.employee.controller.gesture;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 Qais Safdary
 praktijk 1
 */
public abstract class TwoFingerGDetector extends BaseG_Detector {

    private final float EdgeSlop;
    public float PrevFingerDiffX;
    public float PrevFingerDiffY;
    public float CurrFingerDiffX;
    public float CurrFingerDiffY;
    private float CurrLen;
    private float PrevLen;

    public TwoFingerGDetector(Context context) {
        super(context);

        ViewConfiguration config = ViewConfiguration.get(context);
        EdgeSlop = config.getScaledEdgeSlop();
    }

    public static float getRawX(MotionEvent event, int pointerIndex) {
        float offset = event.getX() - event.getRawX();
        if (pointerIndex < event.getPointerCount()) {
            return event.getX(pointerIndex) + offset;
        }
        return 0f;
    }


    public static float getRawY(MotionEvent event, int pointerIndex) {
        float offset = event.getY() - event.getRawY();
        if (pointerIndex < event.getPointerCount()) {
            return event.getY(pointerIndex) + offset;
        }
        return 0f;
    }

    @Override
    public abstract void handleStartProgressEvent(int actionCode, MotionEvent event);

    @Override
    public abstract void handleInProgressEvent(int actionCode, MotionEvent event);

    public void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);

        final MotionEvent prev = PrevEvent;

        CurrLen = -1;
        PrevLen = -1;

        // Previous
        final float px0 = prev.getX(0);
        final float py0 = prev.getY(0);
        final float px1 = prev.getX(1);
        final float py1 = prev.getY(1);
        final float pvx = px1 - px0;
        final float pvy = py1 - py0;
        PrevFingerDiffX = pvx;
        PrevFingerDiffY = pvy;

        // Current
        final float cx0 = curr.getX(0);
        final float cy0 = curr.getY(0);
        final float cx1 = curr.getX(1);
        final float cy1 = curr.getY(1);
        final float cvx = cx1 - cx0;
        final float cvy = cy1 - cy0;
        CurrFingerDiffX = cvx;
        CurrFingerDiffY = cvy;
    }


    public float getCurrentSpan() {
        if (CurrLen == -1) {
            final float cvx = CurrFingerDiffX;
            final float cvy = CurrFingerDiffY;
            CurrLen = (float) Math.sqrt(cvx * cvx + cvy * cvy);
        }
        return CurrLen;
    }


    public float getPreviousSpan() {
        if (PrevLen == -1) {
            final float pvx = PrevFingerDiffX;
            final float pvy = PrevFingerDiffY;
            PrevLen = (float) Math.sqrt(pvx * pvx + pvy * pvy);
        }
        return PrevLen;
    }


    public boolean isSloppyGesture(MotionEvent event) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float mRightSlopEdge = metrics.widthPixels - EdgeSlop;
        float mBottomSlopEdge = metrics.heightPixels - EdgeSlop;

        final float edgeSlop = EdgeSlop;

        final float x0 = event.getRawX();
        final float y0 = event.getRawY();
        final float x1 = getRawX(event, 1);
        final float y1 = getRawY(event, 1);

        boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop || x0 > mRightSlopEdge || y0 > mBottomSlopEdge;
        boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop || x1 > mRightSlopEdge || y1 > mBottomSlopEdge;

        return p0sloppy && p1sloppy || p0sloppy || p1sloppy;
    }

}
