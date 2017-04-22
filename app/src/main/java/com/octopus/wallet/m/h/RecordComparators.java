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

import com.octopus.wallet.m.pb.Record;

import java.util.Comparator;

/**
 * Created by octopus on 9/12/16.
 */
public class RecordComparators {
    public static Comparator<Record> CompareWithName =
            new Comparator<Record>() {
                @Override
                public int compare(Record record,
                                   Record t1) {
                    return record.getName()
                            .compareTo(t1.getName());
                }
            };
    public static Comparator<Record> CompareWithTime =
            new Comparator<Record>() {
                @Override
                public int compare(Record record,
                                   Record t1) {
                    if (record.getDate_millis() >
                            t1.getDate_millis()) {
                        return -1;
                    } else if (record.getDate_millis()
                            < t1.getDate_millis()) {
                        return 1;
                    } else
                        return 0;
                }
            };
    public static Comparator<Record> CompareWithAmount =
            new Comparator<Record>() {
                @Override
                public int compare(Record record,
                                   Record t1) {
                    return record.getAmount() -
                            t1.getAmount();
                }
            };
    public static Comparator<DayRecords> CompareWithTime2 =
            new Comparator<DayRecords>() {
                @Override
                public int compare(DayRecords records,
                                   DayRecords t1) {
                    if (records.hasRecords() &&
                            t1.hasRecords()) {
                        if (records.getRecords().get(0)
                                .getDate_millis() >
                                t1.getRecords().get(0)
                                        .getDate_millis()) {
                            return -1;
                        } else if (records.getRecords().get(0)
                                .getDate_millis() <
                                t1.getRecords().get(0)
                                        .getDate_millis()) {
                            return 1;
                        } else
                            return 0;
                    } else {
                        return 0;
                    }
                }
            };
}
