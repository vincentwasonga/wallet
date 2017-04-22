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

package com.octopus.wallet.m.p.fl.expoimpo.data;

import com.octopus.wallet.m.h.DayRecords;

import java.util.ArrayList;

/**
 * Created by octopus on 10/2/16.
 */
public class Ledger extends Template {
    private ArrayList<DayRecords> dayRecords;
    private int total;

    public Ledger(String name,
                  boolean printBalance,
                  ArrayList<DayRecords> dayRecords,
                  int total) {
        super(name, printBalance);
        this.dayRecords = dayRecords;
        this.total = total;
    }

    public Ledger(String name,
                  boolean printBalance,
                  Filter filter,
                  ArrayList<DayRecords> dayRecords,
                  int total) {
        super(name, printBalance, filter);
        this.dayRecords = dayRecords;
        this.total = total;
    }

    public ArrayList<DayRecords> getDayRecords() {
        return dayRecords;
    }

    public void setDayRecords(ArrayList<DayRecords> dayRecords) {
        this.dayRecords = dayRecords;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
