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

/**
 * Created by yoctopus on 4/6/17.
 */

public class Week extends Period {

    public Week(Date start) {
        super(start,
                Func.addDays(start, 7),
                Func.getDateddMMM(start) +
                " - "
                + Func.getDateDayddMMM(Func.addDays(start, 7)));
    }

    public String getWeekName() {
        return getName();
    }

    public void setWeekName(String weekName) {
        this.setName(weekName);
    }


}
