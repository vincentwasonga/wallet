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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.h.ArrangeOrder;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.RecordsArranger;
import com.octopus.wallet.m.pb.Record;

import java.util.ArrayList;

import me.yoctopus.cac.util.LogUtil;

public class DayRecordsAdapter extends
        VBinder<DayRecords> {
    private TextView dateTextView;
    private RecyclerView recordsList;

    private RecordClickedListener clickedListener;
    private Activity context;
    private String TAG = LogUtil.makeTag(DayRecordsAdapter.class);

    public DayRecordsAdapter(Activity context,
                             SList<DayRecords> dayRecords,
                             RecordClickedListener listener) {
        super(dayRecords, R.layout.record_row);

        RecordsArranger arranger =
                new RecordsArranger(
                        dayRecords,
                        0);
        arranger.sort2(
                new ArrangeOrder(
                        ArrangeOrder.TA
                ).getOrder());
        setList((SList<DayRecords>) arranger.getDayRecords());
        this.context = context;
        this.clickedListener = listener;
        LogUtil.i(TAG,
                "DayRecordsAdapter: ");
    }

    @Override
    public void onInit(View parent) {
        dateTextView = (TextView) parent.
                findViewById(R.id.dateTextView);
        recordsList = (RecyclerView) parent
                .findViewById(R.id.recordsList);
        recordsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager;
        linearLayoutManager =
                new LinearLayoutManager(context);
        recordsList.setLayoutManager(
                linearLayoutManager);
        recordsList.addItemDecoration(
                new AdapterDivider(context,
                        LinearLayoutManager.VERTICAL));
        recordsList.setItemAnimator(
                new DefaultItemAnimator());
    }

    @Override
    public void onBind(DayRecords model) {
        getParent().setFocusable(false);
        ArrayList<Record> records = model.getRecords();
        RecordsAdapter recyclerAdapter = new RecordsAdapter(
                new SList<>(records),
                clickedListener);
        dateTextView.setText(model.getRecordsDateName());
        recordsList.setAdapter(
                recyclerAdapter);
    }

}
