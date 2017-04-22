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

import com.octopus.wallet.m.e.Classify;
import com.octopus.wallet.m.e.UnableToAddRecord;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Record;

import java.util.ArrayList;

import me.yoctopus.cac.util.LogUtil;

/**
 * Created by octopus on 8/10/16.
 */
public class RecordClassifier {
    private ArrayList<Day> days;
    private String TAG = LogUtil.makeTag(RecordClassifier.class);

    private ArrayList<Record> records;
    private ArrayList<DayRecords> dayRecords;

    public RecordClassifier(ArrayList<Record> records) {
        LogUtil.i(TAG,
                "RecordClassifier: Initialising");
        this.records = records;
        dayRecords = new ArrayList<>();
        days = new ArrayList<>();
        //configure days
        configureDays();
        for (Day day : days) {
            dayRecords.add(
                    new DayRecords(day));
        }
    }

    private void configureDays() {
        for (Record r : Func.reverse(records)) {
            Day day = new Day(
                    Func.getDate(r.getDate_millis()));
            if (!isAdded(day) &&
                    days != null) {
                days.add(day);
            }
        }
    }

    private boolean isAdded(Day day) {
        if (!days.isEmpty()) {
            for (Day d : days) {
                if (d.equals(day)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addRecord(Record r) throws UnableToAddRecord {
        if (dayRecords != null) {
            for (int i = 0; i < dayRecords.size(); i++) {

                Day rDay = new Day(
                        Func.getDate(r.getDate_millis()));
                Day cDay = dayRecords.get(i)
                        .getDay();
                if (rDay.equals(cDay)) {
                    dayRecords.get(i).addRecord(r);

                }
            }
        } else {
            throw new UnableToAddRecord();
        }

    }

    public void classify() throws Classify {
        for (Record r : records) {
            try {
                addRecord(r);
            } catch (UnableToAddRecord e) {
                throw new Classify();
            }
        }
    }



    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    public ArrayList<DayRecords> getDayRecords() {
        return dayRecords;
    }

    public void setDayRecords(ArrayList<DayRecords> dayRecords) {
        this.dayRecords = dayRecords;
    }
}
