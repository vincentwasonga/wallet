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

package com.octopus.wallet.u.f.d;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.octopus.wallet.R;
import com.octopus.wallet.m.g.ChartPair;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.u.f.m.OverView;

import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.util.LogUtil;

public class Chart extends Fragment {

    private String TAG = LogUtil.makeTag(OverView.class);
    private PieChart chart;
    private final int animPeriod = AnimDuration.standard().getTime();

    private OnFragmentInteractionListener mListener;
    private int incomes, expenses;

    public Chart() {

    }
    static final String INCOMES = "incomes";
    static final String EXPENSES = "expenses";



    public static Chart newInstance(int incomes, int expenses) {
        Chart fragment = new Chart();
        Bundle args = new Bundle();
        args.putInt(INCOMES,
                incomes);
        args.putInt(EXPENSES,
                expenses);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.incomes = args.getInt(INCOMES);
            this.expenses = args.getInt(EXPENSES);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart,
                container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chart = (PieChart) view
                .findViewById(R.id.chart);
        drawChart();
    }

    private void drawChart() {
        LogUtil.i(TAG,
                "drawChart: ");
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


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

        void onFragmentInteraction(Uri uri);
    }
}
