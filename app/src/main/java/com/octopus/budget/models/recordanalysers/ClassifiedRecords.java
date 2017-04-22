/*
 * ﻿Copyright [2016] [Peter Vincent]
 * Licensed under the Apache License, Version 2.0 (Personal Budget);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.octopus.budget.models.recordanalysers;

import com.octopus.budget.models.Record;
import com.octopus.budget.models.math.HelperFunctions;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by octopus on 8/10/16.
 */
public final class ClassifiedRecords {
    private ArrayList<Record> records;
    private int id = -1;
    private Date date;
    private String string;
    private ClassifiedRecordPairValue classifiedRecordPairValue;


    public ClassifiedRecords(ArrayList<Record> records, int id) {
        this.records = records;
        this.id = id;
        if (!records.isEmpty()) {
            this.date = HelperFunctions.getDate(records.get(0).getDate_millis());
        }
    }
    public ClassifiedRecords(String date) {
        this (new ArrayList<Record>(), 0);
        this.string = date;

    }
    private int getBalanceFromRecords() {
        int sum = 0;
        for (Record r : getRecords()) {
            if (r.getType().equals(Record.Income)) {
                sum += r.getAmount();
            }
            else if (r.getType().equals(Record.Expense)){

                sum -= r.getAmount();

            }
        }
        return sum;
    }
    public ClassifiedRecordPairValue getClassifiedRecordPairValue() {
        classifiedRecordPairValue = new ClassifiedRecordPairValue(string, getBalanceFromRecords());
        return classifiedRecordPairValue;
    }

    public void addRecord(Record r) {
        records.add(r);
    }
    public boolean hasRecords() {
        return !records.isEmpty();
    }

    public ArrayList<Record> getRecordsType(String type) {
        ArrayList<Record> records = new ArrayList<>();
        for (Record r : getRecords()) {
            if (r.getType().equals(type)) {
                records.add(r);
            }
        }
        return records;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
