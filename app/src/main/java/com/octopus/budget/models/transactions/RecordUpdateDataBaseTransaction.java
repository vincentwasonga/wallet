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
import android.util.Log;

import com.octopus.budget.models.pbmodels.DataBaseTransaction;
import com.octopus.budget.models.pbmodels.Record;
import com.octopus.budget.models.math.Func;
import com.octopus.budget.models.recordanalysers.UpdatePackage;


/**
 * Created by octopus on 9/2/16.
 */
public class RecordUpdateDataBaseTransaction extends DataBaseTransaction {
    private UpdatePackage updatePackage;
    private String TAG = "RecordUpdate";
    private boolean updateDailySpent = false;
    private String originalType;
    private int originalAmount;
    private Record record;

    public RecordUpdateDataBaseTransaction(Context context, Context activityContext, UpdatePackage updatePackage) {
        super(context, activityContext, DataBaseTransaction.RECORD_UPDATE);
        this.updatePackage = updatePackage;
        this.originalType = updatePackage.getOriginalType();
        this.originalAmount = updatePackage.getOriginalAmount();
        this.record = updatePackage.getRecord();
        updateDailySpent = Func.isToday(updatePackage.getRecord().getDate_millis());
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
        logTransaction("executing ");
        int balance = getDb().getAccountInfo().getTotalBal();
        int spent = getDb().getAccountInfo().getDailySpent();
        if (authenticateUpdate(balance)) {
            Log.i(TAG, "execute: exiting...");
            return false;
        }
        Log.i(TAG, "execute: updating");
        if (originalType.equals(record.getType())) {
            Log.i(TAG, "execute: same types");
            if (originalType.equals(Record.Income)) {
                Log.i(TAG, "execute: income types");
                if (originalAmount < record.getAmount()) {
                    Log.i(TAG, "execute: new amount is higher than old amount");
                    int difference = record.getAmount() - originalAmount;
                    balance += difference;
                    getDb().updateAccountTotalBal(balance);
                } else if (originalAmount > record.getAmount()) {
                    Log.i(TAG, "execute: new amount is smaller than old");
                    int difference = originalAmount - record.getAmount();
                    balance -= difference;
                    getDb().updateAccountTotalBal(balance);
                }
            } else if (originalType.equals(Record.Expense)) {
                Log.i(TAG, "execute: expense types");
                if (originalAmount < record.getAmount()) {
                    Log.i(TAG, "execute: new amount bigger than old amount");
                    int difference = record.getAmount() - originalAmount;
                    balance -= difference;
                    if (updateDailySpent) {
                        spent = spent + difference;
                        getDb().updateAccountDailySpent(spent);
                    }
                    getDb().updateAccountTotalBal(balance);

                } else if (originalAmount > record.getAmount()) {
                    Log.i(TAG, "execute: old amount is hegher than new");
                    int difference = originalAmount - record.getAmount();
                    balance += difference;
                    if (updateDailySpent) {
                        spent = spent - difference;
                        getDb().updateAccountDailySpent(spent);
                    }
                    getDb().updateAccountTotalBal(balance);
                }
            }
        } else if (originalType.equals(Record.Income) && record.getType().equals(Record.Expense)) {
            Log.i(TAG, "execute: income to expense");
            Log.i(TAG, "execute: reducing balance");
            getDb().updateAccountTotalBal(balance - originalAmount - record.getAmount());
            getDb().updateAccountDailySpent(spent + record.getAmount());
        } else if (originalType.equals(Record.Expense) && record.getType().equals(Record.Income)) {
            Log.i(TAG, "execute: expense to income");
            Log.i(TAG, "execute: increasing balance");
            getDb().updateAccountTotalBal(balance + originalAmount + record.getAmount());
            getDb().updateAccountDailySpent(spent - originalAmount);
        }
        getDb().updateRecord(record);
        return true;
    }

    private boolean authenticateUpdate(int newBal) {
        Log.i(TAG, "authenticateUpdate: ");
        int balance = newBal;
        if (originalType.equals(record.getType())) {
            if (originalType.equals(Record.Income)) {
                if (originalAmount < record.getAmount()) {
                    int difference = record.getAmount() - originalAmount;
                    balance += difference;
                } else if (originalAmount > record.getAmount()) {
                    int difference = originalAmount - record.getAmount();
                    balance -= difference;
                }
            } else if (originalType.equals(Record.Expense)) {
                if (originalAmount < record.getAmount()) {
                    int difference = record.getAmount() - originalAmount;
                    balance -= difference;
                } else if (originalAmount > record.getAmount()) {
                    int difference = originalAmount - record.getAmount();
                    balance += difference;
                }
            }
        } else if (originalType.equals(Record.Income) && record.getType().equals(Record.Expense)) {
            balance = balance - originalAmount - record.getAmount();
        } else if (originalType.equals(Record.Expense) && record.getType().equals(Record.Income)) {
            balance = balance + originalAmount + record.getAmount();
        }
        return balance < 0;
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

    public boolean isUpdateDailySpent() {
        return updateDailySpent;
    }

    public void setUpdateDailySpent(boolean updateDailySpent) {
        this.updateDailySpent = updateDailySpent;
    }

    public String getOriginalType() {
        return originalType;
    }

    public void setOriginalType(String originalType) {
        this.originalType = originalType;
    }

    public int getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(int originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
