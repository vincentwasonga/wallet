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

package com.octopus.wallet.u.f.m;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.octopus.wallet.R;
import com.octopus.wallet.m.a.AdapterDivider;
import com.octopus.wallet.m.a.DailyAnalysisAdapter;
import com.octopus.wallet.m.a.OnDayRecordsClicked;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.g.BarPair;
import com.octopus.wallet.m.g.ScatterPair;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.DayRecordsPairValue;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.u.a.m.DailyRecordsActivity;
import com.octopus.wallet.u.f.BFragment;

import java.util.ArrayList;

import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.util.LogUtil;


public class Analysis extends BFragment {
    private final int animPeriod =
            AnimDuration.standard().getTime();
    private String TAG =
            LogUtil.makeTag(Analysis.class);
    private BarChart barChart;
    private ScatterChart scatterChart;
    private HorizontalBarChart horizontalBarChart;
    private RecyclerView dailyList;
    private OnFragmentInteractionListener mListener;
    private OnDayRecordsClicked recordsClicked =
            new OnDayRecordsClicked() {
        @Override
        public void onClicked(DayRecords dayRecords) {
            onDayRecordClicked(dayRecords);
        }
    };

    public Analysis() {

    }

    public static Analysis newInstance() {
        return new Analysis();
    }

    @Override
    public String getName() {
        return "Analysis";
    }

    @Override
    public int onGetLayout() {
        return R.layout.fragment_analysis;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barChart = (BarChart) view
                .findViewById(R.id.graph);
        scatterChart = (ScatterChart) view.
                findViewById(R.id.scattergraph);
        dailyList = (RecyclerView) view
                .findViewById(R.id.daily_analysis_list);
        horizontalBarChart = (HorizontalBarChart) view
                .findViewById(R.id.comparegraph);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity());
        dailyList.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        SList<DayRecords> list = getDayRecords();
        if (list.isEmpty()) {
            return;
        }
        drawGraph(list);
        displayScatterChart(list);
        displayDailyRecords(list);
        drawComparisons(list);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void drawGraph(ArrayList<DayRecords> dayRecords) {
        LogUtil.i(TAG,
                "drawGraph: ");
        float x = barChart.getX();
        float y = barChart.getTop();
        BarPair barPair = new BarPair();
        int sum = 0;

        for (DayRecords dayRecord : dayRecords) {
            if (dayRecord.hasRecords()) {
                DayRecordsPairValue dayRecordsPairValue =
                        dayRecord.getDayRecordsPairValue();
                String day = dayRecordsPairValue.getDateName();
                int total = dayRecordsPairValue.getBalance();
                sum += total;
                barPair.addPairValue(Func.convertToCurrentCurrency(sum),
                        day);
                LogUtil.i(TAG,
                        "drawGraph: sum " +
                                sum +
                                " day " +
                                day);
            }
            LogUtil.i(TAG,
                    "drawGraph: no records");
        }
        BarDataSet dataSet = new BarDataSet(barPair.getEntries(),
                "Balance trend");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(barPair.getDays(),
                dataSet);
        data.setValueTextColor(Color.WHITE);
        barChart.setData(data);
        barChart.setDescriptionPosition(x,
                y);
        barChart.setDescription("Balance trend");
        barChart.setDescriptionColor(
                getResources().getColor(R.color.colorPrimaryDark));
        barChart.setDescriptionTextSize(12);
        barChart.getAxisLeft().setEnabled(false);
        barChart.invalidate();
        barChart.setDrawValueAboveBar(false);
        barChart.animateY(animPeriod);
    }

    private void displayScatterChart(ArrayList<DayRecords> dayRecords) {
        ScatterPair pairValues = new ScatterPair();
        for (DayRecords records : dayRecords) {
            DayRecordsPairValue dayRecordsPairValue =
                    records.getDayRecordsPairValue();
            String day = dayRecordsPairValue.getDateName();
            pairValues.addPairValue(records.getRecords().size(),
                    day);
        }
        ScatterDataSet dataSet = new ScatterDataSet(pairValues.getEntries(),
                "Activity trend");
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSet.setScatterShapeSize(30);
        ScatterData data = new ScatterData(pairValues.getDays(),
                dataSet);
        scatterChart.setData(data);
        scatterChart.getAxisLeft().setEnabled(false);
        scatterChart.setDescription(null);
        scatterChart.animateY(animPeriod);
    }

    private void displayDailyRecords(ArrayList<DayRecords> dayRecords) {
        DailyAnalysisAdapter analysisAdapter =
                new DailyAnalysisAdapter(new SList<>(dayRecords),
                        recordsClicked);
        dailyList.addItemDecoration(new AdapterDivider(getContext(),
                LinearLayout.VERTICAL));
        dailyList.setAdapter(analysisAdapter);
    }
    private void onDayRecordClicked(DayRecords dayRecords) {
        Intent intent = getIntent(DailyRecordsActivity.class);
        intent.putExtra("records", dayRecords);
        startActivity(intent);
    }

    private void drawComparisons(ArrayList<DayRecords> dayRecords) {
        LogUtil.i(TAG,
                "drawComparisons: ");
        float x = horizontalBarChart.getX();
        float y = horizontalBarChart.getTop();
        BarPair barPair = new BarPair();
        for (DayRecords dayRecord : Func.reverse(dayRecords)) {
            if (dayRecord.hasRecords()) {
                DayRecordsPairValue dayRecordsPairValue =
                        dayRecord.getDayRecordsPairValue();
                String day = dayRecordsPairValue.getDateName();
                int total = dayRecordsPairValue.getBalance();
                barPair.addPairValue(Func.convertToCurrentCurrency(total),
                        day);
                LogUtil.i(TAG,
                        "drawGraph: sum " +
                                total +
                                " day " +
                                day);
            }
            LogUtil.i(TAG,
                    "drawGraph: no records");
        }
        BarDataSet dataSet = new BarDataSet(barPair.getEntries(),
                "Accumulation trend");
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v,
                                            Entry entry,
                                            int i,
                                            ViewPortHandler viewPortHandler) {
                String s = entry.toString();
                return s.toLowerCase();
            }
        });
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(barPair.getDays(),
                dataSet);
        data.setValueTextColor(Color.WHITE);
        horizontalBarChart.setData(data);
        horizontalBarChart.setDescriptionPosition(x, y);
        horizontalBarChart.getAxisRight().setEnabled(false);
        horizontalBarChart.setDescription("Recent daily expenditures");
        horizontalBarChart.invalidate();
        horizontalBarChart.animateY(animPeriod);
    }



    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int uri);
    }
}
