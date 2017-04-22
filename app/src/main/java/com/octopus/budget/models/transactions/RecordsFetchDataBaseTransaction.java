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
import com.octopus.budget.models.pbmodels.TempData;
import com.octopus.budget.models.pbmodels.DataBaseTransaction;

import java.util.ArrayList;

/**
 * Created by octopus on 6/23/16.
 */
public class RecordsFetchDataBaseTransaction extends DataBaseTransaction {
    private Account account;

    public RecordsFetchDataBaseTransaction(Context context, Context activity, Account account) {
        super(context, activity, DataBaseTransaction.RECORDS);
        this.setAccount(account);
    }

    /**
     * save the records from the database
     * save the items from the database
     *
     * @param success
     **/
    @Override
    public void endTransaction(boolean success) {
        logTransaction("Fetch of records complete");
        TempData.setAccount(getAccount());
    }

    /**
     * method is called just before the task is started
     */
    @Override
    public void beforeExecuting() {
        logTransaction("Initiating fetch of records");
    }

    /**
     * get the records from the database
     * get the items from the database
     **/
    @Override
    public boolean execute() {
        getAccount().setRecords(organizeRecords(getDb().getRecords()));
        getAccount().setCategories(getDb().getItems());
        if (!getAccount().getRecords().isEmpty()) {
            for (Record r : getAccount().getRecords()) {
                logTransaction("Record " + r.getNameItem() + " amount " + r.getAmount());
            }
            return true;
        }
        return false;
    }
    private ArrayList<Record> organizeRecords(ArrayList<Record> records) {
        ArrayList<Record> records1 = new ArrayList<>();
        for(int i = records.size() - 1; i >= 0 ; i--) {
            records1.add(records.get(i));
        }
        return records1;
    }

    /**
     * show an updated message here while the task is continuing
     */
    @Override
    public void whileExecuting() {

    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
