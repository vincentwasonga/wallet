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

package com.octopus.budget.activities.transaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.octopus.budget.BudgetApp;
import com.octopus.budget.R;
import com.octopus.budget.models.DescriptionSet;
import com.octopus.budget.models.Record;
import com.octopus.budget.models.TempData;
import com.octopus.budget.models.Transaction;
import com.octopus.budget.models.errors.AmountNotAcceptedException;
import com.octopus.budget.models.errors.BudgetException;
import com.octopus.budget.models.errors.InsufficientBalanceException;
import com.octopus.budget.models.errors.NullAmountException;
import com.octopus.budget.models.errors.NullChoiceException;
import com.octopus.budget.models.errors.NullItemException;
import com.octopus.budget.models.errors.OverflowDailyExpenseException;
import com.octopus.budget.models.math.HelperFunc;
import com.octopus.budget.models.notification.DialogButton;
import com.octopus.budget.models.notification.Notification;
import com.octopus.budget.models.recordanalysers.UpdatePackage;

import java.util.ArrayList;
import java.util.Date;

public class TransactionActivity extends AppCompatActivity implements View.OnClickListener, Transaction.TransactionCompleteListener {
    private final int categoryRequestCode = 1;
    private final int dateRequestCode = 100;
    private final int descriptionRequest = 500;
    private BudgetApp app;
    private int userChoice = Transaction.SAVE_INFO;
    private Notification notification;
    private Button itemButton;
    private String defaultCategory = " ";
    private String categoryInput = defaultCategory;
    private TextView amountTextView;
    private ArrayList<Button> buttons;
    private ImageButton tickButton;
    private RadioButton incomeBtn, expenseBtn;
    private StringBuilder entryNumber;
    private DescriptionSet descriptionSet;
    private TextClock textClock;
    private Button clock;
    private String TAG = "TransactionActivity";
    private UpdatePackage updatePackage;
    private boolean isUpDating = false;
    private Button imageButton;
    private Button deleteBtn;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_transaction);

    }

    private void saveUserChoiceState() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.incomeButton: {
                Log.i(TAG, "saveUserChoiceState: income");
                userChoice = Transaction.INCOME;
                break;
            }
            case R.id.expenseButton: {
                Log.i(TAG, "saveUserChoiceState: expense");
                userChoice = Transaction.EXPENSE;
                break;
            }
        }
    }

    private void saveUserChoiceState(int id) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.check(id);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.incomeButton: {
                Log.i(TAG, "saveUserChoiceState: income");
                userChoice = Transaction.INCOME;
                break;
            }
            case R.id.expenseButton: {
                Log.i(TAG, "saveUserChoiceState: expense");
                userChoice = Transaction.EXPENSE;
                break;
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i(TAG, "onPostCreate: ");
        entryNumber = new StringBuilder();
        imageButton = (Button) findViewById(R.id.descriptionBtn);
        app = (BudgetApp) getApplication();
        incomeBtn = (RadioButton) findViewById(R.id.incomeButton);
        expenseBtn = (RadioButton) findViewById(R.id.expenseButton);
        descriptionSet = new DescriptionSet();
        itemButton = (Button) findViewById(R.id.itemButton);
        itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCategory(view);
            }
        });
        amountTextView = (TextView) findViewById(R.id.AmountText);
        incomeBtn = (RadioButton) findViewById(R.id.incomeButton);
        expenseBtn = (RadioButton) findViewById(R.id.expenseButton);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            clock = (Button) findViewById(R.id.textClock);
            clock.setText("Now");
        } else {
            textClock = (TextClock) findViewById(R.id.textClock);
            textClock.setText("Now");
        }

        buttons = new ArrayList<>();
        buttons.add((Button) findViewById(R.id.btn0));
        buttons.add((Button) findViewById(R.id.btn1));
        buttons.add((Button) findViewById(R.id.btn2));
        buttons.add((Button) findViewById(R.id.btn3));
        buttons.add((Button) findViewById(R.id.btn4));
        buttons.add((Button) findViewById(R.id.btn5));
        buttons.add((Button) findViewById(R.id.btn6));
        buttons.add((Button) findViewById(R.id.btn7));
        buttons.add((Button) findViewById(R.id.btn8));
        buttons.add((Button) findViewById(R.id.btn9));
        buttons.add((Button) findViewById(R.id.btnx));
        tickButton = (ImageButton) findViewById(R.id.btndone);
        deleteBtn = (Button) findViewById(R.id.delete_transaction_btn);
        notification = new Notification(this);
        Intent intent = getIntent();
        if (intent.hasExtra("record")) {
            Record record = intent.getParcelableExtra("record");
            updatePackage = new UpdatePackage(record);
            itemButton.setText(record.getNameItem());
            categoryInput = itemButton.getText().toString();
            descriptionSet = record.getDescriptionSet();
            setAmountText(record.getAmount());
            entryNumber.append(record.getAmount());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                clock.setText(HelperFunc.getTimeString(new Date(record.getDate_millis())));
            } else {
                textClock.setText(HelperFunc.getTimeString(new Date(record.getDate_millis())));
            }
            if (record.getType().equals(Record.Income)) {
                saveUserChoiceState(R.id.incomeButton);
            } else if (record.getType().equals(Record.Expense)) {
                saveUserChoiceState(R.id.expenseButton);
            }
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(this);
            setTitle("Update Record");
            isUpDating = true;
        }
        configureDescriptionDialog();
        configureKeypad();
        configureClock();
    }

    private void configureClock() {
        Log.i(TAG, "configureClock: ");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            clock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TransactionActivity.this, DateTimePickerActivity.class);
                    if (isUpDating) {
                        intent.putExtra("date_millis", updatePackage.getRecord().getDate_millis());
                    }
                    startActivityForResult(intent, dateRequestCode);
                }
            });
        } else {
            textClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TransactionActivity.this, DateTimePickerActivity.class);
                    if (isUpDating) {
                        intent.putExtra("date_millis", updatePackage.getRecord().getDate_millis());
                    }
                    startActivityForResult(intent, dateRequestCode);
                }
            });
        }

    }

    private void configureDescriptionDialog() {
        Log.i(TAG, "configureDescriptionDialog: ");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDescriptionActivity();
            }
        });
    }

    private void startDescriptionActivity() {
        Log.i(TAG, "startDescriptionActivity: ");
        Intent intent = new Intent(TransactionActivity.this, DescriptionActivity.class);
        if (!entryNumber.toString().isEmpty()) {
            int amount = Integer.parseInt(entryNumber.toString());
            intent.putExtra("Amount", amount);
        }
        if (isUpDating) {
            intent.putExtra("description", descriptionSet);
        }
        startActivityForResult(intent, descriptionRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case descriptionRequest: {
                    if (data.hasExtra("Description")) {
                        Log.i(TAG, "onActivityResult: " + descriptionRequest);
                        DescriptionSet newSet = data.getParcelableExtra("Description");
                        descriptionSet.setDescription(newSet.getDescription());
                        descriptionSet.setLocationName(newSet.getLocationName());
                        descriptionSet.setReceiptName(newSet.getReceiptName());
                        if (isUpDating) {
                            updatePackage.getRecord().setDescriptionSet(descriptionSet);
                        }
                    }
                    break;
                }
                case categoryRequestCode: {
                    if (data.hasExtra("category")) {
                        Log.i(TAG, "onActivityResult: " + categoryRequestCode);
                        categoryInput = data.getStringExtra("category");
                        itemButton.setText(categoryInput);
                        if (isUpDating) {
                            updatePackage.setNameItem(categoryInput);
                        }
                    }
                    break;
                }
                case dateRequestCode: {
                    if (data.hasExtra("datetime")) {
                        Log.i(TAG, "onActivityResult: " + dateRequestCode);
                        long time = data.getLongExtra("datetime", Record.DEFAULT_MILLIS);
                        Date date = HelperFunc.getDate(time);
                        if (isUpDating) {
                            updatePackage.setDateMillis(time);
                        } else {
                            this.date = date;
                        }
                        Log.i(TAG, "onActivityResult: new time " + date.toString());
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            clock.setText(HelperFunc.getTimeString(date));
                        } else {
                            textClock.setText(HelperFunc.getTimeString(date));
                        }

                    }
                    break;
                }
            }
        }
    }

    private void configureKeypad() {
        Log.i(TAG, "configureKeypad: ");
        incomeBtn.setOnClickListener(this);
        expenseBtn.setOnClickListener(this);
        tickButton.setOnClickListener(this);
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setOnClickListener(this);
        }
    }

    private void chooseCategory(View view) {
        Log.i(TAG, "chooseCategory: ");
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("items", app.getAccount().getItems());
        startActivityForResult(intent, categoryRequestCode);
    }

    private void setAmountText(int amount) {
        amountTextView.setText(HelperFunc.getMoney(amount));
    }

    @Override
    public void onClick(View view) {
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
                Button b = (Button) view;
                entryNumber.append(b.getText());
                try {
                    setAmountText(Integer.parseInt(entryNumber.toString()));
                } catch (NumberFormatException e) {
                    notification.setNotificationBundle("Amount is not allowed");
                    notification.notify(Notification.TOAST);
                    notification.notify(Notification.VIBRATE);
                    entryNumber = null;
                    entryNumber = new StringBuilder();
                    setAmountText(0);
                }
                break;
            }
            case R.id.btnx: {
                Log.i(TAG, "onClick: btnx");
                if (entryNumber.toString().length() <= 1) {
                    setAmountText(0);
                    entryNumber = null;
                    entryNumber = new StringBuilder();
                    break;
                } else {
                    String newEntry = entryNumber.substring(0, entryNumber.length() - 1);
                    entryNumber = null;
                    entryNumber = new StringBuilder(newEntry);
                    setAmountText(Integer.parseInt(entryNumber.toString()));
                }
                break;
            }
            case R.id.btndone: {
                Log.i(TAG, "onClick: btndone");
                String item = null, amount = null;
                try {
                    if (!isUpDating) {
                        if (checkInputs()) {
                            item = itemButton.getText().toString();
                            if (entryNumber.toString().length() <= 1 || entryNumber.toString().isEmpty()) {
                                Log.i(TAG, "onClick: throwing error");
                                throw new AmountNotAcceptedException("Amount cannot be saved");
                            }
                            amount = entryNumber.toString();
                            if (userChoice == Transaction.EXPENSE) {
                                int cash = Integer.parseInt(amount);
                                int dailySpent = app.getAccount().getInfo().getDailySpent();
                                int dailyLimit = app.getAccount().getInfo().getDailyLimit();
                                int totalBal = app.getAccount().getInfo().getTotalBal();
                                if (cash > totalBal) {
                                    Log.i(TAG, "onClick: insufficient balance");
                                    throw new InsufficientBalanceException("You do not have enough balance to transact ");
                                } else if (cash + dailySpent >= dailyLimit) {
                                    Log.i(TAG, "onClick: insufficient money");
                                    throw new OverflowDailyExpenseException("You are about to overspend");
                                }
                            }
                            transact(item, amount, descriptionSet);
                        }
                    }
                    if (isUpDating) {
                        item = itemButton.getText().toString();
                        updatePackage.setNameItem(item);
                        amount = entryNumber.toString();
                        upDateRecord(updatePackage, amount);
                    }
                } catch (NullItemException | NullAmountException | NullChoiceException | AmountNotAcceptedException e) {
                    notifyError(e);
                    notification.notify(Notification.VIBRATE);
                } catch (OverflowDailyExpenseException e) {
                    notifyExcessSpending(item, amount, descriptionSet, e);
                    notification.notify(Notification.VIBRATE);
                } catch (InsufficientBalanceException e) {
                    notifyInsufficientBalance(e);
                    notification.notify(Notification.VIBRATE);
                }
                break;
            }
            case R.id.incomeButton:
            case R.id.expenseButton: {
                saveUserChoiceState();
                break;
            }
            case R.id.delete_transaction_btn: {
                deleteRecord(updatePackage.getRecord());
            }
        }
    }

    private void notifyExcessSpending(String item, String amount, DescriptionSet descriptionSet, OverflowDailyExpenseException e) {
        final String it = item, am = amount;
        final DescriptionSet descriptionSet1 = descriptionSet;
        notification.setNotificationBundle("Excess Spending", e.getMessage(), new DialogButton("Ignore", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                transact(it, am, descriptionSet1);
            }
        }), null);
        notification.notify(Notification.DIALOG);
    }

    private void notifyInsufficientBalance(InsufficientBalanceException e) {
        notification.setNotificationBundle("Insufficient Balance", e.getMessage());
        notification.notify(Notification.DIALOG);
    }

    private boolean transact(String item, String amount, DescriptionSet descriptionSet) {
        int cash = Integer.parseInt(amount);
        app.getAccount().setActivityContext(this);
        switch (userChoice) {
            case Transaction.INCOME: {
                Log.i(TAG, "transact: adding income");
                app.getAccount().transactIncome(item, cash, descriptionSet, date);
                break;
            }
            case Transaction.EXPENSE: {
                Log.i(TAG, "transact: adding expense");
                app.getAccount().transactExpense(item, cash, descriptionSet, date);
                break;
            }
        }
        app.getAccount().setOnTransactionCompleteListener(this);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        app = (BudgetApp) getApplication();
    }

    private boolean upDateRecord(UpdatePackage updatePackage, String amount) {
        Log.i(TAG, "upDateRecord: " + updatePackage.getRecord().getId());
        int cash = Integer.parseInt(amount);
        app.getAccount().setActivityContext(this);
        switch (userChoice) {
            case Transaction.INCOME: {
                updatePackage.setType(Record.Income);
                break;
            }
            case Transaction.EXPENSE: {
                updatePackage.setType(Record.Expense);
                break;
            }
        }
        updatePackage.setAmount(cash);
        app.getAccount().updateRecord(updatePackage);
        app.getAccount().setOnTransactionCompleteListener(this);
        return true;
    }

    private boolean deleteRecord(final Record r) {
        notification.setNotificationBundle("Confirm Delete",
                "You are about to delete a record",
                new DialogButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doConfirmedDelete(r);
                    }
                }), null);
        notification.notify(Notification.DIALOG);
        return true;
    }

    private void doConfirmedDelete(Record r) {
        app.getAccount().deleteRecord(r);
        app.getAccount().setOnTransactionCompleteListener(this);
    }

    private boolean checkInputs() throws NullItemException, NullAmountException, NullChoiceException {
        Log.i(TAG, "checkInputs: start");
        if (userChoice == Transaction.SAVE_INFO) {
            throw new NullChoiceException("No choice selected");
        } else if (categoryInput.equals(defaultCategory)) {
            throw new NullItemException("No item selected");
        } else if (entryNumber.toString().isEmpty()) {
            throw new NullAmountException("No amount specified");
        } else {
            Log.i(TAG, "checkInputs: correct");
            return true;
        }
    }

    private void notifyError(BudgetException e) {
        notification.setNotificationBundle(e.getMessage(), null, new DialogButton("Correct", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                correctTransaction();
            }
        }), new DialogButton("Ignore", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ignoreTransaction();
            }
        }));
        notification.notify(Notification.DIALOG);
    }

    private void correctTransaction() {
        /**empty method body**/
    }

    private void ignoreTransaction() {
        finish();
    }

    @Override
    public void onTransactionComplete(int id, boolean success) {
        switch (id) {
            case Transaction.INCOME:
            case Transaction.EXPENSE: {
                if (success) {
                    app.getAccount().refresh();
                    notification.setNotificationBundle("Personal Budget", getNotificationTitle());
                    notification.setCurrentActivity(this);
                    notification.notify(Notification.NOTIFICATION_BAR);
                    finish();
                }
                break;
            }
            case Transaction.RECORDS: {
                app.getAccount().setRecords(TempData.getAccount().getRecords());
                break;
            }
            case Transaction.INFO: {
                app.getAccount().setInfo(TempData.getAccount().getInfo());
                break;
            }
            case Transaction.RECORD_UPDATE: {
                if (success) {
                    app.getAccount().refresh();
                    notification.setNotificationBundle("Personal Budget", "Record updated");
                    notification.setCurrentActivity(this);
                    notification.notify(Notification.NOTIFICATION_BAR);
                    finish();
                } else {
                    notification.setNotificationBundle("Updating this record failed", "Possible loss of account integrity");
                    notification.notify(Notification.DIALOG);
                }
                break;
            }
            case Transaction.RECORD_DELETE: {
                if (success) {
                    app.getAccount().refresh();
                    notification.setNotificationBundle("Personal Budget", "Record deleted");
                    notification.setCurrentActivity(this);
                    notification.notify(Notification.NOTIFICATION_BAR);
                    finish();
                } else {
                    notification.setNotificationBundle("Deleting this record failed", "Possible loss of account integrity");
                    notification.notify(Notification.DIALOG);
                }

                break;
            }
        }
    }

    public String getNotificationTitle() {
        return "New Record saved";
    }
}
