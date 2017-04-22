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

package com.octopus.wallet.m.a;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by octopus on 8/9/16.
 */
public class RecyclerTouchListener implements
        RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private final RecordClickListener clickListener;
    public RecyclerTouchListener(Context context,
                                 final RecyclerView recyclerView,
                                 RecordClickListener clickListene) {
        this.clickListener = clickListene;
        setGestureDetector(new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                /*View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && getClickListener() != null) {
                    getClickListener().onLongClick(child, recyclerView.getChildPosition(child));
                }
                */
            }
        }));
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv,
                                         MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(),
                e.getY());
        if (child != null &&
                getClickListener() != null &&
                getGestureDetector().onTouchEvent(e)) {
            getClickListener().onClick(child,
                    rv.getChildPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv,
                             MotionEvent e) {
        /*View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && getClickListener() != null ) {
            getClickListener().onClick(child, rv.getChildPosition(child));
        }
        */
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    public RecordClickListener getClickListener() {
        return clickListener;
    }

    public interface RecordClickListener {
        void onClick(View view,
                     int position);

        void onLongClick(View view,
                         int position);
    }
}

