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

import android.os.Parcel;

import com.octopus.wallet.m.b.S;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Record;

import java.util.ArrayList;

import me.yoctopus.cac.util.LogUtil;

/**
 * Created by yoctopus on 4/6/17.
 */
public final class DayRecords implements S, AnalysedData {
    private String TAG = LogUtil.makeTag(DayRecords.class);
    private ArrayList<Record> records;
    private int id = -1;
    private Day day;
    private DayRecordsPairValue dayRecordsPairValue;

    public DayRecords(ArrayList<Record> records,
                      int id) {
        this.records = records;
        this.id = id;
    }

    public DayRecords(Day day) {
        this(new ArrayList<Record>(),
                0);
        this.day = day;
    }


    public ArrayList<Record> getIncomes() {
        ArrayList<Record> records = new ArrayList<>();
        for (Record r : getRecords()) {
            if (r.isIncome()) {
                records.add(r);
            }
        }
        return records;
    }

    public ArrayList<Record> getExpenses() {
        ArrayList<Record> records = new ArrayList<>();
        for (Record r : getRecords()) {
            if (r.isExpense()) {
                records.add(r);
            }
        }
        return records;
    }

    public int getIncomeTotal() {
        ArrayList<Record> records = getIncomes();
        int sum = 0;
        for (Record r : records) {
            sum += r.getAmount();
        }
        return sum;
    }

    public int getExpenseTotal() {
        ArrayList<Record> records = getExpenses();
        int sum = 0;
        for (Record r : records) {
            sum += r.getAmount();
        }
        return sum;
    }

    @Override
    public double getIncomePercentage() {
        int sum = 0;
        for (Record record : records) {
            sum += record.getAmount();
        }

        return Func.getPercentage(getIncomeTotal(), sum);
    }

    @Override
    public double getExpensePercentage() {
        int sum = 0;
        for (Record record : records) {
            sum += record.getAmount();
        }

        return Func.getPercentage(getExpenseTotal(), sum);
    }

    @Override
    public int getNumberOfIncomes() {
        return getIncomes().size();
    }

    @Override
    public int getNumberOfExpenses() {
        return getExpenses().size();
    }

    @Override
    public int getNumberOfRecords() {
        return getRecords().size();
    }

    @Override
    public int getBalance() {
        int sum = 0;
        for (Record r : getRecords()) {
            if (r.isIncome()) {
                sum += r.getAmount();
            } else {
                sum -= r.getAmount();
            }
        }
        return sum;
    }

    public DayRecordsPairValue getDayRecordsPairValue() {
        dayRecordsPairValue = new DayRecordsPairValue(day,
                getBalance());
        return dayRecordsPairValue;
    }

    public void addRecord(Record r) {
        if (r != null &&
                records != null) {
            records.add(r);
        }
    }

    public boolean hasRecords() {
        return !records.isEmpty();
    }


    public String getRecordsDateName() {
        return getName();
    }


    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.TAG);
        dest.writeTypedList(this.records);
        dest.writeInt(this.id);
        dest.writeParcelable(this.day, flags);
        dest.writeParcelable(this.dayRecordsPairValue, flags);
    }

    protected DayRecords(Parcel in) {
        this.TAG = in.readString();
        this.records = in.createTypedArrayList(Record.CREATOR);
        this.id = in.readInt();
        this.day = in.readParcelable(Day.class.getClassLoader());
        this.dayRecordsPairValue = in.readParcelable(DayRecordsPairValue.class.getClassLoader());
    }

    public static final Creator<DayRecords> CREATOR = new Creator<DayRecords>() {
        @Override
        public DayRecords createFromParcel(Parcel source) {
            return new DayRecords(source);
        }

        @Override
        public DayRecords[] newArray(int size) {
            return new DayRecords[size];
        }
    };

    @Override
    public String getName() {
        return day.getDayName();
    }

}
