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

import com.octopus.wallet.m.h.utl.Func;

import java.util.Date;

public class Day extends Period {

    public Day(Date start) {
        super(start, Func.getDateDayddMMM(start));
        super.setEnd(Func.addDays(start, 1));
    }
    public String getDayName() {
        return getName();
    }

    public Date getDate() {
        return getStart();
    }

    public void setDate(Date date) {
        this.setStart(date);
    }

    public boolean equals(Date date) {
        Day day1 = new Day(date);
        return this.equals(day1);
    }

    public boolean equals(Day day) {
        return getName().equals(
                day.getDayName());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
