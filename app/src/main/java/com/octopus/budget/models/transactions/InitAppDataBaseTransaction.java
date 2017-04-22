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

import com.octopus.budget.models.errors.ClassifyException;
import com.octopus.budget.models.pbmodels.Account;
import com.octopus.budget.models.pbmodels.Record;
import com.octopus.budget.models.pbmodels.TempData;
import com.octopus.budget.models.pbmodels.DataBaseTransaction;
import com.octopus.budget.models.recordanalysers.ArrangeOrder;
import com.octopus.budget.models.recordanalysers.DayRecords;
import com.octopus.budget.models.recordanalysers.RecordClassifier;
import com.octopus.budget.models.recordanalysers.RecordsArranger;

import java.util.ArrayList;

/**
 * Created by octopus on 7/21/16.
 */
public class InitAppDataBaseTransaction extends DataBaseTransaction {
    private Account account;
    private ArrayList<Record> records;
    private ArrayList<Record.Category> categories;
    private String TAG = "InitTransaction";
    public InitAppDataBaseTransaction(Context context, Context activityContext) {
        super(context, activityContext, DataBaseTransaction.INIT);
        Log.i(TAG, "InitAppDataBaseTransaction: ");
        account = null;
        records = new ArrayList<>();
        categories = new ArrayList<>();
    }

    /**
     * method is called just before the task is started
     */
    @Override
    public void beforeExecuting() {
        logTransaction("Initialising Pesa app");
    }

    /**
     * ecexute the main logic here
     */
    @Override
    public boolean execute() {
        account = new Account(getContext());
        logTransaction("loading records");
        records = getDb().getRecords();
        logTransaction("loading categories");
        categories = getSomeCategories();
        categories.addAll(getDb().getItems());
        logTransaction("saving organized records");
        RecordsArranger arranger = new RecordsArranger(records);
        arranger.sort(new ArrangeOrder(ArrangeOrder.TA).getOrder());
        records = arranger.getRecords();
        for(Record r : records) {
            logTransaction("Record loaded : "+r.getNameItem()+", "+r.getAmount());
        }
        account.setRecords(records);


        account.setCategories(categories);
        logTransaction("loading info");
        account.setInfo(getDb().getAccountInfo());
        logTransaction("info loaded\n"+account.getInfo().getName()+ account.getInfo().getPin());

        account.setDayRecords(classifyRecords(account.getRecords()));

        //logTransaction("sorting records");
        //account.sortClassifiedRecords();
        logTransaction("Classified records");
        for (DayRecords dayRecords : account.getDayRecords()) {
            for (Record r : dayRecords.getRecords()) {
                logTransaction("Record classified"+ dayRecords.getId() + r.getNameItem());
            }
        }
        return true;
    }
    private ArrayList<DayRecords> classifyRecords(ArrayList<Record> records) {
        ArrayList<DayRecords> classifiedRecodes;
        RecordClassifier classifier = new RecordClassifier(records);
        Log.i(TAG, "classifyRecords: ");
        try {
            classifier.classify();
        } catch (ClassifyException e) {
            e.printStackTrace();
        }
        classifiedRecodes = classifier.getDayRecords();
        return classifiedRecodes;
    }
    private ArrayList<Record.Category> getSomeCategories() {
        Log.i(TAG, "getSomeCategories: ");
        ArrayList<Record.Category> customCategories = new ArrayList<>();
        customCategories.add(new Record.Category(Record.Category.INCOME_TYPE, "Salary"));
        customCategories.add(new Record.Category(Record.Category.INCOME_TYPE, "Loans"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Car"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Groceries"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Eating out"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Phone and Internet"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Transport"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Entertainment"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Wardrobe"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Personal"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Drugs and Alcohol"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Household"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Electronics"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Rent"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Vacation"));
        customCategories.add(new Record.Category(Record.Category.EXPENSE_TYPE, "Fee"));
        return customCategories;
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
        logTransaction("saving account");
        Log.i(TAG, "endTransaction: ");
        TempData.setAccount(account);
    }

}
