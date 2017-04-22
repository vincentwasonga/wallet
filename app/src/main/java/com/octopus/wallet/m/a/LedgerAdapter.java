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

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.v.a.ListAnimator;

import me.yoctopus.cac.anim.Animator;

/**
 * Created by octopus on 9/28/16.
 */
public class LedgerAdapter extends VBinder<DayRecords> {
    private TextView dateView;
    private RecyclerView ledgerRow;
    View view;
    private int id = 1;
    private OnDayRecordsClicked onDayRecordsClicked;

    public LedgerAdapter(SList<DayRecords> records,
                         OnDayRecordsClicked onDayRecordsClicked) {
        super(records.reverse(), R.layout.ledger_group);
        this.onDayRecordsClicked =
                onDayRecordsClicked;
    }

    @Override
    public void onInit(View itemView) {
        view = itemView;
        dateView = (TextView) itemView
                .findViewById(R.id.dateView);
        ledgerRow = (RecyclerView) itemView
                .findViewById(R.id.ledger_record_list);
        LinearLayoutManager linearLayoutManager;
        linearLayoutManager =
                new LinearLayoutManager(itemView.getContext());
        ledgerRow.setLayoutManager(linearLayoutManager);
        ledgerRow.setItemAnimator(new DefaultItemAnimator());

        Animator animator = new ListAnimator(itemView);
        animator.animate();
    }

    @Override
    public void onBind(final DayRecords records) {
        LedgerRecordAdapter ledgerRecordAdapter =
                new LedgerRecordAdapter(new SList<>(records.getRecords()),
                        id);
        dateView.setText(records.getRecordsDateName());
        ledgerRow.setAdapter(ledgerRecordAdapter);
        id += ledgerRecordAdapter.getItemCount();
        getParent().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDayRecordsClicked.onClicked(
                                records);
                    }
                });
    }
}
