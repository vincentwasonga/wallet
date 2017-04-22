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

import com.octopus.wallet.m.p.db.AppDatabase;

import me.yoctopus.cac.tx.Tx;
import me.yoctopus.cac.util.LogUtil;

public abstract class DBTrans<T> extends Tx<T, Integer> {

    private String TAG = LogUtil.makeTag(
            DBTrans.class);

    public DBTrans(final Context context,
                   final int id) {
        super(new Builder<AppDatabase>() {
            @Override
            public Context getContext() {
                return context;
            }

            @Override
            public AppDatabase get() {
                return new AppDatabase(context);
            }

            @Override
            public int getId() {
                return id;
            }
        });
    }

    public void logTransaction(String message) {
        LogUtil.i(TAG, message);
    }

    public AppDatabase getDb() {
        return (AppDatabase) getBuilder().get();
    }

}
