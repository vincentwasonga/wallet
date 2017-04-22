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

package com.octopus.wallet.m.g;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * Created by octopus on 8/9/16.
 */
public class BarPair {
    private ArrayList<BarEntry> entries;
    private ArrayList<String> days;
    private int count;
    public BarPair() {
        entries = new ArrayList<>();
        days = new ArrayList<>();
        count = 0;
    }
    public void addPairValue(int amount,
                             String day) {
        entries.add(new BarEntry(amount,
                count));
        days.add(day);
        count ++;
    }

    public ArrayList<BarEntry> getEntries() {
        return entries;
    }



    public ArrayList<String> getDays() {
        return days;
    }
}
