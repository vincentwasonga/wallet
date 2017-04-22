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

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.g.BarPair;
import com.octopus.wallet.m.g.ChartPair;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.v.a.ListAnimator;

import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.anim.Animator;

public class DailyAnalysisAdapter extends VBinder<DayRecords> {
    private int green;
    private int orange;
    private TextView dateView;
    private TextView incomeTotalTxt;
    private TextView expenseTotalTxt;
    private PieChart analysis;
    private HorizontalBarChart chart;
    private OnDayRecordsClicked OnDayRecordsClicked;


    public DailyAnalysisAdapter(SList<DayRecords>
                                        dayRecords,
                                OnDayRecordsClicked dayRecordsClicked) {
        super(dayRecords,
                R.layout.daily_analysis_item);
        this.OnDayRecordsClicked = dayRecordsClicked;
    }
    @Override
    public void onInit(View parent) {
        green = parent
                .getResources().getColor(
                        R.color.colorGreen);
        orange = parent
                .getResources().getColor(
                        R.color.colorOrange);
        dateView = (TextView) parent
                .findViewById(
                        R.id.dateView);
        incomeTotalTxt = (TextView) parent
                .findViewById(
                        R.id.incomeView);
        expenseTotalTxt = (TextView) parent
                .findViewById
                        (R.id.expenseView);
        analysis = (PieChart) parent
                .findViewById(
                        R.id.analysisChart);
        chart = (HorizontalBarChart) parent
                .findViewById(
                        R.id.horizontalChart);
        Animator animator = new ListAnimator(
                parent);
        animator.animate();
    }

    @Override
    public void onBind(final DayRecords model) {
        int incomeTotal =
                model.getIncomeTotal();
        int expenseTotal =
                model.getExpenseTotal();
        int incomes =
                model.getIncomes().size();
        int expenses =
                model.getExpenses().size();
        String incomeData =
                (incomes < 1 ?
                        "" :
                        (incomes == 1) ?
                                1 +
                                        " income, " +
                                        Func.getMoney(
                                                incomeTotal) :
                                incomes +
                                        " incomes, " +
                                        Func.getMoney(
                                                incomeTotal));
        String expenseData = (expenses < 1 ?
                "" :
                (expenses == 1) ?
                        1 +
                                " expense, " +
                                Func.getMoney(
                                        expenseTotal) :
                        expenses +
                                " expenses, " +
                                Func.getMoney(
                                        expenseTotal));
        dateView.setText(
                model.getRecordsDateName());
        displayTextViewData(incomeData,
                incomeTotalTxt);
        displayTextViewData(expenseData,
                expenseTotalTxt);
        drawChart(incomes,
                expenses,
                analysis,
                orange,
                green);
        drawHorizontal(chart, model);
        getParent().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (OnDayRecordsClicked == null) {
                            return;
                        }
                        OnDayRecordsClicked.onClicked(model);
                    }
                });
    }

    private void displayTextViewData(String data,
                                     TextView view) {
        view.setText(data);
    }
    private void drawHorizontal(HorizontalBarChart chart, DayRecords dayRecord) {

        float x = chart.getX();
        float y = chart.getTop();
        BarPair barPair = new BarPair();
        barPair.addPairValue(dayRecord.getIncomeTotal(), "");
        barPair.addPairValue(dayRecord.getExpenseTotal(), "");
        int[] colors = new int[]{green, orange};
        BarDataSet dataSet = new BarDataSet(barPair.getEntries(),"");
        dataSet.setDrawValues(false);
        dataSet.setColors(ColorTemplate.createColors(colors));
        BarData data = new BarData(barPair.getDays(), dataSet);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDescriptionPosition(x, y);
        chart.setDescription("Recent daily expenditures");
        chart.invalidate();
        chart.animateY(AnimDuration.big().getTime());
    }

    private void drawChart(int incomes,
                           int expenses,
                           PieChart pieChart,
                           int orange,
                           int green) {
        int[] colors = new int[]{green, orange};
        ChartPair chartPair = new ChartPair();
        chartPair.addPairValue(incomes,"");
        chartPair.addPairValue(expenses,"");
        PieDataSet dataSet = new PieDataSet(chartPair.getEntries(),"");
        dataSet.setColors(
                ColorTemplate.createColors(colors));
        PieData data = new PieData(
                chartPair.getLabels(),
                dataSet);
        data.setValueTextColor(Color.TRANSPARENT);
        data.setValueTextSize(15);
        pieChart.setData(data);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDescription(null);
        pieChart.setUsePercentValues(true);
        pieChart.invalidate();
        pieChart.animateY(AnimDuration.big().getTime());
    }
}
