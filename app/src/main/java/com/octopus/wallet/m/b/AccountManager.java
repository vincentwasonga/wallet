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

package com.octopus.wallet.m.b;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import me.yoctopus.cac.pref.InvalidPreference;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import me.yoctopus.cac.pref.Preferences;


public class AccountManager {
    private static final String name_name = "acc_name";
    private static final String pin_name = "acc_pin";
    private static final String limit_name = "acc_limit";
    private static final String spent_name = "acc_spent";
    private static final String bal_name = "acc_bal";
    private Preferences preferences;
    public AccountManager(Context context) {
        preferences = new Preferences(context);
    }
    @Nullable public Info getInfo() {
        Pref<Integer> pin = new Pref<>(pin_name, 0),
                spent = new Pref<>(spent_name, 0),
                limit = new Pref<>(limit_name, 0),
                bal = new Pref<>(bal_name, 0);
        Pref<String> name = new Pref<>(name_name, "Wallet");
        try {
            int info_pin = preferences.getPreference(pin);
            String info_name = preferences.getPreference(name);
            int info_spent = preferences.getPreference(spent);
            int info_limit = preferences.getPreference(limit);
            int info_bal = preferences.getPreference(bal);
            return new Info(
                    info_name,
                    info_pin,
                    info_spent,
                    info_limit,
                    info_bal);
        } catch (InvalidPreferenceType invalidPreferenceType) {
            invalidPreferenceType.printStackTrace();
            return null;
        }

    }
    public boolean updateInfo(Info info) {
        Pref<Integer> pin = new Pref<>(pin_name, info.getPin());
        Pref<String> name = new Pref<>(name_name,info.getName());
        Pref<Integer> spent = new Pref<>(spent_name, info.getDailySpent());
        Pref<Integer> limit = new Pref<>(limit_name, info.getDailyLimit());
        Pref<Integer> bal = new Pref<>(bal_name, info.getTotalBal());
        try {
            preferences.savePreference(pin);
            preferences.savePreference(name);
            preferences.savePreference(spent);
            preferences.savePreference(limit);
            preferences.savePreference(bal);
        } catch (InvalidPreference invalidPreference) {
            invalidPreference.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updateSpent(int spent) {
        Pref<Integer> preference = new Pref<>(spent_name, spent);
        try {
            preferences.savePreference(preference);
            return true;
        } catch (InvalidPreference invalidPreference) {
            invalidPreference.printStackTrace();
            return false;
        }
    }
    public boolean updateLimit(int limit) {
        Pref<Integer> preference = new Pref<>(limit_name, limit);
        try {
            preferences.savePreference(preference);
            return true;
        } catch (InvalidPreference invalidPreference) {
            invalidPreference.printStackTrace();
            return false;
        }
    }
    public boolean updatePin(int pin) {
        Pref<Integer> preference = new Pref<>(pin_name, pin);
        try {
            preferences.savePreference(preference);
            return true;
        } catch (InvalidPreference invalidPreference) {
            invalidPreference.printStackTrace();
            return false;
        }
    }
    public boolean updateBal(int bal) {
        Pref<Integer> preference = new Pref<>(bal_name, bal);
        try {
            preferences.savePreference(preference);
            return true;
        } catch (InvalidPreference invalidPreference) {
            invalidPreference.printStackTrace();
            return false;
        }
    }
    public boolean updateName(String name) {
        Pref<String> preference = new Pref<>(name_name, name);
        try {
            preferences.savePreference(preference);
            return true;
        } catch (InvalidPreference invalidPreference) {
            invalidPreference.printStackTrace();
            return false;
        }
    }

    public static class Info implements Parcelable {
        public static final Creator<Info> CREATOR =
                new Creator<Info>() {
                    @Override
                    public Info createFromParcel(Parcel source) {
                        return new Info(source);
                    }

                    @Override
                    public Info[] newArray(int size) {
                        return new Info[size];
                    }
                };
        private String Name;
        private int pin;
        private int dailySpent;
        private int dailyLimit;
        private int totalBal;


        public Info(String name,
                    int pin,
                    int dailySpent,
                    int dailyLimit,
                    int totalBal) {
            setName(name);
            this.setPin(pin);
            this.setDailySpent(dailySpent);
            this.setDailyLimit(dailyLimit);
            this.setTotalBal(totalBal);
        }

        protected Info(Parcel in) {
            this.Name = in.readString();
            this.pin = in.readInt();
            this.dailySpent = in.readInt();
            this.dailyLimit = in.readInt();
            this.totalBal = in.readInt();
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public int getPin() {
            return pin;
        }

        public void setPin(int pin) {
            this.pin = pin;
        }

        public int getDailySpent() {
            return dailySpent;
        }

        public void setDailySpent(int dailySpent) {
            this.dailySpent = dailySpent;
        }

        public int getDailyLimit() {
            return dailyLimit;
        }

        public void setDailyLimit(int dailyLimit) {
            this.dailyLimit = dailyLimit;
        }

        public int getTotalBal() {
            return totalBal;
        }

        public void setTotalBal(int totalBal) {
            this.totalBal = totalBal;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest,
                                  int flags) {
            dest.writeString(this.Name);
            dest.writeInt(this.pin);
            dest.writeInt(this.dailySpent);
            dest.writeInt(this.dailyLimit);
            dest.writeInt(this.totalBal);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null ||
                    getClass() != o.getClass())
                return false;

            Info that = (Info) o;

            return getPin() == that.getPin() &&
                    getDailySpent() ==
                            that.getDailySpent() &&
                    getDailyLimit() ==
                            that.getDailyLimit() &&
                    getTotalBal() ==
                            that.getTotalBal() &&
                    getName().equals(that.getName());
        }

        @Override
        public int hashCode() {
            int result = getName().hashCode();
            result = 31 * result + getPin();
            result = 31 * result + getDailySpent();
            result = 31 * result + getDailyLimit();
            result = 31 * result + getTotalBal();
            return result;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "Name='" +
                    Name +
                    '\'' +
                    ", pin=" + pin +
                    ", dailySpent=" +
                    dailySpent +
                    ", dailyLimit=" +
                    dailyLimit +
                    ", totalBal=" +
                    totalBal +
                    '}';
        }
    }
}
