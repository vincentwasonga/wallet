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
import com.octopus.budget.models.notification.Notification;


/**
 * Created by octopus on 6/23/16.
 */
public class AddIncomeTransaction extends Transaction {
    private Record record;
    private Notification notification;

    public AddIncomeTransaction(Context context, Context activity, Record r) {
        super(context, activity, Transaction.INCOME);
        this.setRecord(r);
        setNotification(new Notification(activity));
    }

    /**
     * finalize the transaction normally, no data sent back to the activity
     *
     * @param success
     **/
    @Override
    public void endTransaction(boolean success) {
        logTransaction("Adding income end");


    }

    /**
     * method is called just before the task is started
     */
    @Override
    public void beforeExecuting() {
        logTransaction("Adding income begin");
    }

    /**
     * add the current record to the records table
     * update the total column of the account table incrementing with the new record amount
     **/
    @Override
    public boolean execute() {
        boolean addRecord = getDb().addRecord(getRecord());
        logTransaction("Adding record " + getRecord().getNameItem() + " amount " + getRecord().getAmount() + " " + addRecord);
        Account.AccountInfo info = getDb().getAccountInfo();
        logTransaction("Getting current account info " + info.getName());
        int totalBal = info.getTotalBal();
        boolean addTotal = getDb().updateAccountTotalBal(totalBal + getRecord().getAmount());
        logTransaction("updating current total balance " + addTotal);
        if (addRecord && addTotal) {
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
