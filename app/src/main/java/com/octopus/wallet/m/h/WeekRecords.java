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
import android.os.Parcelable;

import com.octopus.wallet.m.b.S;

import java.util.ArrayList;

public final class WeekRecords implements S, AnalysedData {

    private Week week;
    private ArrayList<DayRecords> dayRecords;
    private int id = -1;
    public WeekRecords(Week week,
                       ArrayList<DayRecords> dayRecords,
                       int id) {
        this.week = week;
        this.dayRecords = dayRecords;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.week, flags);
        dest.writeTypedList(this.dayRecords);
        dest.writeInt(this.id);
    }

    protected WeekRecords(Parcel in) {
        this.week = in.readParcelable(Week.class.getClassLoader());
        this.dayRecords = in.createTypedArrayList(DayRecords.CREATOR);
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<WeekRecords> CREATOR = new Parcelable.Creator<WeekRecords>() {
        @Override
        public WeekRecords createFromParcel(Parcel source) {
            return new WeekRecords(source);
        }

        @Override
        public WeekRecords[] newArray(int size) {
            return new WeekRecords[size];
        }
    };

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
}
