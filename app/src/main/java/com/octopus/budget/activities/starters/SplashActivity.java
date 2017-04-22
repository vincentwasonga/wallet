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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.octopus.budget.BudgetApp;
import com.octopus.budget.R;
import com.octopus.budget.models.pbmodels.TempData;
import com.octopus.budget.models.pbmodels.Transaction;
import com.octopus.budget.models.math.Func;
import com.octopus.budget.models.transactions.InitAppTransaction;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity implements Transaction.TransactionCompleteListener {
    private BudgetApp app;
    private TextView messageView;
    private Transaction tx, tx1;
    private SharedPreferences preferences;

    private ArrayList<String> welcomeMessages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        app = (BudgetApp) getApplication();
        getAccountData();


    }

    private void getAccountData() {
        tx = new InitAppTransaction(app.getPesaContext(), null);
        Log.i("App", "Account init");
        tx.setOnTransactionCompleteListener(this);
        tx.executeNow();
    }
    private static final int START_DELAY_MILLIS = 4000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */

    private final Handler startHandler = new Handler();

    private final Runnable startRunnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedStartLogin(int delayMillis) {
        startHandler.postDelayed(startRunnable, delayMillis);
    }

    @Override
    public synchronized void onTransactionComplete(int id, boolean success) {
        switch (id) {
            case Transaction.INIT : {
                if (success) {
                    messageView = (TextView) findViewById(R.id.messageView);

                    app.setAccount(TempData.getAccount());
                    welcomeMessages.add(" Personal Budget helps you not to overspend on your money");
                    welcomeMessages.add(" personal expenses have never been easy to manage");
                    welcomeMessages.add(" Always remember to save your expenses and incomes at the time you effect them");
                    welcomeMessages.add(" I hope you enjoy using this app");
                    welcomeMessages.add(" Using this app puts you a step ahead of ensuring that managing" +
                            "your expenses is a very efficient way for day to day best practices");
                    String message = welcomeMessages.get(Func.getRandom(welcomeMessages.size()));
                    messageView.setText(message);
                    delayedStartLogin(START_DELAY_MILLIS);

                }
                else {
                    app.setAccount(null);
                }
                break;
            }
        }
    }
}
