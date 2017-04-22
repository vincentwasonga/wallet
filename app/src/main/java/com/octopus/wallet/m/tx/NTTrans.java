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

import android.app.Activity;
import android.content.Context;

import com.octopus.wallet.m.p.nt.NBridge;
import com.octopus.wallet.m.p.nt.NData;

import me.yoctopus.cac.tx.Tx;

public abstract class NTTrans<T extends NData> extends Tx<T, Integer>
        implements NBridge.Listener<T> {
    private NBridge bridge;
    public NTTrans(Context context,
                   int id) {
        super(context, id );
        bridge = new NBridge();
        bridge.setListener(this);
    }
    public NTTrans(Activity activity,
                   int id) {
        this(activity.getApplicationContext(),
                id);
    }

    public NBridge getBridge() {
        return bridge;
    }

    @Override
    public void onComplete(T t) {
        NTTrans.this.finalize(t);
    }
}
