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

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.v.a.ListAnimator;

import me.yoctopus.cac.anim.Animator;
import me.yoctopus.cac.util.LogUtil;

public class RecordsAdapter extends VBinder<Record> {
    private RecordClickedListener clickedListener;
    private ImageView imageView;
    public TextView item;
    public TextView amount;
    public TextView date;
    public TextView location;
    public TextView description;
    private RelativeLayout descriptionLayout;
    public View view;
    private int green;
    private int orange;
    private int light_blue;
    private int position = 0;
    private String TAG = LogUtil.makeTag(RecordsAdapter.class);

    public RecordsAdapter(SList<Record> records,
                          RecordClickedListener listener) {
        super(records, R.layout.record_list);
        LogUtil.i(TAG,
                "RecordsAdapter: ");
        this.clickedListener = listener;
    }


    @Override
    public void onInit(View parent) {
        descriptionLayout = (RelativeLayout) parent
                .findViewById(R.id.descriptionLayout);
        item = (TextView) parent
                .findViewById(R.id.category);
        amount = (TextView) parent
                .findViewById(R.id.amount);
        date = (TextView) parent
                .findViewById(R.id.date);
        imageView = (ImageView) parent
                .findViewById(R.id.list_image);
        location = (TextView) parent.
                findViewById(R.id.locationTextView);
        description = (TextView) parent
                .findViewById(R.id.descriptionTextView);
        setGreen(parent.getResources()
                .getColor(R.color.colorGreen));
        setOrange(parent.getResources()
                .getColor(R.color.colorOrange));
        light_blue = parent.getResources()
                .getColor(R.color.colorBlue_Light);
        view = parent;
        Animator animator = new ListAnimator(parent);
        animator.animate();
    }

    @Override
    public void onBind(final Record record) {
        item.setText(record.getName());
        if (record.isIncome()) {
            amount.setTextColor(getGreen());
        } else  {
            amount.setTextColor(getOrange());
        }
        if (!record.getName().isEmpty()) {
            String text = String.valueOf(record.getName().charAt(0));
            imageView.setImageDrawable(TextDrawable.builder()
                    .beginConfig()
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRound(text,
                            ColorGenerator.MATERIAL.getRandomColor()));
        }
        amount.setText(Func.getMoney(record.getAmount()));
        date.setText(record.getDateString());
        if (!record.getDescriptionSet().getDescription().equals(
                Record.DescriptionSet.DEFAULT)) {
            descriptionLayout.setVisibility(View.VISIBLE);
            description.setText(
                    record.getDescriptionSet().getDescription());
        }
        if (!record.getDescriptionSet().getLocationName().equals(
                Record.DescriptionSet.DEFAULT)) {
            descriptionLayout.setVisibility(View.VISIBLE);
            location.setText(
                    record.getDescriptionSet().getLocationName());
        }
        if (position % 2 == 1) {
            view.setBackgroundColor(light_blue);
        }
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickedListener != null) {
                            clickedListener.recordClicked(view,
                                    record);
                        }
                    }
                });
        position++;
    }
    private int getGreen() {
        return green;
    }

    private void setGreen(int green) {
        this.green = green;
    }

    private int getOrange() {
        return orange;
    }

    private void setOrange(int orange) {
        this.orange = orange;
    }

}
