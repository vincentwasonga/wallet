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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.octopus.budget.BudgetApp;
import com.octopus.budget.R;
import com.octopus.budget.models.pbmodels.Transaction;
import com.octopus.budget.models.notification.Notification;
import com.octopus.budget.models.transactions.SavePinTransaction;

public class WelcomeActivity extends AppCompatActivity implements Transaction.TransactionCompleteListener {
    private BudgetApp app;
    private Transaction tx;
    private SharedPreferences preferences;
    private ImageView splashid;
    private EditText pinEditText;
    private EditText pin1EditText;
    private Button button;
    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (BudgetApp) getApplication();
        notification = new Notification(this);
        initStartScreen();
    }

    private void initStartScreen() {
        preferences = app.getPreferences();
        if (!preferences.getBoolean("is_slide_launched", false)) {
            startSlider();
        } else {
            startNextActivity();
        }
    }

    private void startSlider() {
        setContentView(R.layout.activity_welcome);
        this.button = (Button) findViewById(R.id.button);
        this.pin1EditText = (EditText) findViewById(R.id.pin1_EditText);
        this.pinEditText = (EditText) findViewById(R.id.pin_EditText);
        this.splashid = (ImageView) findViewById(R.id.splash_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin1 = pin1EditText.getText() != null ?
                        pin1EditText.getText().toString() : " ";
                String pin2 = pinEditText.getText() != null ?
                        pinEditText.getText().toString() : " ";
                if (pin1.equals(pin2)) {
                    savePin(pin1);
                } else {
                    notification.setNotificationBundle("Pin mismatch");
                    notification.notify(Notification.TOAST);
                }

            }
        });
    }

    private void savePin(String pin) {
        if (pin.length() == 4) {
            int my_pin = Integer.parseInt(pin);
            tx = new SavePinTransaction(app.getPesaContext(), this, my_pin);
            tx.setOnTransactionCompleteListener(this);
            tx.executeNow();
        } else {
            notification.setNotificationBundle("Pin must be a 4 digit number");
            notification.notify(Notification.TOAST);
        }
    }
    private void startNextActivity() {
        startActivity(new Intent(this, SplashActivity.class));
        this.finish();
    }

    @Override
    public void onTransactionComplete(int id, boolean success) {
        if (success) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("is_slide_launched", true);
            edit.apply();
            startNextActivity();
        }
    }
}
