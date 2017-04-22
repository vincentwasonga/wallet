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

package com.octopus.wallet.u.a.mo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.octopus.wallet.R;
import com.octopus.wallet.m.a.DebtsAdapter;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Debt;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.v.w.PopupList;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.t.TransactionActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.tx.Tx;

public class DebtsActivity extends BActivity {
    private static final int UPDATE_DEBT = 0;
    private static final int DELETE_DEBT = 1;
    private static final int SETTLE_DEBT = 2;
    DebtsAdapter adapter;
    @InjectView(R.id.debt_list)
    RecyclerView debt_list;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debts);
        setHasBackButton(true);
        setTitle("Debts");
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
    }
    @OnClick(R.id.fab)
    public void onViewClicked() {
        Intent intent = getIntent(NewDebtActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        debt_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DebtsAdapter(getDatabase().getDebts(),
                new DebtsAdapter.Options() {
                    @Override
                    public void onClick(Debt debt,
                                        View view) {
                        actOnDebt(debt,
                                view);
                    }

                    @Override
                    public void onSettle(Debt debt) {
                        addPayback(debt);
                    }
                });
        debt_list.setAdapter(adapter);
    }

    private void actOnDebt(final Debt debt, View view) {
        PopupList popupList = new PopupList(this,
                view);
        popupList.setOnItemClickListener(
                new PopupList.OnItemClickListener() {
                    @Override
                    public boolean onItemClick(int itemId) {
                        doChosenOption(debt,
                                itemId);
                        return false;
                    }
                });
        popupList.addItem(UPDATE_DEBT,
                "Update ");
        popupList.addItem(DELETE_DEBT,
                "Delete ");
        if (debt.isFinalized()) {
            popupList.addItem(SETTLE_DEBT,
                    "Settle ");
        }
        popupList.show();
    }

    private void addPayback(final Debt debt) {
        notifyDialog("Enter the amount payed back in figures",
                null,
                new NDialog.DButton("OK",
                        null),
                null,
                new NDialog.OnAnswer() {
                    @Override
                    public void onAnswer(String answer) {
                        if (answer.isEmpty()) {
                            notifyToast("Empty response");
                            return;
                        }
                        int cash;
                        try {
                            cash = Integer.parseInt(answer);
                            if (debt.isLent()) {

                            }
                        } catch (NumberFormatException e) {
                            notifyToast("Unexpected number");
                            return;
                        }
                        int total = cash + debt.getPayedBack();
                        if (total > debt.getAmount()) {
                            notifyToast("Unexpected amount payed back");
                            return;
                        }
                        debt.setPayedBack(Func.convertToDefaultCurrency(total));
                        rollOutDebt(debt);
                    }
                });
    }

    private void rollOutDebt(Debt debt) {
        getDatabase().update(debt);
        adapter.updateAll(getDatabase().getDebts());
    }

    private void doChosenOption(final Debt debt,
                                int itemId) {
        switch (itemId) {
            case UPDATE_DEBT: {
                Intent intent = getIntent(
                        NewDebtActivity.class);
                intent.putExtra(NewDebtActivity.DATA_LABEL,
                        debt);
                startActivity(intent);
                break;
            }
            case DELETE_DEBT: {
                notifyDialog("Warning",
                        "Please confirm to delete this debt",
                        new NDialog.DButton("Confirm",
                                new NDialog.DButton.BListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteDebt(debt);
                                    }
                                }),
                        null);
                break;
            }
            case SETTLE_DEBT: {
                notifyDialog("Info",
                        "When you settle a debt, it means its payed back add you are " +
                                "ready to add it to your records",
                        new NDialog.DButton("Settle",
                                new NDialog.DButton.BListener() {
                                    @Override
                                    public void onClick(View v) {
                                        settleDebt(debt);
                                    }
                                }),
                        new NDialog.DButton("Cancel",
                                new NDialog.DButton.BListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }));
            }
        }
    }

    private void settleDebt(final Debt debt) {
        Record.Category category = null;
        for (Record.Category category1 : getCategories()) {
            if (category1.getName().equalsIgnoreCase("Debts")) {
                category = category1;
            }
        }
        assert category != null;
        if (debt.isLent()) {
            category.setType(Record.Category.EXPENSE_TYPE);
        } else {
            category.setType(Record.Category.INCOME_TYPE);
        }
        Record.DescriptionSet set = new Record.DescriptionSet();
        set.setDescription(debt.getDescription());
        Record record = new Record(category,
                debt.getAmount(),
                set);
        if (category.isIncome()) {
            transact(
                    new TransactionActivity.AddIncome(DebtsActivity.this,
                            100, record),
                    new Tx.OnComplete<Boolean>() {
                        @Override
                        public void onComplete(int id,
                                               Boolean success) {
                            deleteDebt(debt);
                        }
                    });
        } else {
            transact(
                    new TransactionActivity.AddExpense(DebtsActivity.this,
                            100, record),
                    new Tx.OnComplete<Boolean>() {
                        @Override
                        public void onComplete(int id,
                                               Boolean success) {
                            deleteDebt(debt);
                        }
                    });
        }

    }

    private void deleteDebt(Debt debt) {
        getDatabase().delete(debt);
        adapter.updateAll(getDatabase().getDebts());
    }
}
