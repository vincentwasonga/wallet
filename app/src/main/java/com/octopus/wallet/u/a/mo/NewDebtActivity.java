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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Debt;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.t.CalculatorActivity;
import com.octopus.wallet.u.a.t.DateTimeActivity;
import com.octopus.wallet.u.a.t.TransactionActivity;

import org.joda.time.DateTime;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yoctopus.cac.tx.Tx;

public class NewDebtActivity extends BActivity {
    public static final String DATA_LABEL = "debt";
    private static final int DATE_EFFECTED_REQUEST = 10;
    private static final int DATE_PAYBACK_REQUEST = 20;
    private static final int AMOUNT_REQUEST = 30;
    private static final int DEFAULT_TYPE = 0;
    private static final int LENT_TYPE = 1;
    private static final int BORROWED_TYPE = 2;
    @InjectView(R.id.lent_button)
    RadioButton lent;
    @InjectView(R.id.borrowed_button)
    RadioButton borrowed;
    @InjectView(R.id.name_edittext)
    EditText name;
    @InjectView(R.id.description_edittext)
    EditText description;
    @InjectView(R.id.amount_textview)
    TextView amount;
    @InjectView(R.id.date_effected_textview)
    TextView effected;
    @InjectView(R.id.date_payback_textview)
    TextView payback;
    @InjectView(R.id.done_button)
    Button save;
    Debt debt;
    private long date_effected, date_payback;
    private int type = DEFAULT_TYPE;
    private boolean updating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_debt);
        ButterKnife.inject(this);
        setHasBackButton(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDebt();
            }
        });
        lent.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            type = LENT_TYPE;
                        } else {
                            type = BORROWED_TYPE;
                        }
                    }
                });
        borrowed.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            type = BORROWED_TYPE;
                        } else {
                            type = LENT_TYPE;
                        }
                    }
                });
        effected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent(DateTimeActivity.class);
                intent.putExtra(DateTimeActivity.RETURN_TYPE,
                        DateTimeActivity.RETURN_BEFORE);
                startActivityForResult(intent,
                        DATE_EFFECTED_REQUEST);
            }
        });
        payback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent(DateTimeActivity.class);
                intent.putExtra(DateTimeActivity.RETURN_TYPE,
                        DateTimeActivity.RETURN_AFTER);
                startActivityForResult(intent,
                        DATE_PAYBACK_REQUEST);
            }
        });
        amount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = getIntent(CalculatorActivity.class);
                        if (updating) {
                            intent1.putExtra(CalculatorActivity.DATA_LABEL,
                                    debt.getAmount());
                        }
                        startActivityForResult(intent1, AMOUNT_REQUEST);
                    }
                });
        date_effected = DateTime.now().toDate().getTime();
        DateTime next = DateTime.now().plusMonths(1);
        date_payback = next.toDate().getTime();
        Intent intent = getIntent();
        if (intent.hasExtra(DATA_LABEL)) {
            updating = true;
            setTitle(R.string.update);
            debt = intent.getParcelableExtra(DATA_LABEL);
            name.setText(debt.getName());
            description.setText(debt.getDescription());
            amount.setText(String.valueOf(
                    Func.convertToCurrentCurrency(debt.getAmount())));
            if (debt.isLent()) {
                lent.setChecked(true);
                type = LENT_TYPE;
            } else if (debt.isBorrowed()) {
                borrowed.setChecked(true);
                type = BORROWED_TYPE;
            }
            date_effected = debt.getEffectedDate();
            date_payback = debt.getExpectedPayback();
            save.setText(R.string.update);
            save.setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getDatabase().update(debt);
                            finish();
                        }
                    });
        }
        effected.setText(Func.getDateddMMM(date_effected));
        payback.setText(Func.getDateddMMM(date_payback));
    }

    private void saveDebt() {
        if (type == DEFAULT_TYPE) {
            shakeView(R.id.type_group);
            return;
        }
        String cash = amount.getText().toString();
        if (TextUtils.isEmpty(cash)) {
            shakeView(amount);
            return;
        }
        String desc = description.getText().toString();
        if (TextUtils.isEmpty(desc)) {
            shakeView(description);
            return;
        }
        String nm = name.getText().toString();
        if (TextUtils.isEmpty(nm)) {
            shakeView(name);
            return;
        }

        debt = new Debt(type == LENT_TYPE ?
                Debt.LEND :
                Debt.BORROW,
                nm,
                desc,
                Func.convertToDefaultCurrency(
                        Integer.parseInt(cash)),
                date_effected);
        debt.setExpectedPayback(
                date_payback);
        Record.Category category = null;
        for (Record.Category category1 : getCategories()) {
            if (category1.getName().equalsIgnoreCase("Debts")) {
                category = category1;
            }
        }
        if (debt.isLent()) {
            if (category == null) {
                category = new Record.Category(Record.Category.EXPENSE_TYPE,
                        "Debts");
                getDatabase().save(category);
                saveRecord(category);
            } else {
                category.setType(Record.Category.EXPENSE_TYPE);
                saveRecord(category);
            }

        } else {
            if (category == null) {
                category = new Record.Category(Record.Category.INCOME_TYPE,
                        "Debts");
                getDatabase().save(category);
                saveRecord(category);
            } else {
                category.setType(Record.Category.INCOME_TYPE);
                saveRecord(category);
            }
        }
    }

    private void saveRecord(Record.Category category) {
        Record.DescriptionSet set = new Record.DescriptionSet();
        String text;
        if (category.isExpense()) {
            text = "Debt to " +
                    debt.getName() +
                    " for " +
                    debt.getDescription();
        } else {
            text = "Debt from " +
                    debt.getName() +
                    " for " +
                    debt.getDescription();
        }
        set.setDescription(text);
        final Record record = new Record(category,
                debt.getAmount(),
                set);
        record.setDate_millis(debt.getEffectedDate());
        Tx.OnComplete<Boolean> onComplete =
                new Tx.OnComplete<Boolean>() {
            @Override
            public void onComplete(int id, Boolean aBoolean) {
                getDatabase().save(debt);
                finish();
            }
        };
        if (record.isIncome()) {
            transact(new TransactionActivity.AddIncome(this,
                            100,
                            record),
                    onComplete);
        } else {
            transact(new TransactionActivity.AddExpense(this,
                            100,
                            record),
                    onComplete);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("debt", debt);
        outState.putLong("effected", date_effected);
        outState.putLong("payback", date_payback);
        outState.putBoolean("updating", updating);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        debt = savedInstanceState.getParcelable("debt");
        date_effected = savedInstanceState.getLong("effected");
        date_payback = savedInstanceState.getLong("payback");
        updating = savedInstanceState.getBoolean("updating");
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode,
                resultCode,
                data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DATE_EFFECTED_REQUEST: {
                    if (data.hasExtra(DateTimeActivity.RESULT_LABEL)) {
                        date_effected = data.getLongExtra(
                                DateTimeActivity.RESULT_LABEL,
                                new Date().getTime());
                        effected.setText(Func.getDateDayddMMM(date_effected));
                    }
                    break;
                }
                case DATE_PAYBACK_REQUEST: {
                    date_payback = data.getLongExtra(
                            DateTimeActivity.RESULT_LABEL,
                            new Date().getTime());
                    payback.setText(Func.getDateDayddMMM(date_payback));
                    break;
                }
                case AMOUNT_REQUEST: {
                    if (data.hasExtra(CalculatorActivity.RETURN_LABEL)) {
                        int cash = data.getIntExtra(
                                CalculatorActivity.RETURN_LABEL,
                                0);
                        amount.setText(String.valueOf(cash));
                    }
                    break;
                }
            }
        }
    }

}
