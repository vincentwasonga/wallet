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

package com.octopus.wallet.u.a.s;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.h.utl.Func;
import me.yoctopus.cac.pref.InvalidPreference;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.st.LoginActivity;
import com.octopus.wallet.u.a.t.CalculatorActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.util.LogUtil;

public class AccountSettingsActivity extends BActivity {
    private static final int AMOUNT_LIMIT_REQUEST = 1;
    @InjectView(R.id.name_Edit)
    EditText nameEdit;
    @InjectView(R.id.daily_limit_Edit)
    TextView dailyLimitEdit;
    @InjectView(R.id.email_Edit)
    EditText emailEdit;
    @InjectView(R.id.password_Edit)
    EditText passwordEdit;
    private String TAG =
            LogUtil.makeTag(AccountSettingsActivity.class);
    private AccountManager manager;

    @OnClick({R.id.pinButton,
            R.id.save_Btn,
            R.id.deleteBtn,
    R.id.daily_limit_Edit})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.pinButton:
                intent = getIntent(
                        LoginActivity.class);
                intent.putExtra("pin_action",
                        LoginActivity.SET_PIN);
                startActivity(intent);
                break;
            case R.id.save_Btn:
                doButtonAction();
                break;
            case R.id.deleteBtn:
                deleteAction(view);
                break;
            case R.id.daily_limit_Edit:
                AccountManager.Info info = manager.getInfo();
                intent = getIntent(CalculatorActivity.class);
                intent.putExtra(CalculatorActivity.DATA_LABEL,
                        info.getDailyLimit());
                startActivityForResult(intent,
                        AMOUNT_LIMIT_REQUEST);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        ButterKnife.inject(this);
        manager = new AccountManager(this);
        setHasBackButton(true);
        setTitle("Account Settings");
        nameEdit.setText(manager.getInfo().getName());
        showCurrentSettings();
    }

    private void showCurrentSettings() {
        LogUtil.i(TAG,
                "showCurrentSettings: ");
        String email, pass;
        try {
            email = getPreferences().getPreference(new Pref<>("acc_email", ""));
            pass = getPreferences().getPreference(new Pref<>("acc_password", ""));
            emailEdit.setText(email);
            passwordEdit.setText(pass);
        } catch (InvalidPreferenceType e) {
            e.printStackTrace();
        }
    }

    private void doButtonAction() {
        LogUtil.i(TAG,
                "doButtonAction: ");
        if (!nameEdit.getText()
                .toString().isEmpty()) {
            manager.updateName(nameEdit.getText().toString());
        }
        if (!dailyLimitEdit.getText()
                .toString().isEmpty()) {
            int cash = Integer.parseInt(
                    dailyLimitEdit.getText().toString()
            );
            manager.updateLimit(
                    Func.convertToDefaultCurrency(cash));
        }
        if (!emailEdit.getText()
                .toString().isEmpty()) {
            Pref<String> preference = new Pref<>(
                    "acc_email",
                    emailEdit.getText().toString());
            try {
                getPreferences().savePreference(preference);

            } catch (InvalidPreference invalidPreference) {
                invalidPreference.printStackTrace();
            }
        }
        if (!passwordEdit.getText()
                .toString().isEmpty()) {
            Pref<String> preference1 = new Pref<>(
                    "acc_password",
                    passwordEdit.getText().toString());
            try {
                getPreferences().savePreference(preference1);
            } catch (InvalidPreference invalidPreference) {
                invalidPreference.printStackTrace();
            }
        }
        notifyNotificationBar("Account update success");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AMOUNT_LIMIT_REQUEST: {
                    if (data.hasExtra(CalculatorActivity.RETURN_LABEL)) {
                        dailyLimitEdit.setText(String.valueOf(
                                data.getIntExtra(CalculatorActivity.RETURN_LABEL,
                                        0)
                        ));
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteAction(View view) {
        LogUtil.i(TAG,
                "deleteAction: ");
        notifyDialog("Account Refresh ",
                "Are you sure you want to refresh this account?",
                new NDialog.DButton("Yes",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                deleteAll();
                            }
                        }),
                new NDialog.DButton("No",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }));
    }

    public void deleteAll() {
        LogUtil.i(TAG,
                "deleteAll: ");
        getDatabase().refreshData();
        getPreferences().clearAll();
        notifyNotificationBar("Account refresh success");
        finish();
    }
}
