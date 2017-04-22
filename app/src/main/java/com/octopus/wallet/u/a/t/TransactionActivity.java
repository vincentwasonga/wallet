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

package com.octopus.wallet.u.a.t;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.e.AmountNotAccepted;
import com.octopus.wallet.m.e.BudgetException;
import com.octopus.wallet.m.e.InsufficientBalance;
import com.octopus.wallet.m.e.NullAmount;
import com.octopus.wallet.m.e.NullChoice;
import com.octopus.wallet.m.e.NullItem;
import com.octopus.wallet.m.e.OverflowDailyExpense;
import com.octopus.wallet.m.h.UpdatePackage;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.tx.DBTrans;
import com.octopus.wallet.u.a.BActivity;

import java.util.ArrayList;
import java.util.Date;

import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.tx.Tx;
import me.yoctopus.cac.util.LogUtil;

public class TransactionActivity extends BActivity implements
        View.OnClickListener {
    private final int categoryRequestCode = 1;
    private final int dateRequestCode = 3;
    private final int descriptionRequest = 2;
    private final int NO_CHOICE = 0;
    private final int INCOME_CHOICE = 2;
    private final int EXPENSE_CHOICE = 3;
    private String TAG =
            LogUtil.makeTag(
                    TransactionActivity.class);
    private int userChoice = NO_CHOICE;
    private Button categoryButton;
    private Record.Category defaultCategory =
            null;
    private Record.Category category =
            defaultCategory;
    private TextView currencyTextView;
    private ArrayList<Button> buttons;
    private ImageButton tickButton;
    private Button incomeButton,
            expenseButton;
    private ImageButton backButton;
    private TextView titleText;
    private ImageView shareButton;
    private ImageView deleteButton;
    private ImageButton addButton;
    private ImageButton minusButton;
    private ImageButton multiplyButton;
    private ImageButton divideButton;
    private ImageButton equalButton;
    private Record.DescriptionSet descriptionSet;
    private Button timeButton;
    private UpdatePackage updatePackage;
    private boolean updating =
            false;
    private Button descriptionButton;
    private Date date =
            new Date();
    private Calculator calculator;
    private int white =
            0xFFFFFFFF;
    private int green_light =
            0xFF4DB6AC;
    private AccountManager manager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG,
                "onCreate: ");
        setContentView(
                R.layout.activity_transaction);
        manager = new AccountManager(this);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("choice", userChoice);
        outState.putBoolean("update", updating);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        userChoice = savedInstanceState.getInt("choice");
        updating = savedInstanceState.getBoolean("update");
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (titleText != null) {
            titleText.setText(
                    title);
        }
    }

    private void saveUserChoiceState(int id) {
        switch (id) {
            case INCOME_CHOICE: {
                incomeButton.setTextColor(
                        white);
                expenseButton.setTextColor(
                        green_light);
                LogUtil.i(TAG,
                        "saveUserChoiceState: " +
                                "income");
                userChoice = INCOME_CHOICE;
                break;
            }
            case EXPENSE_CHOICE: {
                incomeButton.setTextColor(
                        green_light);
                expenseButton.setTextColor(
                        white);
                LogUtil.i(TAG,
                        "saveUserChoiceState: " +
                                "expense");
                userChoice = EXPENSE_CHOICE;
                break;
            }
        }
    }

    private void openCategoryIncomes() {
        incomeButton.setTextColor(
                white);
        expenseButton.setTextColor(
                green_light);
        LogUtil.i(TAG,
                "saveUserChoiceState: " +
                        "income");
        userChoice = INCOME_CHOICE;
        Intent intent = getIntent(
                CategoryActivity.class);
        intent.putExtra("type",
                "income");
        LogUtil.i(TAG,
                "run: starting activity " +
                        "Category");
        startActivityForResult(
                intent,
                categoryRequestCode);
    }

    private void openCategoryExpenses() {
        incomeButton.setTextColor(
                green_light);
        expenseButton.setTextColor(
                white);
        LogUtil.i(TAG,
                "saveUserChoiceState: " +
                        "expense");
        userChoice = EXPENSE_CHOICE;
        Intent intent = getIntent(
                CategoryActivity.class);
        intent.putExtra("type",
                "expense");
        LogUtil.i(TAG,
                "run: starting activity " +
                        "Category");
        startActivityForResult(intent,
                categoryRequestCode);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Runnable postCreate =
                new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i(TAG,
                                "onPostCreate: ");
                        descriptionButton = (Button) getView(
                                R.id.descriptionBtn);
                        backButton = (ImageButton) getView(
                                R.id.backButton);
                        backButton.setOnClickListener(
                                TransactionActivity.this);
                        titleText = (TextView) getView(
                                R.id.titleText);
                        shareButton = (ImageView) getView(
                                R.id.share);
                        shareButton.setOnClickListener(
                                TransactionActivity.this);
                        deleteButton = (ImageView) getView(
                                R.id.delete);
                        deleteButton.setOnClickListener(
                                TransactionActivity.this);
                        addButton = (ImageButton) getView(
                                R.id.add_btn);
                        addButton.setOnClickListener(
                                TransactionActivity.this);
                        minusButton = (ImageButton) getView(
                                R.id.minus_btn);
                        minusButton.setOnClickListener(
                                TransactionActivity.this);
                        multiplyButton = (ImageButton) getView(
                                R.id.times_btn);
                        multiplyButton.setOnClickListener(
                                TransactionActivity.this);
                        divideButton = (ImageButton) getView(
                                R.id.divide_btn);
                        divideButton.setOnClickListener(
                                TransactionActivity.this);
                        equalButton = (ImageButton) getView(
                                R.id.equal_btn);
                        equalButton.setOnClickListener(
                                TransactionActivity.this);
                        incomeButton = (Button) getView(
                                R.id.incomeButton);
                        incomeButton.setOnClickListener(
                                TransactionActivity.this);
                        expenseButton = (Button) getView(
                                R.id.expenseButton);
                        expenseButton.setOnClickListener(
                                TransactionActivity.this);
                        descriptionSet =
                                new Record.DescriptionSet();
                        categoryButton = (Button) getView(
                                R.id.itemButton);
                        categoryButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        chooseCategory(view);
                                    }
                                });
                        currencyTextView = (TextView) getView(
                                R.id.currencyText);
                        currencyTextView.setText(
                                Func.currentCurrency.getCode());
                        incomeButton = (Button) getView(
                                R.id.incomeButton);
                        expenseButton = (Button) getView(
                                R.id.expenseButton);
                        timeButton = (Button) getView(
                                R.id.textClock);
                        timeButton.setText(R.string.now);
                        buttons = new ArrayList<>();
                        buttons.add((Button) getView(
                                R.id.btn0));
                        buttons.add((Button) getView(
                                R.id.btn1));
                        buttons.add((Button) getView(
                                R.id.btn2));
                        buttons.add((Button) getView(
                                R.id.btn3));
                        buttons.add((Button) getView(
                                R.id.btn4));
                        buttons.add((Button) getView(
                                R.id.btn5));
                        buttons.add((Button) getView(
                                R.id.btn6));
                        buttons.add((Button) getView(
                                R.id.btn7));
                        buttons.add((Button) getView(
                                R.id.btn8));
                        buttons.add((Button) getView(
                                R.id.btn9));
                        getView(R.id.btnx).setOnClickListener(
                                TransactionActivity.this);
                        getView(R.id.btnx).setOnLongClickListener(
                                new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        calculator.deleteAll();
                                        return true;
                                    }
                                });
                        tickButton = (ImageButton) getView(
                                R.id.btndone);
                        setTitle("New Record");
                        calculator =
                                new Calculator((TextView)
                                        getView(R.id.AmountText)
                                );
                        Intent intent = getIntent();
                        if (intent.hasExtra("record")) {
                            Record record = intent.getParcelableExtra(
                                    "record");
                            updatePackage =
                                    new UpdatePackage(
                                            record);
                            categoryButton.setText(
                                    record.getName());
                            category = record.getCategory();
                            descriptionSet =
                                    record.getDescriptionSet();
                            setAmount(String.valueOf(
                                    record.getAmount()));
                            timeButton.setText(
                                    Func.getDateMPassed(
                                            record.getDate_millis()));
                            if (record.getCategory().isIncome()) {
                                saveUserChoiceState(
                                        INCOME_CHOICE);
                            } else {
                                saveUserChoiceState(
                                        EXPENSE_CHOICE);
                            }
                            setTitle("Update Record");
                            shareButton.setVisibility(
                                    View.VISIBLE);
                            deleteButton.setVisibility(
                                    View.VISIBLE);
                            updating = true;
                        }
                        configureDescriptionDialog();
                        configureKeypad();
                        configureClock();
                    }
                };
        getHandler().post(postCreate);
    }

    private void configureClock() {
        LogUtil.i(TAG,
                "configureClock: ");
        timeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = getIntent(
                                DateTimeActivity.class);
                        intent.putExtra(DateTimeActivity.RETURN_TYPE,
                                DateTimeActivity.RETURN_BEFORE);
                        if (updating) {
                            intent.putExtra(DateTimeActivity.DATA_LABEL,
                                    updatePackage.getRecord()
                                            .getDate_millis());
                        }
                        startActivityForResult(intent,
                                dateRequestCode);
                    }
                });

    }

    private void configureDescriptionDialog() {
        LogUtil.i(TAG,
                "configureDescriptionDialog: ");
        descriptionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startDescriptionActivity();
                    }
                });
    }

    private void startDescriptionActivity() {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i(TAG,
                                "startDescriptionActivity: ");
                        Intent intent = getIntent(
                                DescriptionActivity.class);
                        String number =
                                calculator.getCurrentExpression();
                        if (!number.isEmpty()) {
                            int amount = Integer.parseInt(
                                    number);
                            intent.putExtra("Amount",
                                    amount);
                        }
                        if (updating) {
                            intent.putExtra("description",
                                    descriptionSet);
                        }
                        startActivityForResult(intent,
                                descriptionRequest);
                    }
                };
        getHandler().postDelayed(
                runnable,
                200);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        LogUtil.i(TAG,
                "onActivityResult: ");
        super.onActivityResult(requestCode,
                resultCode,
                data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case descriptionRequest: {
                    if (data.hasExtra("Description")) {
                        LogUtil.i(TAG,
                                "onActivityResult: " +
                                        descriptionRequest);
                        Record.DescriptionSet newSet =
                                data.getParcelableExtra(
                                        "Description");
                        descriptionSet.setDescription(
                                newSet.getDescription());
                        descriptionSet.setLocationName(
                                newSet.getLocationName());
                        descriptionSet.setReceiptName(
                                newSet.getReceiptName());
                        if (updating) {
                            updatePackage.setDescription(
                                    descriptionSet);
                        }
                    }
                    break;
                }
                case categoryRequestCode: {
                    if (data.hasExtra("category")) {
                        LogUtil.i(TAG,
                                "onActivityResult: " +
                                        categoryRequestCode);
                        int id = data.getIntExtra(
                                "category",
                                0);
                        SList<Record.Category> list =
                                new SList<>(getCategories());
                        category = list.getWithID(id);
                        assert category != null;
                        categoryButton.setText(category.getName());
                        if (updating) {
                            updatePackage.setCategory(
                                    category);
                        }
                    }
                    break;
                }
                case dateRequestCode: {
                    if (data.hasExtra(DateTimeActivity.RESULT_LABEL)) {
                        long time = data.getLongExtra(
                                DateTimeActivity.RESULT_LABEL,
                                Record.DEFAULT_MILLIS);
                        Date date = Func.getDate(
                                time);
                        if (updating) {
                            updatePackage.setDateMillis(
                                    time);
                        } else {
                            this.date = date;
                        }
                        LogUtil.i(TAG,
                                "onActivityResult: new time " +
                                        date.toString());
                        timeButton.setText(
                                Func.getDateMPassed(
                                        date));
                    }
                    break;
                }
            }
        }
    }

    private void shareRecord() {
        notifyDialog("Share",
                "Please confirm to share " +
                        "this record information",
                new NDialog.DButton("Confirm",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                doConfirmedShare();
                            }
                        }),
                null);
    }

    private void doConfirmedShare() {
        Intent intent = new Intent(
                Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                getShareMessage());
        startActivity(intent);
    }

    private String getShareMessage() {
        assert updatePackage != null;
        String type = updatePackage.getRecord().getTypeName();
        return "Tx:" +
                "\t" +
                type +
                "\n" +
                "Record: " +
                "\t " +
                updatePackage.getRecord().getCategory().getName() +
                "\n" +
                "Amount: " +
                "\t" +
                updatePackage.getRecord().getAmount() +
                "\nT" +
                "ime add Date:" +
                "\t" +
                Func.getDateMPassed(
                        updatePackage.getOld()
                                .getDate_millis());
    }

    private void configureKeypad() {
        LogUtil.i(TAG,
                "configureKeypad: ");
        incomeButton.setOnClickListener(
                this);
        expenseButton.setOnClickListener(
                this);
        tickButton.setOnClickListener(
                this);
        deleteButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setOnClickListener(
                    this);
        }
    }

    private void chooseCategory(View view) {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i(TAG,
                                "chooseCategory: ");

                        if (userChoice == NO_CHOICE) {
                            notifyToast("Choose income or" +
                                            " expense add try again");
                            notifyVibration();
                            shakeView(
                                    incomeButton);
                            shakeView(
                                    expenseButton);
                            return;
                        }
                        Intent intent = getIntent(
                                CategoryActivity.class);
                        if (userChoice ==
                                INCOME_CHOICE) {
                            intent.putExtra("type",
                                    "income");
                        } else if (userChoice ==
                                EXPENSE_CHOICE) {
                            intent.putExtra("type",
                                    "expense");
                        }
                        startActivityForResult(intent,
                                categoryRequestCode);
                    }
                };
        getHandler().postDelayed(
                runnable,
                200);

    }

    private void setAmount(String value) {
        calculator.process(
                value);
    }

    @Override
    public void onClick(final View view) {
        Runnable click =
                new Runnable() {
                    @Override
                    public void run() {
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
                            case R.id.btn9: {
                                Button b =
                                        (Button) view;
                                LogUtil.d(TAG,
                                        "onClick: btn " +
                                                b.getText().toString());
                                processDigit(
                                        Integer.valueOf(
                                                b.getText().toString()));
                                break;
                            }
                            case R.id.add_btn: {
                                LogUtil.d(TAG,
                                        "onClick: btn add");
                                processSign(
                                        Calculator.ADD);
                                break;
                            }
                            case R.id.minus_btn: {
                                LogUtil.d(TAG,
                                        "onClick: btn minus");
                                processSign(
                                        Calculator.MINUS);
                                break;
                            }
                            case R.id.times_btn: {
                                LogUtil.d(TAG,
                                        "onClick: btn multiply");
                                processSign(
                                        Calculator.MULTIPLY);
                                break;
                            }
                            case R.id.divide_btn: {
                                processSign(
                                        Calculator.DIVIDE);
                                break;
                            }
                            case R.id.equal_btn: {
                                LogUtil.d(TAG,
                                        "onClick: btn equal");
                                processSign(
                                        Calculator.EQUAL);
                                break;
                            }
                            case R.id.btnx: {
                                calculator.backSpace();
                                break;
                            }
                            case R.id.btndone: {
                                LogUtil.i(TAG,
                                        "onClick: btndone");
                                doMainAction();
                                break;
                            }
                            case R.id.incomeButton: {
                                openCategoryIncomes();
                                break;
                            }
                            case R.id.expenseButton: {
                                openCategoryExpenses();
                                break;
                            }
                            case R.id.share: {
                                shareRecord();
                                break;
                            }
                            case R.id.delete: {
                                deleteRecord(
                                        updatePackage.getRecord());
                                break;
                            }
                            case R.id.backButton: {
                                finish();
                                break;
                            }
                        }
                    }
                };
        getHandler().post(click);
    }

    private void doMainAction() {
        if (calculator.hasIncompleteEExpression()) {
            notifyToast(" kindly complete " +
                            "expression first");
            return;
        }
        try {
            if (!updating) {
                if (checkInputs()) {
                    String number = calculator.getCurrentExpression();
                    if (number.length() <= 0 ||
                            number.isEmpty()) {
                        LogUtil.i(TAG,
                                "onClick: throwing error");
                        throw new AmountNotAccepted(
                                "Amount cannot be saved");
                    }
                    calculator.process(
                            number);
                    int cash = calculator.getValue();
                    cash = Func.convertToDefaultCurrency(cash);
                    number = String.valueOf(cash);
                    if (userChoice == EXPENSE_CHOICE) {
                        int dailySpent = manager
                                .getInfo()
                                .getDailySpent();
                        int dailyLimit = manager
                                .getInfo()
                                .getDailyLimit();
                        int totalBal = manager
                                .getInfo()
                                .getTotalBal();
                        if (cash > totalBal) {
                            LogUtil.i(TAG,
                                    "onClick:" +
                                            " insufficient" +
                                            " balance");
                            throw new InsufficientBalance(
                                    "You do not have" +
                                            " enough balance " +
                                            "to transact ");
                        } else if (cash +
                                dailySpent >=
                                dailyLimit) {
                            LogUtil.i(TAG,
                                    "onClick: " +
                                            "insufficient" +
                                            " money");
                            throw new OverflowDailyExpense(
                                    "You are about to " +
                                            "overspend");
                        }
                    }
                    transact(category,
                            number,
                            descriptionSet);
                }
            }
            if (updating) {
                String amount =
                        calculator.getCurrentExpression();
                calculator.process(
                        amount);
                updatePackage.setAmount(
                        Func.convertToDefaultCurrency(
                                Integer.parseInt(amount)));
                updatePackage.setCategory(category);
                upDateRecord(updatePackage);
            }
        } catch (NullItem |
                NullAmount |
                NullChoice |
                AmountNotAccepted e) {
            notifyError(e);
            notifyVibration();
        } catch (OverflowDailyExpense e) {
            String amount = calculator.getCurrentExpression();
            calculator.process(amount);
            notifyExcessSpending(category,
                    String.valueOf(calculator.getValue()),
                    descriptionSet,
                    e);
            notifyVibration();
        } catch (InsufficientBalance e) {
            notifyInsufficientBalance(e);
            notifyVibration();
        }
    }

    private void processDigit(int digit) {
        calculator.addDigit(digit);
    }

    private void processSign(char sign) {
        calculator.addSign(sign);
    }


    private void notifyExcessSpending(final Record.Category item,
                                      final String amount,
                                      Record.DescriptionSet descriptionSet,
                                      OverflowDailyExpense e) {

        final Record.DescriptionSet descriptionSet1 =
                descriptionSet;
        notifyDialog("Excess Spending",
                e.getMessage(),
                new NDialog.DButton(
                        "Ignore",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                transact(item,
                                        amount,
                                        descriptionSet1);
                            }
                        }),
                null);
    }

    private void notifyInsufficientBalance(InsufficientBalance e) {
        notifyToast(e.getMessage());
    }

    private boolean transact(Record.Category item,
                             String amount,
                             Record.DescriptionSet descriptionSet) {
        if (calculator.hasIncompleteEExpression() ||
                calculator.hasCompleteExpression()) {
            notifyDialog("Error",
                    "Kindly evaluate the expression first");
            return false;
        }
        int cash = Integer.valueOf(
                amount);
        final Record r = new Record(item,
                cash,
                descriptionSet);
        r.setDate_millis(date.getTime());
        Tx.OnComplete<Boolean> onComplete =
                new Tx.OnComplete<Boolean>() {
            @Override
            public void onComplete(int id, Boolean aBoolean) {
                finish();
            }
        };
        switch (userChoice) {
            case INCOME_CHOICE: {
                LogUtil.i(TAG,
                        "transact: adding income");
                transact(new AddIncome(this,
                                100,
                                r),
                        onComplete);
                break;
            }
            case EXPENSE_CHOICE: {
                LogUtil.i(TAG,
                        "transact: adding expense");
                transact(new AddExpense(this,
                                100,
                                r),
                        onComplete);
                break;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean upDateRecord(final UpdatePackage updatePackage) {
        LogUtil.i(TAG,
                "upDateRecord: " +
                        updatePackage.getRecord()
                                .getId());
        transact(
                new UpdateRecord(this,
                        100,
                        updatePackage),
                new Tx.OnComplete<Boolean>() {
                    @Override
                    public void onComplete(int id,
                                           Boolean success) {
                        if (success) {
                            notifyToast("Record updated");
                            finish();
                        } else {
                            notifyDialog("Updating this record failed",
                                    "Possible loss of account integrity");
                        }
                    }
                });

        return true;
    }

    private boolean deleteRecord(final Record r) {
        notifyDialog("Please confirm",
                "You are about to delete a record",
                new NDialog.DButton(
                        "Confirm",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                doConfirmedDelete(
                                        r);
                            }
                        }),
                null);
        return true;
    }

    private void doConfirmedDelete(final Record r) {
        transact(
                new DeleteRecord(this,
                        100,
                        r),
                new Tx.OnComplete<Boolean>() {
                    @Override
                    public void onComplete(int id,
                                           Boolean success) {
                        if (success) {
                            notifyToast("Record deleted");
                            LogUtil.i(TAG, "deleted record");
                            finish();
                        } else {
                            notifyDialog("Deleting this record failed",
                                    "Possible loss of account integrity");
                        }
                    }
                });
    }

    private boolean checkInputs() throws NullItem,
            NullAmount,
            NullChoice {
        LogUtil.i(TAG,
                "checkInputs: start");
        String number =
                calculator.getCurrentExpression();
        if (userChoice == NO_CHOICE) {
            notifyToast("Choose income or" +
                            " expense add try again");
            notifyVibration();
            shakeView(incomeButton);
            shakeView(expenseButton);
            throw new NullChoice(
                    "No choice selected");
        } else if (category.equals(
                defaultCategory)) {
            shakeView(categoryButton);
            throw new NullItem(
                    "No item selected");
        } else if (number.isEmpty()) {
            throw new NullAmount(
                    "No amount specified");
        } else {
            LogUtil.i(TAG,
                    "checkInputs: correct");
            return true;
        }
    }

    private void notifyError(BudgetException e) {
        notifyToast(e.getMessage());
    }

    public static class AddExpense extends DBTrans<Boolean> {
        private boolean ans;
        private Record record;
        private AccountManager manager;

        private boolean updateDailySpent = false;

        public AddExpense(Context context,
                          int id,
                          Record r) {
            super(context,
                    id);
            this.setRecord(r);
            manager = new AccountManager(getContext());

            setAns(false);
            updateDailySpent = Func.isToday(
                    r.getDate_millis());
        }


        public void setAns(boolean ans) {
            this.ans = ans;
        }

        public Record getRecord() {
            return record;
        }

        public void setRecord(Record record) {
            this.record = record;
        }


        @Override
        public Progress<Boolean,
                            Integer> getProgress() {
            return null;
        }

        @Override
        public CallBacks<Boolean, Integer> getCallBacks() {
            return new CallBacks<Boolean, Integer>() {
                @Override
                public void onStart() {

                }

                @Override
                public Boolean onExecute() {
                    boolean addRecord = getDb().save(getRecord());
                    logTransaction("Adding record " +
                            getRecord().getName() +
                            "of amount " +
                            getRecord().getAmount() +
                            " " +
                            addRecord);
                    AccountManager.Info info =
                            manager.getInfo();
                    logTransaction("Getting current " +
                            "account info" +
                            info.getName());
                    int totalBal = info.getTotalBal();
                    if (updateDailySpent) {
                        int dailySpent =
                                info.getDailySpent();
                        dailySpent += getRecord().getAmount();
                        info.setDailySpent(dailySpent);
                    }
                    info.setTotalBal(totalBal - getRecord().getAmount());
                    return addRecord &&
                            manager.updateInfo(info);
                }


                @Override
                public void onProgress(Integer... x) {

                }


                @Override
                public void onEnd(Boolean aBoolean) {

                }
            };
        }
    }

    public static class AddIncome extends
            DBTrans<Boolean> {
        private Record record;
        private AccountManager manager;


        public AddIncome(Context context,
                         int id,
                         Record r) {
            super(context,
                    id);
            manager = new AccountManager(getContext());
            this.setRecord(r);
        }

        public Record getRecord() {
            return record;
        }

        public void setRecord(Record record) {
            this.record = record;
        }

        @Override
        public Progress<Boolean, Integer> getProgress() {
            return null;
        }

        @Override
        public CallBacks<Boolean, Integer> getCallBacks() {
            return new CallBacks<Boolean, Integer>() {
                @Override
                public void onStart() {

                }

                @Override
                public Boolean onExecute() {
                    boolean addRecord = getDb().save(getRecord());
                    logTransaction("Adding record " +
                            getRecord().getName() +
                            " amount " +
                            getRecord().getAmount() +
                            " " +
                            addRecord);
                    AccountManager.Info info =
                            manager.getInfo();
                    logTransaction("Getting current account info " +
                            info.getName());
                    int totalBal = info.getTotalBal();
                    totalBal += getRecord().getAmount();
                    info.setTotalBal(totalBal);
                    return addRecord && manager.updateInfo(info);
                }
                @Override
                public void onProgress(Integer... x) {

                }


                @Override
                public void onEnd(Boolean aBoolean) {

                }
            };
        }
    }

    /**
     * Created by octopus on 8/10/16.
     */
    public static class DeleteRecord extends DBTrans<Boolean> {
        private Record record;
        private boolean updateSpent;
        private AccountManager manager;
        public DeleteRecord(Context context,
                            int id,
                            Record r) {
            super(context,
                    id);
            manager = new AccountManager(context);
            this.setRecord(r);
            updateSpent = Func.isToday(r.getDate_millis());
        }
        private boolean authenticateDelete(int balance) {
            int newBal = balance;
            if (getRecord().isIncome()) {
                newBal = balance - getRecord().getAmount();
            }
            return newBal >= 0;
        }

        public Record getRecord() {
            return record;
        }

        public void setRecord(Record record) {
            this.record = record;
        }


        @Override
        public Progress<Boolean, Integer> getProgress() {
            return null;
        }

        @Override
        public CallBacks<Boolean, Integer> getCallBacks() {
            return new CallBacks<Boolean, Integer>() {
                @Override
                public void onStart() {

                }

                @Override
                public Boolean onExecute() {
                    AccountManager.Info info =
                            manager.getInfo();
                    int balance = info.getTotalBal();
                    if (authenticateDelete(balance)) {
                        if (getRecord().isIncome()) {
                            info.setTotalBal(balance - getRecord().getAmount());
                        }
                        else  {
                            if (updateSpent) {
                                info.setDailySpent(info.getDailySpent() -
                                        getRecord().getAmount());
                            }
                            info.setTotalBal(info.getTotalBal() +
                                    getRecord().getAmount());
                        }
                        return manager.updateInfo(info) &&
                                getDb().delete(getRecord());

                    }
                    return false;
                }

                @Override
                public void onProgress(Integer... x) {

                }

                @Override
                public void onEnd(Boolean aBoolean) {

                }
            };
        }
    }

    public static class UpdateRecord extends DBTrans<Boolean> {
        private String TAG = LogUtil.makeTag(UpdateRecord.class);
        private boolean updateDailySpent = false;
        private String originalType;
        private int originalAmount;
        private Record record;
        private AccountManager manager;

        public UpdateRecord(Context context,
                            int id,
                            UpdatePackage updatePackage) {
            super(context,
                    id);
            manager = new AccountManager(context);
            this.originalType =
                    updatePackage.getOld().getType();
            this.originalAmount =
                    updatePackage.getOld().getAmount();
            this.record =
                    updatePackage.getRecord();
            updateDailySpent = Func.isToday(
                    updatePackage.getRecord()
                            .getDate_millis());
        }

        private boolean authenticateUpdate(int nowBal,
                                           int nowSpent) {
            LogUtil.i(TAG,
                    "authenticateUpdate: ");
            int balance = nowBal;
            int spent = nowSpent;
            if (originalType.equals(
                    record.getType())) {
                if (originalType.equals(
                        Record.Category.INCOME_TYPE)) {
                    if (originalAmount <
                            record.getAmount()) {
                        int difference = record.getAmount() -
                                originalAmount;
                        balance += difference;
                    } else if (originalAmount >
                            record.getAmount()) {
                        int difference = originalAmount -
                                record.getAmount();
                        balance -= difference;
                    }
                } else if (originalType.equals(
                        Record.Category.EXPENSE_TYPE)) {
                    if (originalAmount <
                            record.getAmount()) {
                        int difference =
                                record.getAmount() -
                                        originalAmount;
                        if (updateDailySpent) {
                            spent += difference;
                        }
                        balance -= difference;
                    } else if (originalAmount >
                            record.getAmount()) {
                        int difference = originalAmount -
                                record.getAmount();
                        if (updateDailySpent) {
                            spent -= difference;
                        }
                        balance += difference;
                    }
                }
            } else if (originalType.equals(
                    Record.Category.INCOME_TYPE) &&
                    record.getType().equals(
                            Record.Category.EXPENSE_TYPE)) {
                balance = balance -
                        originalAmount -
                        record.getAmount();
                if (updateDailySpent) {
                    spent += record.getAmount();
                }
            } else if (originalType.equals(
                    Record.Category.EXPENSE_TYPE) &&
                    record.getType().equals(
                            Record.Category.INCOME_TYPE)) {
                balance = balance +
                        originalAmount +
                        record.getAmount();
                if (updateDailySpent) {
                    spent -= record.getAmount();
                }
            }
            return balance < 0 ||
                    spent < 0;
        }


        public Record getRecord() {
            return record;
        }

        public void setRecord(Record record) {
            this.record = record;
        }


        @Override
        public Progress<Boolean, Integer> getProgress() {
            return null;
        }

        @Override
        public CallBacks<Boolean,
                Integer> getCallBacks() {
            return new CallBacks<Boolean,
                    Integer>() {
                @Override
                public void onStart() {

                }
                @Override
                public Boolean onExecute() {
                    logTransaction("executing ");
                    AccountManager.Info info =
                            manager.getInfo();
                    int balance =
                            info.getTotalBal();
                    int spent =
                            info.getDailySpent();
                    if (authenticateUpdate(balance,
                            spent)) {
                        LogUtil.i(TAG,
                                "execute: exiting...");
                        return false;
                    }
                    LogUtil.i(TAG,
                            "execute: updating");
                    if (originalType.equals(
                            record.getType())) {
                        LogUtil.i(TAG,
                                "execute: same types");
                        if (originalType.equals(
                                Record.Category.INCOME_TYPE)) {
                            LogUtil.i(TAG,
                                    "execute: income types");
                            if (originalAmount <
                                    record.getAmount()) {
                                LogUtil.i(TAG,
                                        "execute: new amount" +
                                                " is higher" +
                                                " than old amount");
                                int difference = record.getAmount() -
                                        originalAmount;
                                balance += difference;
                            } else if (originalAmount >
                                    record.getAmount()) {
                                LogUtil.i(TAG,
                                        "execute: new amount" +
                                                " is smaller " +
                                                "than old");
                                int difference = originalAmount -
                                        record.getAmount();
                                balance -= difference;
                            }
                        } else if (originalType.equals(
                                Record.Category.EXPENSE_TYPE)) {
                            LogUtil.i(TAG,
                                    "execute: expense types");
                            if (originalAmount <
                                    record.getAmount()) {
                                LogUtil.i(TAG,
                                        "execute: new amount" +
                                                " bigger" +
                                                " than old amount");
                                int difference = record.getAmount() -
                                        originalAmount;
                                balance -= difference;
                                if (updateDailySpent) {
                                    spent += difference;
                                }

                            } else if (originalAmount >
                                    record.getAmount()) {
                                LogUtil.i(TAG,
                                        "execute: old amount " +
                                                "is" +
                                                " higher than new");
                                int difference = originalAmount -
                                        record.getAmount();
                                balance += difference;
                                if (updateDailySpent) {
                                    spent -= difference;
                                }
                            }
                        }
                    } else if (originalType.equals(
                            Record.Category.INCOME_TYPE) &&
                            record.getType().equals(
                                    Record.Category.EXPENSE_TYPE)) {
                        LogUtil.i(TAG,
                                "execute: income to " +
                                        "expense");
                        LogUtil.i(TAG,
                                "execute: reducing balance");
                        balance -=
                                originalAmount -
                                record.getAmount();
                        info.setTotalBal(balance);
                        if (updateDailySpent) {
                            spent += record.getAmount();
                        }
                    } else if (originalType.equals(
                            Record.Category.EXPENSE_TYPE) &&
                            record.getType().equals(
                                    Record.Category.INCOME_TYPE)) {
                        LogUtil.i(TAG,
                                "execute: expense to " +
                                        "income");
                        LogUtil.i(TAG,
                                "execute: increasing balance");
                        balance +=
                                originalAmount +
                                record.getAmount();
                        if (updateDailySpent) {
                            spent -= record.getAmount();
                        }
                    }
                    info.setDailySpent(spent);
                    info.setTotalBal(balance);
                    return manager.updateInfo(info) &&
                            getDb().update(record);
                }
                @Override
                public void onProgress(Integer... x) {

                }



                @Override
                public void onEnd(Boolean aBoolean) {

                }
            };
        }
    }
}
