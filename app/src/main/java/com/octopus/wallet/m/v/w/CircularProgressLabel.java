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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Func;

public class CircularProgressLabel extends TextView {
    private Paint b_paint, g_Paint, w_paint;
    private RectF rectF;
    private boolean use_percentage = false;
    private int max = 10, progress = 2;
    private int boundary_color = Color.BLUE;
    private int outer_background = Color.DKGRAY;
    private int inner_background = Color.WHITE;
    private int label_color = Color.BLACK;
    private float boundary_size = 1.0F;

    public CircularProgressLabel(Context context) {
        super(context);
        init(null, 0);
    }

    public CircularProgressLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularProgressLabel(Context context,
                                 AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircularProgressLabel, defStyle, 0);

        boundary_color = a.getColor(
                R.styleable.CircularProgressLabel_boundaryColor,
                boundary_color);
       outer_background = a.getColor(
               R.styleable.CircularProgressLabel_outerBackground,
               outer_background);
        inner_background = a.getColor(
                R.styleable.CircularProgressLabel_innerBackground,
                inner_background);
        boundary_size = a.getDimension(
                R.styleable.CircularProgressLabel_boundary_size,
                boundary_size);
        max = a.getInt(
                R.styleable.CircularProgressLabel_max,
                max);
        progress = a.getInt(
                R.styleable.CircularProgressLabel_progress,
                progress);
        use_percentage = a.getBoolean(
                R.styleable.CircularProgressLabel_use_percentage,
                use_percentage);
        label_color = a.getColor(
                R.styleable.CircularProgressLabel_labelColor,
                label_color);
        a.recycle();
        b_paint = new Paint();
        b_paint.setColor(boundary_color);
        b_paint.setStyle(Paint.Style.FILL);
        b_paint.setAntiAlias(true);
        g_Paint = new Paint();
        g_Paint.setColor(outer_background);
        g_Paint.setStyle(Paint.Style.FILL);
        g_Paint.setAntiAlias(true);
        w_paint = new Paint();
        w_paint.setColor(inner_background);
        w_paint.setStyle(Paint.Style.FILL);
        w_paint.setAntiAlias(true);
        rectF = new RectF();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();
        int width = getWidth() - left - right;
        int height = getHeight() - top - bottom;
        int diameter = width > height ?
                height :
                width;
        rectF.set(0,
                0,
                diameter,
                diameter);
        int radius = diameter / 2;
        canvas.drawCircle(width / 2,
                height / 2,
                radius,
                g_Paint);
        float sweepAngle;
        if (max == 0 ||
                progress == 0) {
            sweepAngle = 0;
        }
        else {
            sweepAngle = (float) (progress *
                    Math.pow(max,
                            -1) *
                    360);
        }
        canvas.drawArc(rectF, -90,
                sweepAngle,
                true,
                b_paint);
        String text;
        if (use_percentage) {
            int percent = (int) Func.getPercentage(progress,
                    max);
            text = String.valueOf(percent+"%");
        }
        else {
            text = progress +
                    "/" +
                    max;
        }
        setText(text);
        canvas.drawCircle(width / 2,
                height / 2,
                radius - boundary_size,
                w_paint);
        setGravity(Gravity.CENTER);
        setTextColor(label_color);
        super.onDraw(canvas);
    }
    public void setData(int data,
                        int max) {
        this.max = max;
        this.progress = data;
        draw(new Canvas());
        invalidate();
    }

    public int getBoundary_color() {
        return boundary_color;
    }

    public void setBoundary_color(int boundary_color) {
        this.boundary_color = boundary_color;
    }

    public int getOuter_background() {
        return outer_background;
    }

    public void setOuter_background(int outer_background) {
        this.outer_background = outer_background;
    }

    public int getInner_background() {
        return inner_background;
    }

    public void setInner_background(int inner_background) {
        this.inner_background = inner_background;
    }

    public float getBoundary_size() {
        return boundary_size;
    }

    public void setBoundary_size(float boundary_size) {
        this.boundary_size = boundary_size;
    }
}
