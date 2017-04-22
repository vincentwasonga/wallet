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
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.octopus.wallet.R;
import com.octopus.wallet.m.a.AdapterDivider;
import com.octopus.wallet.m.a.LedgerAdapter;
import com.octopus.wallet.m.a.OnDayRecordsClicked;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.g.ChartPair;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.u.a.m.DailyRecordsActivity;
import com.octopus.wallet.u.f.BFragment;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.util.LogUtil;


public class OverView extends BFragment {
    private String TAG = LogUtil.makeTag(OverView.class);
    private PieChart chart;
    private RecyclerView ledgerList;
    private TextView currentBal;
    private final int animPeriod =
            AnimDuration.standard().getTime();
    private OnFragmentInteractionListener mListener;
    private OnDayRecordsClicked onDayRecordsClicked =
            new OnDayRecordsClicked() {
        @Override
        public void onClicked(DayRecords dayRecords) {
            onRecordsClicked(dayRecords);
        }
    };
    private AccountManager manager;

    public OverView() {
        // Required empty public constructor
    }

    public static OverView newInstance() {
        return new OverView();
    }

    @Override
    public String getName() {
        return "Overview";
    }

    @Override
    public int onGetLayout() {
        return R.layout.fragment_over_view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new AccountManager(getContext());

        chart = (PieChart) view
                .findViewById(R.id.chart);
        ledgerList = (RecyclerView) view
                .findViewById(R.id.ledger_group_list);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity());
        ledgerList.setLayoutManager(layoutManager);
        currentBal = (TextView) view.
                findViewById(R.id.textViewCurrentBal);
    }

    @Override
    public void onResume() {
        super.onResume();
        SList<DayRecords> list = getDayRecords();
        if (list.isEmpty()) {
            return;
        }
        drawChart();
        listLedger(list);
        displayCurrentBalance();
    }
    private int getRecordTotal(List<Record> records, int type) {
        int sum = 0;
        switch (type) {
            case 0 :{
                for (Record record : records) {
                    if (record.isIncome()) {
                        sum += record.getAmount();
                    }
                }
            }
            case 1 : {
                for (Record record : records) {
                    if (record.isExpense()) {
                        sum += record.getAmount();
                    }
                }
            }
        }
        return sum;
    }

    private void drawChart() {
        SList<Record> list = getDatabase().getRecords();
        int incomes = getRecordTotal(list, 0);
        int expenses = getRecordTotal(list, 1);
        int chart1 = getResources().getColor(
                R.color.colorGreen);
        int chart2 = getResources().getColor(
                R.color.colorOrange);
        int[] colors = new int[]{chart1,
                chart2};
        ChartPair chartPair = new ChartPair();
        chartPair.addPairValue(incomes,
                "Incomes");
        chartPair.addPairValue(expenses,
                "Expenses");
        PieDataSet dataSet = new PieDataSet(
                chartPair.getEntries(),
                "");
        dataSet.setColors(ColorTemplate.createColors(colors));
        PieData data = new PieData(
                chartPair.getLabels(),
                dataSet); //
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(12);
        chart.setData(data);
        chart.setCenterText(
                getPercentAnalysis(incomes,
                        expenses));
        chart.setCenterTextColor(getResources().getColor(
                R.color.colorBlue));
        chart.setRotationEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setDescription(null);
        chart.setUsePercentValues(true);
        chart.invalidate();
        chart.animateY(animPeriod);
    }

    private String getPercentAnalysis(int income,
                                      int expense) {
        LogUtil.d(TAG,
                "getPercentAnalysis: getting analysis");
        LogUtil.i(TAG,
                "getPercentAnalysis: incomes " +
                        income);
        LogUtil.i(TAG,
                "getPercentAnalysis: expenses " +
                        expense);
        String analysis;
        int total = income + expense;
        LogUtil.i(TAG,
                "getPercentAnalysis: total " +
                        total);
        if (total != 0) {
            int diff = income - expense;
            String diffs = Func.getMoney(diff);
            LogUtil.i(TAG,
                    "getPercentAnalysis: difference " +
                            diff);
            if (income >= expense) {
                analysis = "" +
                        diffs +
                        " \n gain";
            } else {
                analysis = "( " +
                        diffs +
                        ") \n loss";
            }
        } else {
            analysis = 0 + " %";
        }
        return analysis;
    }

    private void listLedger(ArrayList<DayRecords> dayRecords) {
        LedgerAdapter ledgerAdapter =
                new LedgerAdapter(new SList<>(dayRecords),
                onDayRecordsClicked);
        ledgerList.addItemDecoration(new AdapterDivider(getContext(),
                LinearLayout.VERTICAL));
        ledgerList.setAdapter(ledgerAdapter);
    }
    private void onRecordsClicked(DayRecords dayRecords) {
        Intent intent = getIntent(DailyRecordsActivity.class);
        intent.putExtra("records",
                dayRecords);
        startActivity(intent);
    }

    private void displayCurrentBalance() {
        int bal;
        bal = manager.getInfo().getTotalBal();
        currentBal.setText(Func.getMoney(bal));
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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int uri);
    }
}
