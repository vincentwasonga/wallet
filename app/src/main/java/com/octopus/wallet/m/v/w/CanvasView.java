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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class CanvasView extends View {

    private ArrayList<HUDShape> shapes = new ArrayList<>();

    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context,
                      AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasView(Context context,
                      AttributeSet attrs,
                      int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() -
                paddingLeft -
                paddingRight;
        int contentHeight = getHeight() -
                paddingTop -
                paddingBottom;

        for (HUDShape s : shapes) {
            s.getShape().resize(contentWidth,
                    contentHeight);
            s.draw(canvas);
        }

    }

    public HUDShape addShape(Shape shape,
                             Paint paint) {
        HUDShape hudShape = new HUDShape(shape,
                paint);
        shapes.add(hudShape);
        return hudShape;
    }

    public HUDShape addShape(Shape shape,
                             Paint paint,
                             Paint border) {
        HUDShape hudShape = new HUDShape(shape,
                paint,
                border);
        shapes.add(hudShape);
        return hudShape;
    }

    public void removeShape(HUDShape shape) {
        shapes.remove(shape);
    }

    public void removeShape(int index) {
        shapes.remove(index);
    }

    public void clear() {
        shapes.clear();
    }

    public class HUDShape {
        private final Shape mShape;
        private final Paint mPaint;
        private final Paint mBorder;

        public HUDShape(Shape shape,
                        Paint paint) {
            mShape = shape;
            mPaint = paint;
            mBorder = null;
        }

        public HUDShape(Shape shape,
                        Paint paint,
                        Paint border) {
            mShape = shape;
            mPaint = paint;
            mBorder = border;
            mBorder.setStyle(Paint.Style.STROKE);
        }

        public void draw(Canvas canvas) {
            mShape.draw(canvas, mPaint);

            if (mBorder != null) {
                mShape.draw(canvas, mBorder);
            }
        }

        public Shape getShape() {
            return mShape;
        }
    }

}
