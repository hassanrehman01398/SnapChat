package nl.renevane.employee.view;
/*

Qais Safdary
 praktijk 1
*
* */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import nl.renevane.employee.Models.entity.Motion;
import nl.renevane.employee.R;
import nl.renevane.employee.controller.gesture.MoveGDetector;
import nl.renevane.employee.controller.gesture.RotateGDetector;


public class MotionView extends FrameLayout {

    // layers
    private final List<Motion> entities = new ArrayList<>();
    @Nullable
    private Motion selectedEntity;
    private Paint selectedLayerPaint;
    // callback
    @Nullable
    private MotionViewCallback motionViewCallback;
    // gesture detection
    private ScaleGestureDetector scaleGestureDetector;
    private RotateGDetector RotateGDetector;
    private MoveGDetector MoveGDetector;
    private GestureDetectorCompat gestureDetectorCompat;
    private final OnTouchListener onTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            view.performClick();
            if (scaleGestureDetector != null) {
                scaleGestureDetector.onTouchEvent(event);
                RotateGDetector.onTouchEvent(event);
                MoveGDetector.onTouchEvent(event);
                gestureDetectorCompat.onTouchEvent(event);
            }
            return true;
        }
    };

    // constructors
    public MotionView(Context context) {
        super(context);
        init(context);
    }

    public MotionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MotionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressWarnings("unused")
   public MotionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context) {
        setWillNotDraw(false);

        selectedLayerPaint = new Paint();
        selectedLayerPaint.setAlpha((int) (255 * Constants.SELECTED_LAYER_ALPHA));
        selectedLayerPaint.setAntiAlias(true);

        // init listeners
        this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.RotateGDetector = new RotateGDetector(context, new RotateListener());
        this.MoveGDetector = new MoveGDetector(context, new MoveListener());
        this.gestureDetectorCompat = new GestureDetectorCompat(context, new TapsListener());

        setOnTouchListener(onTouchListener);
        updateUI();
    }

    @Nullable
    public Motion getSelectedEntity() {
        return selectedEntity;
    }

    public List<Motion> getEntities() {
        return entities;
    }

    public void setMotionViewCallback(@Nullable MotionViewCallback callback) {
        this.motionViewCallback = callback;
    }

    public void addEntity(@Nullable Motion entity) {
        if (entity != null) {
            entities.add(entity);
            selectEntity(entity, false);
        }
    }

    public void deleteEntity(@Nullable Motion entity) {
        if (entity != null) {
            entities.remove(entity);
            selectEntity(null, true);
        }
    }

    public void addEntityAndPosition(@Nullable Motion entity) {
        if (entity != null) {
            initEntityBorder(entity);
            initialTranslateAndScale(entity);
            entities.add(entity);
            selectEntity(entity, true);
        }
    }

    private void initEntityBorder(@NonNull Motion entity) {
        // init stroke
        int strokeSize = getResources().getDimensionPixelSize(R.dimen.stroke_size);
        Paint borderPaint = new Paint();

        borderPaint.setStrokeWidth(strokeSize);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(ContextCompat.getColor(getContext(), R.color.stroke_color));

        entity.setBorderPaint(borderPaint);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);


        if (selectedEntity != null) {
            selectedEntity.draw(canvas, selectedLayerPaint);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawAllEntities(canvas);
        super.onDraw(canvas);
    }

    /**
     * draws all entities on the canvas
     *
     * @param canvas Canvas where to draw all entities
     */
    private void drawAllEntities(Canvas canvas) {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).draw(canvas, null);
        }
    }


    public Bitmap getMotionViewBitmap() {
        selectEntity(null, false);

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawAllEntities(canvas);
        return bitmap;
    }

    private void updateUI() {
        invalidate();
    }

    private void handleTranslate(PointF delta) {
        if (selectedEntity != null) {
            float newCenterX = selectedEntity.absoluteCenterX() + delta.x;
            float newCenterY = selectedEntity.absoluteCenterY() + delta.y;
            // limit entity center to screen bounds
            boolean needUpdateUI = false;
            if (newCenterX >= 0 && newCenterX <= getWidth()) {
                selectedEntity.getLayer().postTranslate(delta.x / getWidth(), 0.0F);
                needUpdateUI = true;
            }
            if (newCenterY >= 0 && newCenterY <= getHeight()) {
                selectedEntity.getLayer().postTranslate(0.0F, delta.y / getHeight());
                needUpdateUI = true;
            }
            if (needUpdateUI) {
                updateUI();
            }
        }
    }

    private void initialTranslateAndScale(@NonNull Motion entity) {
        entity.moveToCanvasCenter();
        entity.getLayer().setScale(entity.getLayer().initialScale());
    }

    private void selectEntity(@Nullable Motion entity, boolean updateCallback) {
        if (selectedEntity != null) {
            selectedEntity.setIsSelected(false);
        }
        if (entity != null) {
            entity.setIsSelected(true);
        }
        selectedEntity = entity;
        invalidate();
        if (updateCallback && motionViewCallback != null) {
            motionViewCallback.onEntitySelected(entity);
        }
    }

    public void unselectEntity() {
        if (selectedEntity != null) {
            selectEntity(null, true);
        }
    }

    @Nullable
    private Motion findEntityAtPoint(float x, float y) {
        Motion selected = null;
        PointF p = new PointF(x, y);
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i).pointInLayerRect(p)) {
                selected = entities.get(i);
                break;
            }
        }
        return selected;
    }

    private void updateSelectionOnTap(MotionEvent e) {
        Motion entity = findEntityAtPoint(e.getX(), e.getY());
        selectEntity(entity, true);
    }

    private void updateOnLongPress(MotionEvent e) {
        // if layer is currently selected and point inside layer - move it to front
        if (selectedEntity != null) {
            PointF p = new PointF(e.getX(), e.getY());
            if (selectedEntity.pointInLayerRect(p)) {
                bringLayerToFront(selectedEntity);
            }
        }
    }

    private void bringLayerToFront(@NonNull Motion entity) {
        // bring layer to front by removing and adding
        if (entities.remove(entity)) {
            entities.add(entity);
            invalidate();
        }
    }

    private void moveEntityToBack(@Nullable Motion entity) {
        if (entity == null) {
            return;
        }
        if (entities.remove(entity)) {
            entities.add(0, entity);
            invalidate();
        }
    }

    public void flipSelectedEntity() {
        if (selectedEntity == null) {
            return;
        }
        selectedEntity.getLayer().flip();
        invalidate();
    }

    public void moveSelectedBack() {
        moveEntityToBack(selectedEntity);
    }

    public void deletedSelectedEntity() {
        if (selectedEntity == null) {
            return;
        }
        if (entities.remove(selectedEntity)) {
            selectedEntity.release();
            selectedEntity = null;
            invalidate();
        }
    }

    // free up memory
    public void release() {
        for (Motion entity : entities) {
            entity.release();
        }
    }

    public interface Constants {
        float SELECTED_LAYER_ALPHA = 0.15F;
    }

    // gesture detectors

    public interface MotionViewCallback {
        void onEntitySelected(@Nullable Motion entity);

        void onEntityDoubleTap(@NonNull Motion entity);
    }

    private class TapsListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (motionViewCallback != null && selectedEntity != null) {
                motionViewCallback.onEntityDoubleTap(selectedEntity);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            updateOnLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            updateSelectionOnTap(e);
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (selectedEntity != null) {
                float scaleFactorDiff = detector.getScaleFactor();

                selectedEntity.getLayer().postScale(scaleFactorDiff);
                updateUI();
            }
            return true;
        }
    }

    private class RotateListener extends RotateGDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGDetector detector) {
            if (selectedEntity != null) {
                selectedEntity.getLayer().postRotate(-detector.getRotationDegreesDelta());
                updateUI();
            }
            return true;
        }
    }

    private class MoveListener extends MoveGDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onMove(MoveGDetector detector) {
            handleTranslate(detector.getFocusDelta());
            return true;
        }
    }

}