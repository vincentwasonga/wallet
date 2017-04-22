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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Debt;
import com.octopus.wallet.m.v.a.ListAnimator;
import com.octopus.wallet.m.v.w.CircularProgressLabel;


public class DebtsAdapter extends VBinder<Debt> {
    private TextView effected;
    private TextView payback;
    private TextView progress;
    private TextView description;
    private TextView name;
    private ImageButton options_button;
    private ImageView settle_button;
    private Options options;
    private int green, orange;
    private CircularProgressLabel progressLabel;

    public DebtsAdapter(SList<Debt> list,
                        Options options) {
        super(list, R.layout.debt_item);
        this.options = options;
    }

    @Override
    public void onInit(View parent) {
        green = parent.getResources().getColor(R.color.colorGreen);
        orange = parent.getResources().getColor(R.color.colorOrange);
        effected = (TextView) parent.findViewById(R.id.effected_textview);
        payback = (TextView) parent.findViewById(R.id.payback_textview);
        progress = (TextView) parent.findViewById(R.id.progress_textview);
        description = (TextView) parent.findViewById(R.id.description);
        name = (TextView) parent.findViewById(R.id.name_textview);
        options_button = (ImageButton) parent.findViewById(R.id.optionsButton);
        settle_button = (ImageView) parent.findViewById(R.id.settle_icon);
        progressLabel = (CircularProgressLabel) parent.findViewById(R.id.progress_label);
        new ListAnimator(parent).animate();
    }

    @Override
    public void onBind(final Debt debt) {
        effected.setText(Func.getDateddMMM(debt.getEffectedDate()));
        payback.setText(Func.getDateddMMM(debt.getExpectedPayback()));
        progress.setText(getProgress(debt));
        String desc = "more info '" + debt.getDescription() + "'";
        description.setText(desc);
        String text;
        if (debt.isLent()) {
            text = "Gave " + debt.getName() + " " + Func.getMoney(debt.getAmount());
            progressLabel.setBoundary_color(orange);
        } else {
            text = "Borrowed " + debt.getName() + " " + Func.getMoney(debt.getAmount());
            progressLabel.setBoundary_color(green);
        }
        name.setText(text);
        options_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        options.onClick(debt, v);
                    }
                });
        settle_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        options.onSettle(debt);
                    }
                });
        progressLabel.setData(debt.getPayedBack(), debt.getAmount());
    }

    private String getProgress(Debt debt) {
        int payed = debt.getPayedBack();
        return "Settled " + Func.getMoney(payed);
    }

    public interface Options {
        void onClick(Debt debt,
                     View view);

        void onSettle(Debt debt);
    }
}
