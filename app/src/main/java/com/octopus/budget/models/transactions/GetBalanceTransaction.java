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

package com.octopus.budget.models.transactions;

import android.content.Context;

import com.octopus.budget.models.pbmodels.Transaction;


/**
 * Created by octopus on 6/23/16.
 */
public class GetBalanceTransaction extends Transaction {
    private int Bal;

    public GetBalanceTransaction(Context context, Context activityContext) {
        super(context, activityContext, 0);
    }

    @Override
    public void endTransaction(boolean success) {

    }

    /**
     * method is called just before the task is started
     */
    @Override
    public void beforeExecuting() {

    }

    @Override
    public boolean execute() {

        return false;
    }

    /**
     * show an updated message here while the task is continuing
     */
    @Override
    public void whileExecuting() {

    }

    public int getBal() {
        return Bal;
    }

    public void setBal(int bal) {
        Bal = bal;
    }
}
