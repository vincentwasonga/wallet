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
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.h.RecordPair;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Record;

import me.yoctopus.cac.util.LogUtil;

public class LedgerRecordAdapter extends VBinder<RecordPair> {
    private TextView sNo;
    private TextView incomeName;
    private TextView incomeAmount;
    private TextView expenseName;
    private TextView expenseAmount;
    private int id;
    private String TAG = LogUtil.makeTag(LedgerRecordAdapter.class);
    public LedgerRecordAdapter(SList<Record> records,
                               int id) {
        super(new SList<>(Func.getRecordPairs(records)),
                R.layout.ledger_record);
        this.id = id;
    }


    public int getId() {
        return id;
    }

    private void display2Records(Record income,
                                 Record expense) {
        if (income != null) {
            incomeName.setText(income.getName());
            incomeAmount.setText(
                    Func.getMoney(income.getAmount()));
        }
        if (expense != null) {
            expenseName.setText(expense.getName());
            expenseAmount.setText(
                    Func.getMoney(expense.getAmount()));
        }
    }

    @Override
    public void onInit(View itemView) {
        sNo = (TextView) itemView.
                findViewById(R.id.sNo);
        incomeName = (TextView) itemView
                .findViewById(R.id.incomeName);
        incomeAmount = (TextView) itemView
                .findViewById(R.id.incomeAmount);
        expenseName = (TextView) itemView
                .findViewById(R.id.expenseName);
        expenseAmount = (TextView) itemView
                .findViewById(R.id.expenseAmount);
    }

    @Override
    public void onBind(RecordPair pair) {
        sNo.setText(id+ "");
        display2Records(pair.getIncome(),
                pair.getExpense());
        id++;
    }
}
