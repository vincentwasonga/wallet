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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.octopus.budget.BudgetApp;
import com.octopus.budget.R;
import com.octopus.budget.activities.main.Main2Activity;
import com.octopus.budget.models.pbmodels.TempData;
import com.octopus.budget.models.notification.Notification;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //user account
    private BudgetApp app;
    private Notification notification;
    //textview holding the pin
    private TextView pinText;
    //buttons for entering the pin
    private int pin;
    private ArrayList<Button> buttons = new ArrayList<>();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        setPinText((TextView) findViewById(R.id.textviewPin));
        setApp((BudgetApp) getApplication());

        setNotification(new Notification(this));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        pin = getApp().getAccount().getInfo().getPin();
        configureButtons();

    }

    private void configureButtons() {
        getButtons().add((Button) findViewById(R.id.btn0));
        getButtons().add((Button) findViewById(R.id.btn1));
        getButtons().add((Button) findViewById(R.id.btn2));
        getButtons().add((Button) findViewById(R.id.btn3));
        getButtons().add((Button) findViewById(R.id.btn4));
        getButtons().add((Button) findViewById(R.id.btn5));
        getButtons().add((Button) findViewById(R.id.btn6));
        getButtons().add((Button) findViewById(R.id.btn7));
        getButtons().add((Button) findViewById(R.id.btn8));
        getButtons().add((Button) findViewById(R.id.btn9));
        getButtons().add((Button) findViewById(R.id.btnx));
        getButtons().add((Button) findViewById(R.id.btnC));
        for (int i = 0; i < getButtons().size(); i++) {
            getButtons().get(i).setOnClickListener(this);
        }

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(String pin) {


        // Store values at the time of the login attempt.

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (pin.isEmpty() && !isPasswordValid(pin)) {
            focusView = getPinText();
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt
            execute(pin);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        final int id = btn.getId();
        switch (id) {
            case R.id.btn0:
            case R.id.btn1:
            case R.id.btn2:
            case R.id.btn3:
            case R.id.btn4:
            case R.id.btn5:
            case R.id.btn6:
            case R.id.btn7:
            case R.id.btn8:
            case R.id.btn9: {
                String number = btn.getText().toString();
                if (getPinText().getText().toString().trim().length() < 4) {
                    getPinText().append(number);
                    if (getPinText().getText().toString().trim().length() == 4) {
                        attemptLogin(getPinText().getText().toString().trim());
                    }
                }

                break;
            }
            case R.id.btnx: {
                if (!getPinText().getText().toString().isEmpty()) {
                    String number = getPinText().getText().toString();
                    String newnumber = number.substring(0, number.length() - 1);
                    getPinText().setText(newnumber);
                }
                break;
            }
            case R.id.btnC: {
                getPinText().setText(null);

                break;
            }


        }
    }

    //initiate the login attempt
    public void execute(String pinn) {
        int intpin = Integer.parseInt(pinn);
        if (intpin == pin) {
            getNotification().setNotificationBundle("Success", "Login success");
            getNotification().notify(Notification.TOAST);
            login();
        } else {
            getNotification().setNotificationBundle("Failed", "Login failed");
            getNotification().notify(Notification.TOAST);
        }
    }

    public void login() {
        StartMainActivity();
        finish();
    }

    private void StartMainActivity() {
        Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
        TempData.setAccount(getApp().getAccount());
        startActivity(intent);
    }


    public TextView getPinText() {
        return pinText;
    }

    public void setPinText(TextView pinText) {
        this.pinText = pinText;
    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public void setButtons(ArrayList<Button> buttons) {
        this.buttons = buttons;
    }


    public BudgetApp getApp() {
        return app;
    }

    public void setApp(BudgetApp app) {
        this.app = app;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

}

