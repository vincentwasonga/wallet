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

package com.octopus.wallet.m.pb;

import android.os.Parcel;
import android.os.Parcelable;

import com.octopus.wallet.m.b.S;

/**
 * Created by yoctopus on 3/19/17.
 */

public class Debt implements Parcelable, S {
    public static final int LEND = 1;
    public static final int BORROW = 2;
    private int type;
    private int id;
    private String name;
    private String description;
    private int amount;
    private int payedBack;
    private long effectedDate;
    private long expectedPayback;


    public Debt(int type,
                String name,
                String description,
                int amount,
                long effectedDate) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.effectedDate = effectedDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getEffectedDate() {
        return effectedDate;
    }

    public void setEffectedDate(long effectedDate) {
        this.effectedDate = effectedDate;
    }

    public long getExpectedPayback() {
        return expectedPayback;
    }

    public void setExpectedPayback(long expectedPayback) {
        this.expectedPayback = expectedPayback;
    }
    public boolean isLent() {
        return type == LEND;
    }
    public boolean isBorrowed() {
        return type == BORROW;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPayedBack() {
        return payedBack;
    }
    public boolean isFinalized() {
        return payedBack == amount;
    }

    public void setPayedBack(int payedBack) {
        this.payedBack = payedBack;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeInt(this.amount);
        dest.writeInt(this.payedBack);
        dest.writeLong(this.effectedDate);
        dest.writeLong(this.expectedPayback);
    }

    protected Debt(Parcel in) {
        this.type = in.readInt();
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.amount = in.readInt();
        this.payedBack = in.readInt();
        this.effectedDate = in.readLong();
        this.expectedPayback = in.readLong();
    }

    public static final Parcelable.Creator<Debt> CREATOR = new Parcelable.Creator<Debt>() {
        @Override
        public Debt createFromParcel(Parcel source) {
            return new Debt(source);
        }

        @Override
        public Debt[] newArray(int size) {
            return new Debt[size];
        }
    };
}
