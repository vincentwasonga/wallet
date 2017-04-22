/*
 * Copyright 2017, Peter Vincent
 * Licensed under the Apache License, Version 2.0, Vin Budget.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.octopus.wallet.m.v.w;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class ZoomImageView extends ImageView {

    private static final float DEFAULT_MIN_SCALE = 1f;
    private static final float DEFAULT_MAX_SCALE = 2.5f;
    private static final int CLICK = 3;

    private final ScaleGestureDetector scaleGestureDetector;
    private final Matrix matrix;
    private final PointF lastPointF, startPointF;
    private final float[] values;
    private float minScale, maxScale,
            origWidth, origHeight,
            saveScale;
    private int viewWidth, viewHeight,
            oldMeasuredHeight;
    private Mode mode;

    private enum Mode {
        NONE, DRAG, ZOOM,
    }

    public ZoomImageView(Context context) {
        super(context);
        scaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleListener());
        matrix = new Matrix();
        startPointF = new PointF();
        lastPointF = new PointF();
        values = new float[9];
        minScale = DEFAULT_MIN_SCALE;
        maxScale = DEFAULT_MAX_SCALE;
        saveScale = 1f;
        setImageMatrix(matrix);
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    public ZoomImageView(Context context,
                         AttributeSet attrs) {
        super(context,
                attrs);
        scaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleListener());
        matrix = new Matrix();
        startPointF = new PointF();
        lastPointF = new PointF();
        values = new float[9];
        minScale = DEFAULT_MIN_SCALE;
        maxScale = DEFAULT_MAX_SCALE;
        saveScale = 1f;
        setImageMatrix(matrix);
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    public ZoomImageView(Context context,
                         AttributeSet attrs,
                         int defStyle) {
        super(context,
                attrs,
                defStyle);
        scaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleListener());
        matrix = new Matrix();
        startPointF = new PointF();
        lastPointF = new PointF();
        values = new float[9];
        minScale = DEFAULT_MIN_SCALE;
        maxScale = DEFAULT_MAX_SCALE;
        saveScale = 1f;
        setImageMatrix(matrix);
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setScaleType(ImageView.ScaleType.MATRIX);
        scaleGestureDetector.onTouchEvent(event);
        PointF curr = new PointF(event.getX(),
                event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPointF.set(curr);
                startPointF.set(lastPointF);
                mode = Mode.DRAG;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == Mode.DRAG) {
                    float deltaX = curr.x - lastPointF.x;
                    float deltaY = curr.y - lastPointF.y;
                    float fixTransX = getFixDragTrans(deltaX,
                            viewWidth,
                            origWidth * saveScale);
                    float fixTransY = getFixDragTrans(deltaY,
                            viewHeight,
                            origHeight * saveScale);
                    matrix.postTranslate(fixTransX,
                            fixTransY);
                    fixTrans();
                    lastPointF.set(curr.x,
                            curr.y);
                }
                break;

            case MotionEvent.ACTION_UP:
                mode = Mode.NONE;
                int xDiff = (int) Math.abs(curr.x - startPointF.x);
                int yDiff = (int) Math.abs(curr.y - startPointF.y);
                if (xDiff < CLICK && yDiff < CLICK)
                    performClick();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = Mode.NONE;
                break;

            default:
                return false;
        }

        setImageMatrix(matrix);
        return true;
    }



    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = Mode.ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            float origScale = saveScale;
            saveScale *= mScaleFactor;
            if (saveScale > maxScale) {
                saveScale = maxScale;
                mScaleFactor = maxScale / origScale;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                mScaleFactor = minScale / origScale;
            }

            if (origWidth * saveScale <= viewWidth ||
                    origHeight * saveScale <= viewHeight)
                matrix.postScale(mScaleFactor,
                        mScaleFactor,
                        viewWidth / 2,
                        viewHeight / 2);
            else
                matrix.postScale(mScaleFactor,
                        mScaleFactor,
                        detector.getFocusX(),
                        detector.getFocusY());

            fixTrans();
            return true;
        }
    }

    private void fixTrans() {
        matrix.getValues(values);
        float transX = values[Matrix.MTRANS_X];
        float transY = values[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX,
                viewWidth,
                origWidth * saveScale);
        float fixTransY = getFixTrans(transY,
                viewHeight,
                origHeight * saveScale);

        if (fixTransX != 0 ||
                fixTransY != 0)
            matrix.postTranslate(fixTransX,
                    fixTransY);
    }

    private float getFixTrans(float trans,
                              float viewSize,
                              float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }

    private float getFixDragTrans(float delta,
                                  float viewSize,
                                  float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,
                heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (oldMeasuredHeight == viewWidth &&
                oldMeasuredHeight == viewHeight ||
                viewWidth == 0 ||
                viewHeight == 0)
            return;

        oldMeasuredHeight = viewHeight;

        if (saveScale == 1) {
            float scale;

            Drawable drawable = getDrawable();
            if (drawable == null ||
                    drawable.getIntrinsicWidth() == 0 ||
                    drawable.getIntrinsicHeight() == 0)
                return;
            int bmWidth = drawable.getIntrinsicWidth();
            int bmHeight = drawable.getIntrinsicHeight();

            float scaleX = (float) viewWidth / (float) bmWidth;
            float scaleY = (float) viewHeight / (float) bmHeight;
            scale = Math.min(scaleX,
                    scaleY);
            matrix.setScale(scale,
                    scale);
            float redundantYSpace = (float) viewHeight -
                    (scale *
                            (float) bmHeight);
            float redundantXSpace = (float) viewWidth -
                    (scale *
                            (float) bmWidth);
            redundantYSpace /= (float) 2;
            redundantXSpace /= (float) 2;

            matrix.postTranslate(redundantXSpace,
                    redundantYSpace);

            origWidth = viewWidth - 2 *
                    redundantXSpace;
            origHeight = viewHeight - 2 *
                    redundantYSpace;
            setImageMatrix(matrix);
        }
        fixTrans();
    }
}
