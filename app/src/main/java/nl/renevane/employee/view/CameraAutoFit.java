package nl.renevane.employee.view;
/*

Qais Safdary
 praktijk 1
*
* */

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class CameraAutoFit extends TextureView {

    private int ration_width = 0;
    private int ratio_height = 0;

    public CameraAutoFit(Context context) {
        this(context, null);
    }

    public CameraAutoFit(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraAutoFit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be less then zero");
        }
        ration_width = width;
        ratio_height = height;
        requestLayout();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == ration_width || 0 == ratio_height) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * ration_width / ratio_height) {
                setMeasuredDimension(width, width * ratio_height / ration_width);
            } else {
                setMeasuredDimension(height * ration_width / ratio_height, height);
            }
        }
    }

}
