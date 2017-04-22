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

import com.octopus.budget.models.errors.InvalidExportChoice;
import com.octopus.budget.models.exportimport.exp.ExportChoice;
import com.octopus.budget.models.exportimport.exp.ReportExporter;
import com.octopus.budget.models.exportimport.data.ExportData;
import com.octopus.budget.models.pbmodels.DataBaseTransaction;

/**
 * Created by octopus on 8/14/16.
 */
public class SaveFileDataBaseTransaction extends DataBaseTransaction {
    private String TAG = "SaveFileDataBaseTransaction";
    private ExportData data;
    private ExportChoice choice;

    public SaveFileDataBaseTransaction(Context context,
                                       Context activityContext,
                                       ExportData data,
                                       ExportChoice choice) {
        super(context, activityContext, DataBaseTransaction.SAVE_FILE);
        Log.i(TAG, "SaveFileDataBaseTransaction: ");
        this.data = data;
        this.choice = choice;
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
        Log.i(TAG, "execute: ");
        ReportExporter exporter = new ReportExporter(getContext(), data, choice);
        try {
             exporter.export();
        } catch (InvalidExportChoice invalidExportChoice) {
            Log.e(TAG, "execute: ", invalidExportChoice );
            return false;
        }
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

    }







}
