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

package com.octopus.budget.models.pbmodels;


import android.os.Parcel;
import android.os.Parcelable;

import com.octopus.budget.models.persistence.db.Statement;

/**
 * Created by octopus on 7/2/16.
 */
public class Item implements Parcelable {
    public static String INCOME_TYPE = Statement.Types.INCOME_TYPE;
    public static String EXPENSE_TYPE = Statement.Types.EXPENSE_TYPE;
    public static final String DEFAULT = "DEFAULT";
    private String type;
    private String name;
    public Item(String type, String name) {
        this.setType(type);
        this.setName(name);

    }
    public boolean isDefault() {
        return this.type.equals(DEFAULT);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.name);
    }

    protected Item(Parcel in) {
        this.type = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
