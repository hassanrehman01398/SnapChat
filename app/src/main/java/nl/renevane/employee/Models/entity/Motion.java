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
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings({"WeakerAccess"})
public abstract class Motion {

    /**
     * data
     */
    @NonNull
    public final MotionLayer layer;

    /**
     * transformation matrix for the entity
     */
    public final Matrix matrix = new Matrix();
    /**
     * Initial points of the entity
     *
     * @see #destPoints
     */
    public final float[] srcPoints = new float[10];  // x0, y0, x1, y1, x2, y2, x3, y3, x0, y0
    /**
     * Destination points of the entity
     * 5 points. Size of array - 10; Starting upper left corner, clockwise
     * last point is the same as first to close the circle
     * NOTE: saved as a field variable in order to avoid creating array in draw()-like methods
     */
    private final float[] destPoints = new float[10]; // x0, y0, x1, y1, x2, y2, x3, y3, x0, y0
    private final PointF pA = new PointF();
    private final PointF pB = new PointF();
    private final PointF pC = new PointF();
    private final PointF pD = new PointF();
    /**
     * maximum scale of the initial image, so that
     * the entity still fits within the parent canvas
     */
    public float holyScale;
    /**
     * width of canvas the entity is drawn in
     */
    @IntRange(from = 0)
    public int canvasWidth;
    /**
     * height of canvas the entity is drawn in
     */
    @IntRange(from = 0)
    public int canvasHeight;
    /**
     * true - entity is selected and need to draw it's border
     * false - not selected, no need to draw it's border
     */
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

    /**
     * S - scale matrix, R - rotate matrix, T - translate matrix,
     * L - result transformation matrix
     * <p>
     * The correct order of applying transformations is : L = S * R * T
     * <p>
     * See more info: <a href="http://gamedev.stackexchange.com/questions/29260/transform-matrix-multiplication-order">Game Dev: Transform Matrix multiplication order</a>
     * <p>
     * Preconcat works like M` = M * S, so we apply preScale -> preRotate -> preTranslate
     * the result will be the same: L = S * R * T
     * <p>
     * NOTE: postconcat (postScale, etc.) works the other way : M` = S * M, in order to use it
     * we'd need to reverse the order of applying
     * transformations : post holy scale ->  postTranslate -> postRotate -> postScale
     */
    public void updateMatrix() {
        // init matrix to E - identity matrix
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
            // flip (by X-coordinate) if needed
            rotationInDegree *= -1.0F;
            scaleX *= -1.0F;
        }

        // applying transformations : L = S * R * T

        // scale
        matrix.preScale(scaleX, scaleY, centerX, centerY);

        // rotate
        matrix.preRotate(rotationInDegree, centerX, centerY);

        // translate
        matrix.preTranslate(topLeftX, topLeftY);

        // applying holy scale - S`, the result will be : L = S * R * T * S`
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

    /**
     * For more info:
     * <a href="http://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle">StackOverflow: How to check point is in rectangle</a>
     * <p>NOTE: it's easier to apply the same transformation matrix (calculated before) to the original source points, rather than
     * calculate the result points ourselves
     *
     * @param point point
     * @return true if point (x, y) is inside the triangle
     */
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

    /**

     * @param pt point to check
     * @param v1 vertex 1 of the triangle
     * @param v2 vertex 2 of the triangle
     * @param v3 vertex 3 of the triangle
     * @return true if point (x, y) is inside the triangle
     */
    public static boolean pointInTriangle(@NonNull PointF pt, @NonNull PointF v1,
                                          @NonNull PointF v2, @NonNull PointF v3) {

        boolean b1 = crossProduct(pt, v1, v2) < 0.0f;
        boolean b2 = crossProduct(pt, v2, v3) < 0.0f;
        boolean b3 = crossProduct(pt, v3, v1) < 0.0f;

        return (b1 == b2) && (b2 == b3);
    }

    /**
     * calculates cross product of vectors AB and AC
     *
     * @param a beginning of 2 vectors
     * @param b end of vector 1
     * @param c enf of vector 2
     * @return cross product AB * AC
     */
    private static float crossProduct(@NonNull PointF a, @NonNull PointF b, @NonNull PointF c) {
        return crossProduct(a.x, a.y, b.x, b.y, c.x, c.y);
    }

    /**
     * calculates cross product of vectors AB and AC
     *
     * @param ax X coordinate of point A
     * @param ay Y coordinate of point A
     * @param bx X coordinate of point B
     * @param by Y coordinate of point B
     * @param cx X coordinate of point C
     * @param cy Y coordinate of point C
     * @return cross product AB * AC
     */
    private static float crossProduct(float ax, float ay, float bx, float by, float cx, float cy) {
        return (ax - cx) * (by - cy) - (bx - cx) * (ay - cy);
    }

    /**
     * http://judepereira.com/blog/calculate-the-real-scale-factor-and-the-angle-of-rotation-from-an-android-matrix/
     *
     * @param canvas       Canvas to draw
     * @param drawingPaint Paint to use during drawing
     */
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
        //noinspection Range
        canvas.drawLines(destPoints, 0, 8, borderPaint);
        //noinspection Range
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
            //noinspection ThrowFromFinallyBlock
            super.finalize();
        }
    }

}
