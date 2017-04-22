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

package com.octopus.wallet.m.a;

import android.support.annotation.LayoutRes;
import android.view.View;

import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.pb.RecurringPayment;

/**
 * Created by yoctopus on 3/20/17.
 */

public class RecurringPaymentsAdapter extends VBinder<RecurringPayment> {
    public RecurringPaymentsAdapter(SList<RecurringPayment> list,
                                    @LayoutRes int layout) {
        super(list, layout);
    }

    @Override
    public void onInit(View parent) {

    }

    @Override
    public void onBind(RecurringPayment payment) {

    }
}