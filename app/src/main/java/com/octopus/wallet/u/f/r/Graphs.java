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

package com.octopus.wallet.u.f.r;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octopus.wallet.R;
import com.octopus.wallet.m.h.AnalysedData;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.MonthRecords;
import com.octopus.wallet.m.h.WeekRecords;

public class Graphs extends Fragment {
    private static final String DATA = "data";
    private static final int TYPE_DAY = 1;
    private static final int TYPE_WEEK = 2;
    private static final int TYPE_MONTH = 3;
    private int type;
    private AnalysedData data;
    private OnInteraction listener;

    public Graphs() {

    }

    public static Graphs newInstance(AnalysedData analysedData) {
        Graphs fragment = new Graphs();
        Bundle args = new Bundle();
        args.putParcelable(DATA, analysedData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = getArguments().getParcelable(DATA);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int layout;
        assert data != null;
        if (data instanceof DayRecords) {
            type = TYPE_DAY;
            layout = R.layout.graph_day_records;
        }
        else if (data instanceof WeekRecords) {
            type = TYPE_WEEK;
            layout = R.layout.graph_week_records;
        }
        else if (data instanceof MonthRecords) {
            type = TYPE_MONTH;
            layout = R.layout.graph_month_records;
        }
        else {
            throw new RuntimeException("Unknown condition in choosing" +
                    "layout file for graphs");
        }
        return inflater.inflate(
                layout,
                container,
                false);
    }

    @Override
    public void onViewCreated(View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (type) {
            case TYPE_DAY:
                //TODO init views
                showDayRecords((DayRecords) data);
                break;
            case TYPE_WEEK:
                //TODO init views
                showWeekRecords((WeekRecords) data);
                break;
            case TYPE_MONTH:
                //TODO init views
                showMonthRecords((MonthRecords) data);
                break;
        }
    }
    private void showDayRecords(DayRecords dayRecord) {

    }
    private void showWeekRecords(WeekRecords weekRecords) {

    }
    private void showMonthRecords(MonthRecords monthRecords) {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInteraction) {
            listener = (OnInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    public interface OnInteraction {
        void onInteraction(Uri uri);
    }
}
