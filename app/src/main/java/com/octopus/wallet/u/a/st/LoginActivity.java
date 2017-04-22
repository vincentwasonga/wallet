/*
 * Copyright 2017, Peter Vincent
 * Licensed under the Apache License, Version 2.0, Vin Budget.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.octopus.wallet.u.a.st;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.m.MainActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yoctopus.cac.util.LogUtil;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends BActivity {
    private String TAG =
            LogUtil.makeTag(LoginActivity.class);
    public static final int SET_PIN =
            1;
    public static final int LOGIN =
            2;
    public static final int ENABLE_DISABLE_PIN =
            3;
    public static final String PIN_ACTION = "pin_action";
    private int action;
    private boolean saving = false;
    String pin1, pin2;
    private TextView pinText;
    private int pin;
    private ArrayList<Button> buttons =
            new ArrayList<>();
    private AccountManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        manager = new AccountManager(this);
        LogUtil.i(TAG,
                "onCreate: ");
        Intent intent = getIntent();
        if (intent.hasExtra(PIN_ACTION)) {
            action = intent.getIntExtra(PIN_ACTION,
                    LOGIN);
            if (action == SET_PIN ||
                    action == ENABLE_DISABLE_PIN) {
                setHasBackButton(true);
            }
        }

        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {

                        // Set up the login form.
                        setPinText((TextView) getView(
                                R.id.textviewPin));
                        pin = manager.getInfo().getPin();

                        switch (action) {
                            case LOGIN: {
                                setTitle("Log in");
                                break;
                            }
                            case SET_PIN: {
                                setTitle("Set Pin");
                                break;
                            }
                            case ENABLE_DISABLE_PIN: {
                                setTitle("Confirm Pin");
                                break;
                            }
                        }
                    }
                };
        getHandler().post(runnable);
    }




    private void attemptLogin(String pin) {
        LogUtil.i(TAG,
                "attemptLogin: " +
                        pin);

        // Store values at the time of the login attempt.

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (pin.isEmpty() &&
                !isPasswordValid(pin)) {
            focusView = getPinText();
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login add focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, add kick off a background task to
            // perform the user login attempt
            execute(pin);
        }
    }

    private boolean isPasswordValid(String password) {
        LogUtil.i(TAG,
                "isPasswordValid: ");
        return password.length() >= 4;
    }
    @OnClick({R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9,
            R.id.btnC,
            R.id.btn0,
            R.id.btnX})
    public void onViewClicked(View view) {
        String number;
        switch (view.getId()) {
            case R.id.btn0:
            case R.id.btn1:
            case R.id.btn2:
            case R.id.btn3:
            case R.id.btn4:
            case R.id.btn5:
            case R.id.btn6:
            case R.id.btn7:
            case R.id.btn8:
            case R.id.btn9:
                Button btn = (Button) view;
                LogUtil.i(TAG,
                        "onClick: " +
                                btn.getText());
                number = btn.getText().toString();
                if (getPinText().getText().toString()
                        .trim().length() < 4) {
                    getPinText().append(number);
                    if (getPinText().getText().toString()
                            .trim().length() == 4) {
                        attemptLogin(getPinText().getText()
                                .toString().trim());
                    }
                }
                break;
            case R.id.btnC:
                getPinText().setText(null);
                break;
            case R.id.btnX:
                if (!getPinText().getText()
                        .toString().isEmpty()) {
                    number = getPinText()
                            .getText().toString();
                    String new_number = number.substring(0,
                            number.length() - 1);
                    getPinText().setText(new_number);
                }
                break;
        }
    }
    public void execute(String pinn) {

        LogUtil.i(TAG,
                "execute: pin");
        int intpin = Integer.parseInt(pinn);
        switch (action) {
            case LOGIN: {
                if (intpin == pin) {
                    LogUtil.i(TAG,
                            "execute: login success");
                    notifyToast("Login success");
                    login();
                } else {
                    LogUtil.i(TAG,
                            "execute: login failed");
                    shakeView(getPinText());
                    notifyToast("Login failed");
                }
                break;
            }
            case SET_PIN: {
                if (saving) {
                    pin2 = pinn;
                } else {
                    pin1 = pinn;
                    saving = true;
                    getPinText().setText(
                            null);
                    setTitle("Confirm pin");
                    notifyToast("Confirm pin");
                    break;
                }
                if (pin1.equals(pin2)) {
                    savePin(pin1);
                } else {
                    notifyToast("Pin mismatch");
                }
                break;
            }
            case ENABLE_DISABLE_PIN: {
                pin = manager.getInfo().getPin();
                if (intpin == pin) {
                    LogUtil.i(TAG,
                            "execute: confirm success");
                    notifyToast("Confirmation success");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    LogUtil.i(TAG,
                            "execute: confirmation failed");
                    notifyToast("Confirmation failed");
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }

    }

    public void login() {
        startMainActivity();
        finish();
    }

    private void startMainActivity() {
        LogUtil.i(TAG,
                "startMainActivity: ");
        Intent intent = getIntent(MainActivity.class);
        startActivity(intent);
    }

    private void savePin(String pin) {
        if (pin.length() == 4) {
            int my_pin = Integer.parseInt(pin);
            manager.updatePin(my_pin);
            notifyToast("Pin update successfully");
            finish();
        } else {
            notifyToast("Pin must be a " +
                            "4 digit number");
        }
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



}

