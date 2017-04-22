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

package com.octopus.wallet.m.tx;

import android.content.Context;

import com.octopus.wallet.m.p.fl.FData;
import com.octopus.wallet.m.p.fl.FManager;

import me.yoctopus.cac.tx.Tx;

/**
 * Created by yoctopus on 2/26/17.
 */
public abstract class FLTrans<T extends FData> extends Tx<T, T> {

    public FLTrans(Context context, int id) {
        super(context, id);
    }

    public FManager getFManager() {
        return new FManager(getContext());
    }
}
