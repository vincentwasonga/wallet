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
import com.octopus.budget.models.pbmodels.DataBaseTransaction;
import com.octopus.budget.models.notification.Notification;

/**
 * Created by octopus on 8/11/16.
 */
public class DeleteAllDataBaseTransaction extends DataBaseTransaction {
    private Account.AccountInfo info;
    private Notification notification;
    public DeleteAllDataBaseTransaction(Context context, Context activityContext, Account.AccountInfo info) {
        super(context, activityContext, DataBaseTransaction.DELETE_ALL);
        this.setInfo(info);
        setNotification(new Notification(activityContext));
    }

    /**
     * method is called just before the task is started
     */
    @Override
    public void beforeExecuting() {

    }

    /**
     * ecexute the main logic here
     */
    @Override
    public boolean execute() {
        getDb().deleteAllRecords();
        getDb().updateAccountPin(getInfo().getPin());
        getDb().updateAccountName(getInfo().getName());
        getDb().updateAccountTotalBal(getInfo().getTotalBal());
        getDb().updateAccountDailySpent(getInfo().getDailySpent());
        getDb().updateAccountDailyLimit(getInfo().getDailyLimit());
        return true;
    }

    /**
     * show an updated message here while the task is continuing
     */
    @Override
    public void whileExecuting() {

    }

    /**
     * call this end the transaction
     *
     * @param success the boolean showing if the transaction was successful
     */
    @Override
    public void endTransaction(boolean success) {
        getNotification().setNotificationBundle("Account Init", "Account reset successfully");
        notification.notify(Notification.TOAST);
    }

    public Account.AccountInfo getInfo() {
        return info;
    }

    public void setInfo(Account.AccountInfo info) {
        this.info = info;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
