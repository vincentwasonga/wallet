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

package com.octopus.wallet.m.v.v;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import me.grantland.widget.AutofitTextView;

public class AmountTextView extends AutofitTextView {
    private Context context;

    public AmountTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AmountTextView(Context context,
                          AttributeSet attrs) {
        super(context,
                attrs);
        this.context = context;
        init();
    }

    public AmountTextView(Context context,
                          AttributeSet attrs,
                          int defStyle) {
        super(context,
                attrs,
                defStyle);
        this.context = context;
        init();
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "font/Roboto-Thin.ttf");
        setTypeface(typeface);
    }
}
