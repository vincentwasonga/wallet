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
import com.octopus.budget.models.pbmodels.Record;
import com.octopus.budget.models.pbmodels.Transaction;
import com.octopus.budget.models.math.Func;
import com.octopus.budget.models.notification.Notification;


/**
 * Created by octopus on 6/23/16.
 */
public class AddExpenseTransaction extends Transaction {
    private boolean ans;
    private Record record;
    private Notification notification;
    private boolean updateDailySpendt = true;

    public AddExpenseTransaction(Context context, Context activity, Record r) {
        super(context, activity, Transaction.EXPENSE);
        this.setRecord(r);
        setNotification(new Notification(activity));
        setAns(false);
        if (r.getDate_millis() != Record.DEFAULT_MILLIS) {
            updateDailySpendt = Func.isToday(r.getDate_millis());
        }

    }

    /**
     * finalize with expense transaction id
     *
     * @param success
     **/
    @Override
    public void endTransaction(boolean success) {
        logTransaction("Adding expense end");
        getNotification().setNotificationBundle("Expense", "Expense transacted");
        getNotification().notify(Notification.TOAST);
    }

    /**
     * method is called just before the task is started
     */
    @Override
    public void beforeExecuting() {
        logTransaction("Adding expense begin");
    }

    /**
     * add the record to the database
     * increment the daily spent amount
     * decrement the account total balance
     **/
    @Override
    public boolean execute() {
        boolean addRecord = getDb().addRecord(getRecord());
        logTransaction("Adding record " + getRecord().getNameItem() + "of amount " + getRecord().getAmount() + " " + addRecord);
        Account.AccountInfo info = getDb().getAccountInfo();
        logTransaction("Getting current account info" + info.getName());
        int totalBal = info.getTotalBal();
        if (updateDailySpendt) {
            int dailyspent = info.getDailySpent();
            getDb().updateAccountDailySpent(dailyspent + getRecord().getAmount());
        }

        boolean minusTotal = getDb().updateAccountTotalBal(totalBal - getRecord().getAmount());
        logTransaction("updating account total balance " + minusTotal);
        if (addRecord && minusTotal) {
            logTransaction("Transaction successful");
            return true;
        }
        logTransaction("Transaction unsuccessful");
        return false;
    }


    /**
     * show an updated message here while the task is continuing
     */
    @Override
    public void whileExecuting() {

    }

    public boolean isAns() {
        return ans;
    }

    public void setAns(boolean ans) {
        this.ans = ans;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
