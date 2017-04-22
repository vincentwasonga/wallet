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

import com.octopus.wallet.m.h.utl.Func;

/**
 * Created by octopus on 9/12/16.
 */
public class DayRecordsPairValue implements Parcelable {
    private Day day;
    private int balance;

    public DayRecordsPairValue(Day day,
                               int balance) {
        this.day = day;
        this.balance = balance;
    }



    public String getDateName() {
        return Func.getDateddMMM(day.getDate());
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.day, flags);
        dest.writeInt(this.balance);
    }

    protected DayRecordsPairValue(Parcel in) {
        this.day = in.readParcelable(Day.class.getClassLoader());
        this.balance = in.readInt();
    }

    public static final Creator<DayRecordsPairValue> CREATOR = new Creator<DayRecordsPairValue>() {
        @Override
        public DayRecordsPairValue createFromParcel(Parcel source) {
            return new DayRecordsPairValue(source);
        }

        @Override
        public DayRecordsPairValue[] newArray(int size) {
            return new DayRecordsPairValue[size];
        }
    };
}
