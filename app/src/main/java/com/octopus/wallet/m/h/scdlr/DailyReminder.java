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

package com.octopus.wallet.m.h.scdlr;

import android.content.Context;

import com.octopus.wallet.m.h.ntnt.INTENT_ACTION;
import com.octopus.wallet.m.h.ntnt.INTENT_MESSAGE;
import com.octopus.wallet.r.BReceiver;

import java.util.Calendar;

import me.yoctopus.cac.util.Scheduler;

/**
 * Created by octopus on 10/10/16.
 */
public class DailyReminder extends Scheduler {
    public DailyReminder(Context context) {
        super(context, BReceiver.class);
    }

    @Override
    public Params onGetParam() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        Params one = new Params(
                INTENT_ACTION.DAILY_REMINDER_ACTION,
                INTENT_MESSAGE.REMINDER_MESSAGE,
                calendar);
        one.setInterval(INTERVAL_DAY);
        one.setRepeating(true);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        return one;
    }

}
