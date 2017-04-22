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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.g.ChartPair;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.v.a.ListAnimator;

import java.util.ArrayList;

import me.yoctopus.cac.util.LogUtil;

/**
 * Created by yoctopus on 2/1/17.
 */

public class SavingsAdapter extends VBinder<DayRecords> {
    private RecordClickedListener clickedListener;
    private Activity context;
    private String TAG = LogUtil.makeTag(SavingsAdapter.class);
    private int dailyTarget;
    private TextView dateTextView;
    private LineChart lineChart;
    private TextView targetTextView;
    private TextView savedAmount;
    private RecyclerView saving_recordsList;

    public SavingsAdapter(Activity context,
                             SList<DayRecords> dayRecords,
                             int dailyTarget,
                             RecordClickedListener listener) {
        super((SList<DayRecords>) Func.sortDayRecordsTA(dayRecords),
                R.layout.fragment_savings_item);
        this.dailyTarget = dailyTarget;
        this.context = context;
        this.clickedListener = listener;
        LogUtil.i(TAG,
                "DayRecordsAdapter: ");
    }


    private void drawChart(LineChart lineChart,
                           int saved) {
        LogUtil.i(TAG,
                "drawLineChart: ");
        ChartPair chartPair;
        int limit = dailyTarget;
        limit = Func.convertToCurrentCurrency(limit);
        float x = lineChart.getX();
        float y = lineChart.getTop();
        chartPair = new ChartPair();
        chartPair.addPairValue(0,
                "");
        chartPair.addPairValue( Func.convertToCurrentCurrency(saved),
                "");
        chartPair.addPairValue(0,
                "");
        LineDataSet dataSet = new LineDataSet(
                chartPair.getEntries(),
                "");
        lineChart.getAxisLeft().setAxisMaxValue(limit);
        lineChart.getAxisRight().setAxisMaxValue(limit);
        lineChart.getAxisRight().setEnabled(false);
        dataSet.setDrawCubic(true);
        dataSet.setDrawFilled(true);
        LineData data = new LineData(chartPair.getLabels(),
                dataSet);
        data.setHighlightEnabled(true);
        data.setValueTextSize(12);
        lineChart.setData(data);
        lineChart.setDescriptionPosition(x,
                y);
        lineChart.setDescription("Today's expense Total");
        lineChart.setDescriptionTextSize(12);
        lineChart.getLegend().setEnabled(false);
        lineChart.invalidate();
        lineChart.animateY(3000);

    }
    private int getSavedAmount(DayRecords dayRecords) {
        int incomes = dayRecords.getIncomeTotal();
        int expenses = dayRecords.getExpenseTotal();
        int used = incomes - expenses;
        if (used < 0) {
            return dailyTarget - Math.abs(used);
        }
        if (used > 0) {
            return dailyTarget + used;
        }
        return dailyTarget;
    }

    @Override
    public void onInit(View parent) {
        dateTextView = (TextView) parent.
                findViewById(R.id.date_textView);
        lineChart = (LineChart)
                parent.findViewById(R.id.saving_chart);
        targetTextView = (TextView)
                parent.findViewById(R.id.target_amount_textView);
        saving_recordsList = (RecyclerView) parent
                .findViewById(R.id.saving_record_list);
        savedAmount = (TextView)
                parent.findViewById(R.id.saved_amount_textView);
        LinearLayoutManager linearLayoutManager;
        linearLayoutManager =
                new LinearLayoutManager(context);
        saving_recordsList.setLayoutManager(
                linearLayoutManager);
        saving_recordsList.setItemAnimator(
                new DefaultItemAnimator());
        new ListAnimator(parent)
                .animate();
    }

    @Override
    public void onBind(DayRecords dayRecords) {
        ArrayList<Record> records = dayRecords.getRecords();
        SavingsRecordAdapter savingsRecordAdapter =
                new SavingsRecordAdapter(new SList<>(records),
                        context);
        dateTextView.setText(dayRecords.getRecordsDateName());
        saving_recordsList.setAdapter(
                savingsRecordAdapter);
        targetTextView.setText(Func.getMoney(dailyTarget));
        int saved = getSavedAmount(dayRecords);
        if (saved < 0) {
            String s = "("+
                    Func.getMoney(Math.abs(saved))+
                    ")";
            savedAmount.setText(s);
        }
        else {
            String s =
                    Func.getMoney(Math.abs(saved));
            savedAmount.setText(s);
        }
        drawChart(lineChart, saved);
    }
}
