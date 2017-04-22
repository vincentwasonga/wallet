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

package com.octopus.wallet.m.b;

import android.content.Context;

import com.octopus.wallet.m.pb.Currency;

import me.yoctopus.cac.pref.InvalidPreference;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import me.yoctopus.cac.pref.Preferences;


public class CurrencyManager {
    private Preferences preferences;
    private static final String code_name = "currency_code";
    private static final String country_name = "currency_name";
    private static final String rate_name = "currency_name";

    public CurrencyManager(Context context) {
        preferences = new Preferences(context);
    }
    public void saveCurrency(Currency currency) {
        Pref<String> code = new Pref<>(code_name, currency.getCode()),
                name = new Pref<>(country_name, currency.getCountry());
        Pref<Float> rate = new Pref<>(rate_name, currency.getRate());
        try {
            preferences.savePreference(code);
            preferences.savePreference(name);
            preferences.savePreference(rate);
        } catch (InvalidPreference invalidPreference) {
            invalidPreference.printStackTrace();
        }
    }
    public Currency getCurrency() {
        Currency currency = Currency.DEFAULT_CURRENCY();
        Pref<String> code = new Pref<>(code_name, currency.getCode()),
                name = new Pref<>(country_name, currency.getCountry());
        Pref<Float> rate = new Pref<>(rate_name, currency.getRate());
        try {
            String code1 = preferences.getPreference(code);
            String name1 = preferences.getPreference(name);
            float rate1 = preferences.getPreference(rate);
            return new Currency(code1, name1, rate1);
        } catch (InvalidPreferenceType invalidPreferenceType) {
            invalidPreferenceType.printStackTrace();
            return currency;
        }
    }
}
