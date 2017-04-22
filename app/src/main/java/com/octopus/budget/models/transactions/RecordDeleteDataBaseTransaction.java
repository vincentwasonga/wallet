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

import com.octopus.budget.models.pbmodels.Record;
import com.octopus.budget.models.pbmodels.DataBaseTransaction;
import com.octopus.budget.models.notification.Notification;

/**
 * Created by octopus on 8/10/16.
 */
public class RecordDeleteDataBaseTransaction extends DataBaseTransaction {
    private Record record;
    private Notification notification;
    public RecordDeleteDataBaseTransaction(Context context, Context activityContext, Record r) {
        super(context, activityContext, DataBaseTransaction.RECORD_DELETE);
        this.setRecord(r);
        setNotification(new Notification(getActivityContext()));
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
        int balance = getDb().getAccountInfo().getTotalBal();
        if (authenticateDelete(balance)) {
            return false;
        }
        if (getRecord().getType().equals(Record.Income)) {
            getDb().updateAccountTotalBal(getDb().getAccountInfo().getTotalBal() - getRecord().getAmount());
        }
        else if (getRecord().getType().equals(Record.Expense)) {
            getDb().updateAccountDailySpent(getDb().getAccountInfo().getDailySpent() - getRecord().getAmount());
            getDb().updateAccountTotalBal(getDb().getAccountInfo().getTotalBal() + getRecord().getAmount());
        }
        getDb().deleteRecord(getRecord());
        return true;
    }
    private boolean authenticateDelete(int balance) {
        int newBal = balance;
        if (getRecord().getType().equals(Record.Income)) {
            newBal = balance - getRecord().getAmount();
        }
        return newBal < 0;
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
