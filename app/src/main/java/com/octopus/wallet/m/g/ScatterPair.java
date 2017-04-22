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

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by octopus on 10/8/16.
 */
public class ScatterPair {
    private List<Entry> entries;
    private List<String> days;
    private int count;
    public ScatterPair() {
        entries = new ArrayList<>();
        days = new ArrayList<>();
        count = 0;
    }
    public void addPairValue(int amount,
                             String day) {
        entries.add(new Entry(amount,
                count));
        days.add(day);
        count ++;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public List<String> getDays() {
        return days;
    }
}
