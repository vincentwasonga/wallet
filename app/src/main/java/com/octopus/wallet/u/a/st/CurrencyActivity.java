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

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.octopus.wallet.R;
import com.octopus.wallet.m.a.CurrencyAdapter;
import com.octopus.wallet.m.b.CurrencyManager;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Currency;
import com.octopus.wallet.u.a.BActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yoctopus.cac.notif.NDialog;

public class CurrencyActivity extends BActivity {
    @InjectView(R.id.currency_list)
    RecyclerView recyclerView;
    @InjectView(R.id.add_button)
    Button add;
    CurrencyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_currency);
        ButterKnife.inject(this);
        setHasBackButton(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CurrencyAdapter(getDatabase().getCurrencies(),
                new CurrencyAdapter.Listener() {
                    @Override
                    public void onClicked(Currency currency) {
                        setDefaultCurrency(currency);
                    }
                });
        recyclerView.setAdapter(adapter);
    }

    private void setDefaultCurrency(Currency currency) {
        new CurrencyManager(this).saveCurrency(currency);
        Func.init(currency);
    }

    private void addCurrency() {
        notifyDialog("Enter new Currency",
                "Use format / code, country, rate in reference to KES",
                new NDialog.DButton("Save",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }),
                null,
                new NDialog.OnAnswer() {
                    @Override
                    public void onAnswer(String answer) {
                        String[] parts = answer.split(",");
                        String code = parts[0];
                        String country = parts[1];
                        float rate = Float.parseFloat(parts[2]);
                        Currency currency = new Currency(code,
                                country,
                                rate);
                        getDatabase().save(currency);
                        adapter.add(currency);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
