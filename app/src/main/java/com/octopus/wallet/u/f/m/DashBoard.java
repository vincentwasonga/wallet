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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.octopus.wallet.R;
import com.octopus.wallet.m.a.DayRecordsAdapter;
import com.octopus.wallet.m.a.RecordClickedListener;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.g.ChartPair;
import com.octopus.wallet.m.h.ArrangeOrder;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.v.a.RecordClickAnimator;
import com.octopus.wallet.m.v.v.AmountTextView;
import com.octopus.wallet.m.v.w.PopupList;
import com.octopus.wallet.u.a.t.TransactionActivity;
import com.octopus.wallet.u.f.BFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.anim.Animator;
import me.yoctopus.cac.util.LogUtil;

public class DashBoard extends BFragment {

    private final int animPeriod =
            AnimDuration.standard().getTime();
    private String TAG = LogUtil.makeTag(DashBoard.class);
    private ViewHolder holder;
    private DayRecordsAdapter adapter;
    private OnFragmentInteractionListener mListener;

    private AccountManager manager;


    public DashBoard() {
    }

    public static DashBoard newInstance() {
        return new DashBoard();
    }

    @Override
    public String getName() {
        return "Dashboard";
    }

    @Override
    public int onGetLayout() {
        return R.layout.fragment_dash_board;
    }
    @OnClick(R.id.sort_imageview)
    public void onViewClicked() {
        PopupList popupList = new PopupList(getContext(), getView(R.id.sort_imageview));
        popupList.setOnItemClickListener(
                new PopupList.OnItemClickListener() {
                    @Override
                    public boolean onItemClick(int itemId) {
                        onPopupItemSelected(itemId);
                        return false;
                    }
                });
        popupList.addItem(ArrangeOrder.TA,
                "Ascending");
        popupList.addItem(ArrangeOrder.TD,
                "Descending");
        popupList.show();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        holder = new ViewHolder(view);
        setSwitcher(holder.switcher);
        manager = new AccountManager(getActivity());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        holder.recordList.setLayoutManager(manager);
    }

    @Override
    public void onResume() {
        super.onResume();
        SList<DayRecords> list = getDayRecords();
        if (list.isEmpty()) {
            return;
        }
        displayBalance();
        drawLineChart();
        listRecords(list);
    }

    private void onPopupItemSelected(int id) {
        SList<DayRecords> records =
                new SList<>(Func.sortDayRecords(getDayRecords(),
                        new ArrangeOrder(id)));
        adapter.updateAll(records);
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


    private void displayBalance() {
        holder.balanceText.setText(
                Func.getMoney(manager
                        .getInfo().getTotalBal()));
    }

    private void drawLineChart() {
        LogUtil.i(TAG,
                "drawLineChart: ");
        int spent = manager.getInfo().getDailySpent();
        spent = Func.convertToCurrentCurrency(spent);
        int limit = manager.getInfo().getDailyLimit();
        limit = Func.convertToCurrentCurrency(limit);
        float x = holder.linechart.getX();
        float y = holder.linechart.getTop();
        ChartPair chartPair = new ChartPair();
        chartPair.addPairValue(0,
                "");
        chartPair.addPairValue(spent,
                "");
        chartPair.addPairValue(0,
                "");
        LineDataSet dataSet = new LineDataSet(
                chartPair.getEntries(),
                "");
        if (limit != 0) {
            String percent;
            if (limit <= spent) {
                int red_color = getResources().getColor(
                        R.color.colorRed);
                percent = "Overspent by " +
                        Func.getMoney(spent - limit);
                holder.percentageText.setTextColor(red_color);
            } else {
                int green_color = getResources().getColor(
                        R.color.colorGreen);
                holder.percentageText.setTextColor(green_color);
                percent = Func.getMoney(limit - spent) +
                        " can be spent today";
            }
            holder.percentageText.setText(percent);
            holder.linechart.getAxisLeft().setAxisMaxValue(limit);
            holder.linechart.getAxisRight().setAxisMaxValue(limit);
        } else {
            holder.percentageText.setText(R.string.no_limit_set);
        }

        dataSet.setDrawCubic(true);
        dataSet.setDrawFilled(true);
        LineData data = new LineData(chartPair.getLabels(),
                dataSet);
        data.setHighlightEnabled(true);
        data.setValueTextSize(12);
        holder.linechart.setData(data);
        holder.linechart.setDescriptionPosition(x,
                y);
        holder.linechart.setDescription("Today's expense Total");
        holder.linechart.setDescriptionTextSize(12);
        holder.linechart.getLegend().setEnabled(false);
        holder.linechart.invalidate();
        holder.linechart.animateY(animPeriod);
    }

    private void listRecords(ArrayList<DayRecords> records) {
        LogUtil.i(TAG,
                "listRecords: ");
        if (!records.isEmpty()) {
            adapter = new DayRecordsAdapter(getActivity(),
                    new SList<>(records),
                    new RecordClickedListener() {
                        @Override
                        public void recordClicked(View v,
                                                  final Record record) {
                            Animator animator = new RecordClickAnimator(v);
                            animator.setAnimatorListener(
                                    new Animator.AnimatorListener() {

                                        @Override
                                        public void onStartAnimator(Animator animator) {

                                        }

                                        @Override
                                        public void onRepeatAnimator(Animator animator) {

                                        }

                                        @Override
                                        public void onStopAnimator(Animator animator) {
                                            showDetailedRecord(record);
                                        }
                                    });
                            animator.animate();
                        }
                    });
            holder.recordList.setAdapter(adapter);
        }
    }

    private void showDetailedRecord(final Record record) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(),
                        TransactionActivity.class);
                intent.putExtra("record",
                        record);
                startActivity(intent);
            }
        };
        getHandler().postDelayed(runnable,
                500);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }



    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int uri);
    }

    static class ViewHolder {
        @InjectView(R.id.balanceText)
        AmountTextView balanceText;
        @InjectView(R.id.percentageText)
        TextView percentageText;
        @InjectView(R.id.linechart)
        LineChart linechart;
        @InjectView(R.id.recordList)
        RecyclerView recordList;
        @InjectView(R.id.switcher)
        ViewSwitcher switcher;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
