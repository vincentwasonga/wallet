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

import com.octopus.budget.models.pbmodels.Account;
import com.octopus.budget.models.pbmodels.TempData;
import com.octopus.budget.models.pbmodels.Transaction;


/**
 * Created by octopus on 6/23/16.
 */
public class LoadAccountInfoTransaction extends Transaction {

    private Account.AccountInfo info;

    public LoadAccountInfoTransaction(Context context, Context activity) {
        super(context, activity, Transaction.INFO);


    }

    /**
     * @param success the boolean showing if the transaction was successful
     */
    @Override
    public void endTransaction(boolean success) {
        Account account = new Account(getContext());
        account.setInfo(getInfo());
        logTransaction("Loading info complete");
        if (success) {
            TempData.setAccount(account);
            logTransaction("Loading info for " + getInfo().getName());

        } else {
            TempData.getAccount().setInfo(null);
            logTransaction("Account info not loaded");

        }

    }

    /**
     * method is called just before the task is started
     */
    @Override
    public void beforeExecuting() {
        logTransaction("Initiating loading account info");
    }

    @Override
    public boolean execute() {
        setInfo(getDb().getAccountInfo());
        if (getInfo() != null) {
            logTransaction("info found " + getInfo().getName() +
                    " daily spent " + getInfo().getDailySpent() +
                    " daily limit " + getInfo().getDailyLimit() +
                    " total balance " + getInfo().getTotalBal());
            return true;
        } else {
            logTransaction("info not found");
            return false;
        }

    }

    /**
     * show an updated message here while the task is continuing
     */
    @Override
    public void whileExecuting() {

    }

    public Account.AccountInfo getInfo() {
        return info;
    }

    public void setInfo(Account.AccountInfo info) {
        this.info = info;
    }
}
