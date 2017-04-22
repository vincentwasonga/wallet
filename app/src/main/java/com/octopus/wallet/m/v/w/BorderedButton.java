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
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.Button;

import com.octopus.wallet.R;

public class BorderedButton extends Button {
    private boolean smooth_corner = true;
    private float border_radius = 1f;
    private int border_color =
            getResources().getColor(R.color.colorAccent);
    private RectF rectF;
    private Paint paint;


    public BorderedButton(Context context) {
        super(context);
        init(null, 0);
    }

    public BorderedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BorderedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BorderedButton, defStyle, 0);
        border_color = a.getColor(
                R.styleable.BorderedButton_borderColor,
                border_color);
        border_radius = a.getDimension(
                R.styleable.BorderedButton_borderRadius,
                border_radius);
        smooth_corner = a.getBoolean(
                R.styleable.BorderedButton_smoothCorners,
                smooth_corner);
        a.recycle();
        rectF = new RectF();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();
        rectF.set(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (smooth_corner) {
            canvas.drawRoundRect(rectF, border_radius, border_radius,paint);
        }
    }
}
