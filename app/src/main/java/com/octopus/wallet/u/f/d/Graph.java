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

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.octopus.wallet.R;
import com.octopus.wallet.m.g.BarPair;

import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.util.LogUtil;

public class Graph extends Fragment {
    private String TAG = LogUtil.makeTag(Graph.class);
    private final int animPeriod =
            AnimDuration.standard().getTime();
    private HorizontalBarChart horizontalBarChart;

    private OnFragmentInteractionListener mListener;
    private int incomes, expenses;

    public Graph() {

    }
    static final String INCOMES = "incomes";
    static final String EXPENSES = "expenses";

    public static Graph newInstance(int incomes,
                                    int expenses) {
        Graph fragment = new Graph();
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
        return inflater.inflate(R.layout.fragment_graph,
                container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,
                savedInstanceState);
        horizontalBarChart = (HorizontalBarChart) view
                .findViewById(R.id.comparegraph);
        drawGraph();
    }

    private void drawGraph() {
        LogUtil.i(TAG,
                "drawComparisons: ");
        float x = horizontalBarChart.getX();
        float y = horizontalBarChart.getTop();
        BarPair barPair = new BarPair();
        LogUtil.i(TAG,
                "drawGraph: drawing");

        barPair.addPairValue(incomes,
                INCOMES);
        barPair.addPairValue(expenses,
                EXPENSES);
        BarDataSet dataSet = new BarDataSet(barPair.getEntries(),
                "daily comparisons");
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
        int chart1 = getResources().getColor(
                R.color.colorGreen);
        int chart2 = getResources().getColor(
                R.color.colorOrange);
        int[] colors = new int[]{chart1,
                chart2};
        dataSet.setColors(ColorTemplate.createColors(colors));
        BarData data = new BarData(barPair.getDays(),
                dataSet);
        data.setValueTextColor(Color.WHITE);

        horizontalBarChart.setData(data);

        horizontalBarChart.setDescriptionPosition(x,
                y);

        horizontalBarChart.setDescription("Income/Expense Comparison");

        horizontalBarChart.invalidate();
        horizontalBarChart.animateY(animPeriod);
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
