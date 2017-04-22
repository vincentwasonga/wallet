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

package com.octopus.budget.activities.starters;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.octopus.budget.R;
import com.octopus.budget.models.pbmodels.Account;
import com.octopus.budget.models.pbmodels.TempData;
import com.octopus.budget.models.pbmodels.Transaction;
import com.octopus.budget.models.math.Func;
import com.octopus.budget.models.transactions.InitAppTransaction;

/**
 * Implementation of App Widget functionality.
 */
public class PersonalBudgetWidgetSmall extends AppWidgetProvider implements Transaction.TransactionCompleteListener{
    private Transaction tx;
    private static RemoteViews views;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.personal_budget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        tx = new InitAppTransaction(context, null);
        tx.setOnTransactionCompleteListener(this);
        tx.executeNow();

    }

    @Override
    public void onEnabled(Context context) {
        tx = new InitAppTransaction(context, null);
        tx.setOnTransactionCompleteListener(this);
        tx.executeNow();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onTransactionComplete(int id, boolean success) {
        switch (id) {
            case Transaction.INIT : {
                if (success) {
                    Account account = TempData.getAccount();
                    String bal = Func.getMoney(account.getInfo().getTotalBal());
                    views.setTextViewText(R.id.appwidget_text, bal);

                }
            }
        }
    }
}

