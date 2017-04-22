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

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Record;

import me.yoctopus.cac.util.LogUtil;


public class SavingsRecordAdapter extends VBinder<Record> {
    private String TAG = LogUtil.makeTag(SavingsRecordAdapter.class);

    private Activity activity;
    private int green;
    private int orange;
    private TextView recordName;
    private TextView recordCost;

    public SavingsRecordAdapter(SList<Record> records,
                                Activity activity) {
        super(records, R.layout.expense_saving_item);

        this.activity = activity;
        green = activity.getResources().getColor(R.color.colorGreen);
        orange = activity.getResources().getColor(R.color.colorOrange);
    }

    @Override
    public void onInit(View parent) {
        recordName = (TextView) parent.findViewById(R.id.record_name);
        recordCost = (TextView) parent.findViewById(R.id.record_cost);
    }

    @Override
    public void onBind(Record record) {
        if (record.isExpense()) {
            recordName.setTextColor(orange);
            recordCost.setTextColor(orange);
        }
        else if (record.isIncome()) {
            recordName.setTextColor(green);
            recordCost.setTextColor(green);
        }
        recordName.setText(record.getName());
        recordCost.setText(Func.getMoney(record.getAmount()));
    }
}
