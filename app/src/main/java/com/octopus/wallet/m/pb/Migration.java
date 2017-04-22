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

import java.util.Date;

public class Migration implements Parcelable, S {
    public static final int TYPE_FILE = 1;
    public static final int TYPE_DB = 2;
    public static final int TYPE_NET = 3;
    private int id;
    private int type;
    private String description;
    private Date date;

    public Migration(int type,
                     String description,
                     Date date) {
        this.type = type;
        this.description = description;
        this.date = date;
    }
    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.description);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected Migration(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.description = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Parcelable.Creator<Migration> CREATOR = new Parcelable.Creator<Migration>() {
        @Override
        public Migration createFromParcel(Parcel source) {
            return new Migration(source);
        }

        @Override
        public Migration[] newArray(int size) {
            return new Migration[size];
        }
    };
}
