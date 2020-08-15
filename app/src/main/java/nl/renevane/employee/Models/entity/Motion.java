package nl.renevane.employee.Models.entity;
/*

Qais Safdary
 praktijk 1
*
* */

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings({"WeakerAccess"})
public abstract class Motion {

    @NonNull
    public final MotionLayer layer;


    public final Matrix matrix = new Matrix();

    public final float[] srcPoints = new float[10];

    private final float[] destPoints = new float[10];
    private final PointF pA = new PointF();
    private final PointF pB = new PointF();
    private final PointF pC = new PointF();
    private final PointF pD = new PointF();

    public float holyScale;

    @IntRange(from = 0)
    public int canvasWidth;
    /**
     * height of canvas the entity is drawn in
     */
    @IntRange(from = 0)
    public int canvasHeight;

    private boolean isSelected;
    @NonNull
    private Paint borderPaint = new Paint();

    public Motion(@NonNull MotionLayer layer,
                  @IntRange(from = 1) int canvasWidth,
                  @IntRange(from = 1) int canvasHeight) {
        this.layer = layer;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    private boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


    public void updateMatrix() {
        matrix.reset();

        float topLeftX = layer.getX() * canvasWidth;
        float topLeftY = layer.getY() * canvasHeight;

        float centerX = topLeftX + getWidth() * holyScale * 0.5F;
        float centerY = topLeftY + getHeight() * holyScale * 0.5F;

        // calculate params
        float rotationInDegree = layer.getRotationInDegrees();
        float scaleX = layer.getScale();
        float scaleY = layer.getScale();
        if (layer.isFlipped()) {
            rotationInDegree *= -1.0F;
            scaleX *= -1.0F;
        }



        matrix.preScale(scaleX, scaleY, centerX, centerY);

        matrix.preRotate(rotationInDegree, centerX, centerY);

        matrix.preTranslate(topLeftX, topLeftY);


        matrix.preScale(holyScale, holyScale);
    }

    public float absoluteCenterX() {
        float topLeftX = layer.getX() * canvasWidth;
        return topLeftX + getWidth() * holyScale * 0.5F;
    }

    public float absoluteCenterY() {
        float topLeftY = layer.getY() * canvasHeight;

        return topLeftY + getHeight() * holyScale * 0.5F;
    }

    public PointF absoluteCenter() {
        float topLeftX = layer.getX() * canvasWidth;
        float topLeftY = layer.getY() * canvasHeight;

        float centerX = topLeftX + getWidth() * holyScale * 0.5F;
        float centerY = topLeftY + getHeight() * holyScale * 0.5F;

        return new PointF(centerX, centerY);
    }

    public void moveToCanvasCenter() {
        moveCenterTo(new PointF(canvasWidth * 0.5F, canvasHeight * 0.5F));
    }

    public void moveCenterTo(PointF moveToCenter) {
        PointF currentCenter = absoluteCenter();
        layer.postTranslate(1.0F * (moveToCenter.x - currentCenter.x) / canvasWidth,
                1.0F * (moveToCenter.y - currentCenter.y) / canvasHeight);
    }


    public boolean pointInLayerRect(PointF point) {

        updateMatrix();
        // map rect vertices
        matrix.mapPoints(destPoints, srcPoints);

        pA.x = destPoints[0];
        pA.y = destPoints[1];
        pB.x = destPoints[2];
        pB.y = destPoints[3];
        pC.x = destPoints[4];
        pC.y = destPoints[5];
        pD.x = destPoints[6];
        pD.y = destPoints[7];

        return pointInTriangle(point, pA, pB, pC) || pointInTriangle(point, pA, pD, pC);
    }


    public static boolean pointInTriangle(@NonNull PointF pt, @NonNull PointF v1,
                                          @NonNull PointF v2, @NonNull PointF v3) {

        boolean b1 = crossProduct(pt, v1, v2) < 0.0f;
        boolean b2 = crossProduct(pt, v2, v3) < 0.0f;
        boolean b3 = crossProduct(pt, v3, v1) < 0.0f;

        return (b1 == b2) && (b2 == b3);
    }

    private static float crossProduct(@NonNull PointF a, @NonNull PointF b, @NonNull PointF c) {
        return crossProduct(a.x, a.y, b.x, b.y, c.x, c.y);
    }


    private static float crossProduct(float ax, float ay, float bx, float by, float cx, float cy) {
        return (ax - cx) * (by - cy) - (bx - cx) * (ay - cy);
    }


    public final void draw(@NonNull Canvas canvas, @Nullable Paint drawingPaint) {

        updateMatrix();

        canvas.save();

        drawContent(canvas, drawingPaint);

        if (isSelected()) {
            // get alpha from drawingPaint
            int storedAlpha = borderPaint.getAlpha();
            if (drawingPaint != null) {
                borderPaint.setAlpha(drawingPaint.getAlpha());
            }
            drawSelectedBg(canvas);
            // restore border alpha
            borderPaint.setAlpha(storedAlpha);
        }

        canvas.restore();
    }

    private void drawSelectedBg(Canvas canvas) {
        matrix.mapPoints(destPoints, srcPoints);

        canvas.drawLines(destPoints, 0, 8, borderPaint);

        canvas.drawLines(destPoints, 2, 8, borderPaint);
    }

    @NonNull
    public MotionLayer getLayer() {
        return layer;
    }

    public void setBorderPaint(@NonNull Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    public abstract void drawContent(@NonNull Canvas canvas, @Nullable Paint drawingPaint);

    public abstract int getWidth();

    public abstract int getHeight();

    public void release() {
        // free resources here
    }

    @Override
    public void finalize() throws Throwable {
        try {
            release();
        } finally {

            super.finalize();
        }
    }

}
