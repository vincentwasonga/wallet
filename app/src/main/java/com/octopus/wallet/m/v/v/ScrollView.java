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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;


public class ScrollView extends android.widget.ScrollView {

    public ScrollView(Context context,
                      AttributeSet attrs) {
        super(context,
                attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        for (int i = 0;
             i < ((ViewGroup) getChildAt(0)).getChildCount();
             i++) {
            try {
                CustomView child =
                        (CustomView) ((ViewGroup) getChildAt(0)).getChildAt(i);
                if (child.isLastTouch) {
                    child.onTouchEvent(ev);
                    return true;
                }
            } catch (ClassCastException e) {
            }
        }

        return super.onTouchEvent(ev);
    }

}
