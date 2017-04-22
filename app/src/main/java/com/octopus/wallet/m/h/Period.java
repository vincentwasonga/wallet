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

import java.util.Date;

/**
 * Created by yoctopus on 4/6/17.
 */

public class Period implements Parcelable {
    private Date start;
    private Date end;
    private String name;
    private long duration;

    public Period(Date start, Date end) {
        this.start = start;
        this.end = end;
        this.duration = end.getTime() - start.getTime();
        this.name = Func.getDateDayddMMM(start) + " - " + Func.getDateDayddMMM(end);
    }

    public Period(Date start, long duration) {
        this.start = start;
        this.duration = duration;
        this.end = new Date(start.getTime() + duration);
        this.name = Func.getDateDayddMMM(start) + " - " + Func.getDateDayddMMM(end);
    }

    public Period(Date start, Date end, String name) {
        this.start = start;
        this.end = end;
        this.duration = end.getTime() - start.getTime();
        this.name = name;
    }

    public Period(Date start, String name) {
        this.start = start;
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.start != null ? this.start.getTime() : -1);
        dest.writeLong(this.end != null ? this.end.getTime() : -1);
        dest.writeString(this.name);
        dest.writeLong(this.duration);
    }

    protected Period(Parcel in) {
        long tmpStart = in.readLong();
        this.start = tmpStart == -1 ? null : new Date(tmpStart);
        long tmpEnd = in.readLong();
        this.end = tmpEnd == -1 ? null : new Date(tmpEnd);
        this.name = in.readString();
        this.duration = in.readLong();
    }

    public static final Parcelable.Creator<Period> CREATOR = new Parcelable.Creator<Period>() {
        @Override
        public Period createFromParcel(Parcel source) {
            return new Period(source);
        }

        @Override
        public Period[] newArray(int size) {
            return new Period[size];
        }
    };
}
