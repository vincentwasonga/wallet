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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.octopus.wallet.R;
import com.octopus.wallet.m.a.RecordClickedListener;
import com.octopus.wallet.m.a.SavingsAdapter;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.f.BFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class Savings extends BFragment {
    AccountManager manager;
    ViewHolder holder;
    private OnFragmentInteractionListener mListener;

    public Savings() {
    }

    public static Savings newInstance() {
        return new Savings();
    }

    @Override
    public String getName() {
        return "Savings";
    }

    @Override
    public int onGetLayout() {
        return R.layout.fragment_savings;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new AccountManager(getContext());
        holder = new ViewHolder(view);
        holder.swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                holder.swipeRefresh.setRefreshing(false);
                displaySavings(getDayRecords());
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        SList<DayRecords> list = getDayRecords();
        if (list.isEmpty()) {
            return;
        }
        displaySavings(list);

    }

    private void displaySavings(final ArrayList<DayRecords> dayRecords) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BActivity activity = (BActivity) getActivity();
                SavingsAdapter adapter = new SavingsAdapter(activity,
                        new SList<>(dayRecords),
                        manager.getInfo().getDailyLimit(),
                        new RecordClickedListener() {
                            @Override
                            public void recordClicked(View v, Record record) {

                            }
                        });
                LinearLayoutManager manager = new LinearLayoutManager(activity);
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                holder.list.setLayoutManager(manager);
                holder.list.setAdapter(adapter);
            }
        };
        getHandler().post(runnable);
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

      class ViewHolder {
        @InjectView(R.id.list)
        RecyclerView list;
        @InjectView(R.id.swipe_refresh)
        SwipeRefreshLayout swipeRefresh;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
