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


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Currency;
import com.octopus.wallet.u.a.st.CurrencyActivity;
import com.octopus.wallet.u.a.st.LoginActivity;
import com.octopus.wallet.u.a.t.CategoryActivity;

import me.yoctopus.cac.notif.Notification;

public class SettingsActivity extends AppCompatPreferenceActivity {


    static final String LOGIN_KEY = "login";
    private final int LOGIN_CODE = 1;
    CheckBoxPreference loginPreference;
    boolean loginOn;
    private Notification notification;

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    Preference currencyPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        addPreferencesFromResource(R.xml.pref_account);
        notification = new Notification(this);
        Preference accountPreference = findPreference("account");
        Preference categoriesPreference = findPreference("categories");
        currencyPreference = findPreference("currency");
        loginPreference = (CheckBoxPreference) findPreference("login");
        loginOn = loginPreference.isChecked();
        loginPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(SettingsActivity.this,
                                LoginActivity.class);
                        intent.putExtra(LoginActivity.PIN_ACTION,
                                LoginActivity.ENABLE_DISABLE_PIN);
                        startActivityForResult(intent,
                                LOGIN_CODE);
                        return false;
                    }
                });
        accountPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(new Intent(SettingsActivity.this,
                                AccountSettingsActivity.class));
                        return false;
                    }
                });
        categoriesPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(SettingsActivity.this,
                                CategoryActivity.class);
                        intent.putExtra("editable",
                                true);
                        startActivity(intent);
                        return false;
                    }
                });

        currencyPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(SettingsActivity.this,
                                CurrencyActivity.class);
                        startActivity(intent);
                        return false;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Currency currency = Func.currentCurrency;
        currencyPreference.setSummary(currency
                .getCountry() +
                " " +
                currency.getCode());
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_CODE: {
                    loginPreference.setChecked(
                            !loginOn);
                }
            }
        }
        else if (resultCode == RESULT_CANCELED){
            switch (requestCode) {
                case LOGIN_CODE: {
                    loginPreference.setChecked(
                            loginOn);
                }
            }
        }
        super.onActivityResult(requestCode,
                resultCode,
                data);
    }

    @Override
    protected void onSaveInstanceState(
            Bundle outState) {
        outState.putInt(LOGIN_KEY,
                loginOn ?
                        1 :
                        0);
        super.onSaveInstanceState(
                outState);

    }

    @Override
    protected void onRestoreInstanceState(
            Bundle state) {
        int b = state.getInt(
                LOGIN_KEY);
        loginOn =
                b == 1;
        super.onRestoreInstanceState(
                state);

    }

    @Override
    public boolean onOptionsItemSelected(
            MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(
                item);
    }

    /**
     * Set up the {@link android.app.ActionBar},
     * if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(
                    true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

}
