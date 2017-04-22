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
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.octopus.wallet.R;

import java.util.ArrayList;

public class PopupList {

    private final Context context;
    private final View anchorView;
    private final ArrayList<Item> items =
            new ArrayList<>();
    private PopupWindow popupWindow;
    private ListView listView;
    private OnItemClickListener clickListener;
    private final AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View view,
                                        int position, long id) {
                    if (popupWindow == null) return;
                    popupWindow.dismiss();
                    if (clickListener != null) {
                        clickListener.onItemClick((int) id);
                    }
                }
            };
    private int offsetX;
    private int offsetY;
    private int width;
    private int height;
    private final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (popupWindow == null) return;
                    updatePopupLayoutParams();
                    popupWindow.update(anchorView,
                            offsetX,
                            offsetY,
                            width,
                            height);
                }
            };
    private final PopupWindow.OnDismissListener onDismissListener =
            new PopupWindow.OnDismissListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onDismiss() {
                    if (popupWindow == null) return;
                    popupWindow = null;
                    ViewTreeObserver observer =
                            anchorView.getViewTreeObserver();
                    if (observer.isAlive()) {
                        observer.removeGlobalOnLayoutListener(
                                globalLayoutListener);
                    }
                }
            };

    public PopupList(Context context,
                     View anchorView) {
        this.context = context;
        this.anchorView = anchorView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public void addItem(int id, String title) {
        items.add(new Item(id, title));
    }

    public void show() {
        if (popupWindow != null) return;
        anchorView.getViewTreeObserver()
                .addOnGlobalLayoutListener(globalLayoutListener);
        popupWindow = createPopupWindow();
        updatePopupLayoutParams();
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setElevation(5.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupWindow.setOverlapAnchor(false);
        }
        popupWindow.showAsDropDown(anchorView,
                offsetX,
                offsetY);
    }

    private void updatePopupLayoutParams() {
        ListView content = listView;
        PopupWindow popup = popupWindow;

        Rect p = new Rect();
        popup.getBackground().getPadding(p);

        int maxHeight =
                popupWindow.getMaxAvailableHeight(anchorView) -
                        p.top -
                        p.bottom;
        listView.measure(
                View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(maxHeight,
                        View.MeasureSpec.AT_MOST));
        width = content.getMeasuredWidth() +
                p.top +
                p.bottom;
        height = Math.min(maxHeight,
                content.getMeasuredHeight() +
                        p.left +
                        p.right);
        offsetX = -p.left;
        offsetY = -p.top;
    }

    private PopupWindow createPopupWindow() {
        PopupWindow popup = new PopupWindow(context);
        popup.setOnDismissListener(onDismissListener);

        popup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        listView = new ListView(context,
                null,
                android.R.attr.dropDownListViewStyle);
        listView.setAdapter(new ItemAdapter());
        listView.setOnItemClickListener(onItemClickListener);
        popup.setContentView(listView);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);

        return popup;
    }


    public interface OnItemClickListener {
        boolean onItemClick(int itemId);
    }

    public static class Item {
        public final int id;
        public String title;

        public Item(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    private class ItemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).id;
        }


        @Override
        public View getView(int position,
                            View view,
                            ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(context)
                        .inflate(R.layout.popup_list_item,
                                null);
            }
            TextView text = (TextView)
                    view.findViewById(R.id.option_name);
            text.setText(items.get(position)
                    .title);
            return view;
        }
    }
}
