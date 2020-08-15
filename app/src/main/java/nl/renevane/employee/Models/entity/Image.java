package nl.renevane.employee.Models.entity;
/*

Qais Safdary
 praktijk 1
*
* */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Image extends Motion {

    @NonNull
    private final Bitmap bitmap;

    public Image(@NonNull MotionLayer layer,
                 @NonNull Bitmap bitmap,
                 @IntRange(from = 1) int canvasWidth,
                 @IntRange(from = 1) int canvasHeight) {
        super(layer, canvasWidth, canvasHeight);

        this.bitmap = bitmap;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        float widthAspect = 1.0F * canvasWidth / width;
        float heightAspect = 1.0F * canvasHeight / height;
        // fit the smallest size
        holyScale = Math.min(widthAspect, heightAspect);

        // initial position of the entity
        srcPoints[0] = 0;
        srcPoints[1] = 0;
        srcPoints[2] = width;
        srcPoints[3] = 0;
        srcPoints[4] = width;
        srcPoints[5] = height;
        srcPoints[6] = 0;
        srcPoints[7] = height;
        srcPoints[8] = 0;
        srcPoints[9] = 0;
    }

    @Override
    public void drawContent(@NonNull Canvas canvas, @Nullable Paint drawingPaint) {
        canvas.drawBitmap(bitmap, matrix, drawingPaint);
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public void release() {
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

}
