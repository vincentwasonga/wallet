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

package com.octopus.wallet.m.h;

import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Record;

import java.util.ArrayList;

/**
 * Created by octopus on 9/5/16.
 */
public class RecordsArranger {

    private ArrayList<Record> records;
    private ArrayList<DayRecords> dayRecords;
    private ArrangeOrder order;

    public RecordsArranger(ArrayList<Record> records) {
        this.records = records;
        order = new ArrangeOrder();
    }

    public RecordsArranger(ArrayList<DayRecords> dayRecords,
                           int arg) {
        this.dayRecords = dayRecords;
        order = new ArrangeOrder();
    }

    public void sort(int sortID) {
        order.setOrder(sortID);
        records = Func.sortRecords(records,
                order);
    }

    public void sort2(int sortID) {
        order.setOrder(sortID);
        dayRecords = Func.sortDayRecords(dayRecords,
                order);
    }

    public ArrayList<DayRecords> getDayRecords() {
        return dayRecords;
    }

    public ArrayList<Record> getRecords() {
        return this.records;
    }
}
