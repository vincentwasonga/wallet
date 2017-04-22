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

import com.octopus.wallet.m.e.RecordsEmpty;
import com.octopus.wallet.m.pb.Record;

import java.util.ArrayList;

/**
 * Created by octopus on 9/30/16.
 */
public class RecordsToPairsConverter {
    private ArrayList<Record> records;
    private ArrayList<RecordPair> recordPairs;

    public RecordsToPairsConverter(ArrayList<Record> records) {
        this.records = records;
        recordPairs = new ArrayList<>();
    }

    public void convert() throws RecordsEmpty {
        if (records.isEmpty() || records == null) {
            throw new RecordsEmpty();
        }
        for (int i = 0; i < records.size(); i += 2) {
            Record r = records.get(i);
            Record r1 = null;
            if (i != (records.size() -
                    1)) {
                r1 = records.get(i +
                        1);
            }
            if (r1 == null) {
                if (r.isIncome()) {
                    recordPairs.add(
                            new RecordPair(r));
                }
                if (r.isExpense()) {
                    recordPairs.add(
                            new RecordPair(r,
                                    0));
                }
            } else {
                if (r.isIncome() &&
                        r1.isIncome()) {
                    recordPairs.add(
                            new RecordPair(r));
                    recordPairs.add(
                            new RecordPair(r1));
                }
                if (r.isExpense() &&
                        r1.isIncome()) {
                    recordPairs.add(
                            new RecordPair(r1,
                                    r));
                }
                if (r.isIncome() &&
                        r1.isExpense()) {
                    recordPairs.add(
                            new RecordPair(r,
                                    r1));
                }
                if (r.isExpense() &&
                        r1.isExpense()) {
                    recordPairs.add(
                            new RecordPair(r,
                                    0));
                    recordPairs.add(
                            new RecordPair(r1,
                                    0));
                }
            }
        }

    }

    public ArrayList<RecordPair> getRecordPairs() {
        return recordPairs;
    }
}
