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

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.v.a.ListAnimator;
import com.octopus.wallet.u.a.t.CategoryActivity;

/**
 * Created by octopus on 8/30/16.
 */
public class CategoriesAdapter extends VBinder<Record.Category> {
    public TextView item;
    private ImageView imageView;
    private FloatingActionButton editBtn;

    private boolean see = true;
    private CategoryActivity.OnCategoryClickedListener listener;


    public CategoriesAdapter(SList<Record.Category> categories,
                             CategoryActivity.OnCategoryClickedListener listener) {
        super(categories,
                R.layout.item_row);

        this.listener = listener;
    }

    @Override
    public void onInit(View parent) {
        item = (TextView)
                parent.findViewById(R.id.itemText);
        imageView = (ImageView)
                parent.findViewById(R.id.itemIcon);
        editBtn = (FloatingActionButton)
                parent.findViewById(R.id.editItemBtn);
        new ListAnimator(parent)
                .animate();
    }

    @Override
    public void onBind(final Record.Category model) {
        item.setText(model.getName());
        String text = (String.valueOf(model.getName().charAt(0)));
        imageView.setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(text,
                        ColorGenerator.MATERIAL.getRandomColor()));
        if (listener != null) {
            imageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClicked(model);
                        }
                    });
            item.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClicked(model);
                        }
                    });
            editBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onUpdate(model);
                        }
                    });

        }
        if (see) {
            editBtn.setVisibility(
                    View.VISIBLE);
        }
    }

    public void setSeeEditBtns(boolean see) {
        this.see = see;
    }


}
