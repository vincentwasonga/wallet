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

package com.octopus.wallet.u.a.m;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.a.AdapterDivider;
import com.octopus.wallet.m.a.RecordClickedListener;
import com.octopus.wallet.m.a.RecordsAdapter;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.t.TransactionActivity;
import com.octopus.wallet.u.f.d.Chart;
import com.octopus.wallet.u.f.d.Graph;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DailyRecordsActivity extends BActivity implements
        Chart.OnFragmentInteractionListener,
        Graph.OnFragmentInteractionListener {
    static final int GRAPH = 1;
    static final int CHART = 2;
    @InjectView(R.id.textViewType)
    TextView textViewType;
    @InjectView(R.id.recordsList)
    RecyclerView recordsList;
    private DayRecords dayRecords;
    private int incomes, expenses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_records);
        ButterKnife.inject(this);
        setHasBackButton(true);
        recordsList.setLayoutManager(new LinearLayoutManager(this));
        recordsList.addItemDecoration(new AdapterDivider(this,
                LinearLayout.VERTICAL));

    }
    @OnClick(R.id.imageViewChart)
    public void onImageViewChartClicked() {
        displayChart();
    }

    @OnClick(R.id.imageViewGraph)
    public void onImageViewGraphClicked() {
        displayGraph();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("records")) {
            dayRecords = intent.getParcelableExtra("records");
            incomes = dayRecords.getIncomeTotal();
            expenses = 0 -
                    dayRecords.getExpenseTotal();
            setTitle(dayRecords.getDay().getDayName());
        } else {
            incomes = 0;
            expenses = 0;
        }
        if (intent.hasExtra("graph_type")) {
            int t = intent.getIntExtra("graph_type",
                    GRAPH);
            showAnalysis(t);
        } else {
            showAnalysis(GRAPH);
        }
        listRecords();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void showAnalysis(int type) {
        switch (type) {
            case GRAPH: {
                displayGraph();
                break;
            }
            case CHART: {
                displayChart();
                break;
            }
        }
    }

    private void displayGraph() {
        textViewType.setText("Graph");
        replaceFragment(Graph.newInstance(incomes,
                expenses));
    }

    private void displayChart() {
        textViewType.setText("Chart");
        replaceFragment(Chart.newInstance(incomes,
                Math.abs(expenses)));
    }

    private void listRecords() {
        RecordsAdapter adapter = new RecordsAdapter(
                new SList<>(dayRecords.getRecords()),
                new RecordClickedListener() {
                    @Override
                    public void recordClicked(View v, Record record) {
                        showDetailedRecord(record);
                    }
                });
        recordsList.setAdapter(adapter);
    }

    private void showDetailedRecord(final Record record) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent(TransactionActivity.class);
                intent.putExtra("record",
                        record);
                startActivity(intent);
            }
        };
        getHandler().postDelayed(runnable,
                500);
    }

    private void replaceFragment(@NonNull final Fragment fragment) {
        Runnable replaceRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft =
                                getMyFragmentTransaction();
                        ft.replace(R.id.fragment,
                                fragment);
                        ft.commit();
                    }
                };
        executeRunnable(replaceRunnable,
                10);
    }

    private FragmentTransaction getMyFragmentTransaction() {
        FragmentTransaction ft =
                getFragmentManager().beginTransaction();
        ft.setTransition(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        return ft;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
