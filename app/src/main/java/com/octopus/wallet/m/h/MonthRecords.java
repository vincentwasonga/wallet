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

import java.util.ArrayList;

/**
 * Created by yoctopus on 4/6/17.
 */

public class MonthRecords implements S, AnalysedData {
    private Month month;
    private ArrayList<WeekRecords> weekRecords;
    private int id;

    public MonthRecords(Month month,
                        ArrayList<WeekRecords> weekRecords,
                        int id) {
        this.month = month;
        this.weekRecords = weekRecords;
        this.id = id;
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getIncomeTotal() {
        return 0;
    }

    @Override
    public int getExpenseTotal() {
        return 0;
    }

    @Override
    public double getIncomePercentage() {
        return 0;
    }

    @Override
    public double getExpensePercentage() {
        return 0;
    }

    @Override
    public int getNumberOfIncomes() {
        return 0;
    }

    @Override
    public int getNumberOfExpenses() {
        return 0;
    }

    @Override
    public int getNumberOfRecords() {
        return 0;
    }

    @Override
    public int getBalance() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.month, flags);
        dest.writeTypedList(this.weekRecords);
        dest.writeInt(this.id);
    }

    protected MonthRecords(Parcel in) {
        this.month = in.readParcelable(Month.class.getClassLoader());
        this.weekRecords = in.createTypedArrayList(WeekRecords.CREATOR);
        this.id = in.readInt();
    }

    public static final Creator<MonthRecords> CREATOR = new Creator<MonthRecords>() {
        @Override
        public MonthRecords createFromParcel(Parcel source) {
            return new MonthRecords(source);
        }

        @Override
        public MonthRecords[] newArray(int size) {
            return new MonthRecords[size];
        }
    };
}
