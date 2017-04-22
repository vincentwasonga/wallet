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

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.octopus.wallet.R;
import com.octopus.wallet.m.h.ArrangeOrder;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.p.fl.expoimpo.data.Basic;
import com.octopus.wallet.m.p.fl.expoimpo.data.Ledger;
import com.octopus.wallet.m.p.fl.expoimpo.data.Template;
import com.octopus.wallet.m.p.fl.expoimpo.exp.Format;
import com.octopus.wallet.m.p.fl.expoimpo.exp.ReportExporter;
import com.octopus.wallet.m.p.fl.fmodels.FExportData;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.tx.FLTrans;
import com.octopus.wallet.u.a.BActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.tx.Tx;
import me.yoctopus.cac.util.LogUtil;

public class ExportActivity extends BActivity {
    private final int defaultChoice = -1;
    private final int allRecordsChoice = Template.Filter.ALL_TYPES;
    private final int incomesChoice = Template.Filter.INCOMES;
    private final int expensesChoice = Template.Filter.EXPENSES;
    private final int basicChoice = 3;
    private final int advancedChoice = 4;
    @InjectView(R.id.exportNameEditText)
    EditText exportNameEditText;
    @InjectView(R.id.allRecordCheckbox)
    Switch allRecordCheckbox;
    @InjectView(R.id.dateStartSpinner)
    Spinner dateStartSpinner;
    @InjectView(R.id.dateEndSpinner)
    Spinner dateEndSpinner;
    @InjectView(R.id.dateLinearLayout)
    LinearLayout dateLinearLayout;
    @InjectView(R.id.includeBalanceButon)
    Switch includeBalanceSwitch;
    @InjectView(R.id.csvRadioButton)
    RadioButton csvRadioButton;
    @InjectView(R.id.xlsRadioButton)
    RadioButton xlsRadioButton;
    @InjectView(R.id.pdfRadioButton)
    RadioButton pdfRadioButton;
    @InjectView(R.id.ad_view)
    AdView adView;
    private Format exportChoice = Format.DEFAULT;
    private boolean includeBalance = true;
    private Template template;
    private Template.Filter filter =
            new Template.Filter(Template.Filter.ALL_TYPES);
    private int recordsChoice = defaultChoice;
    private int dataGroupChoice = defaultChoice;
    private ArrayList<Long> dates;
    private Runnable initRunnable =
            new Runnable() {
                @Override
                public void run() {
                    List<String> categories = new ArrayList<>();
                    for (int i = 0;
                         i < dates.size();
                         i++) {
                        long time = dates.get(i);
                        categories.add(
                                Func.getDateDayddMMM(time));
                    }
                    prepSpinner(dateStartSpinner,
                            categories,
                            new SpinnerChoice() {
                                @Override
                                public void onSelected(int id) {
                                    filter.setDateStart(new Date(dates.get(id)));
                                }

                                @Override
                                public void onNotSelected() {
                                    filter.setDateStart(null);
                                }
                            });
                    prepSpinner(dateEndSpinner,
                            categories,
                            new SpinnerChoice() {
                                @Override
                                public void onSelected(int id) {
                                    filter.setDateEnd(new Date(dates.get(id)));
                                }

                                @Override
                                public void onNotSelected() {
                                    filter.setDateEnd(null);
                                }
                            });
                }
            };
    @OnClick({R.id.dashBoardRadioButton,
            R.id.overViewRadioButton,
            R.id.allRecordsRadioButton,
            R.id.incomesRadioButton,
            R.id.includeBalanceButon,
            R.id.expensesRadioButton,
            R.id.allRecordCheckbox,
            R.id.csvRadioButton,
            R.id.xlsRadioButton,
            R.id.pdfRadioButton,
            R.id.exportBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dashBoardRadioButton:
                dataGroupChoice = basicChoice;
                break;
            case R.id.overViewRadioButton:
                dataGroupChoice = advancedChoice;
                break;
            case R.id.allRecordsRadioButton:
                recordsChoice = allRecordsChoice;
                break;
            case R.id.incomesRadioButton:
                recordsChoice = incomesChoice;
                break;
            case R.id.expensesRadioButton:
                recordsChoice = expensesChoice;
                break;
            case R.id.includeBalanceButon:
                includeBalance = includeBalanceSwitch.isChecked();
                break;
            case R.id.allRecordCheckbox:
                boolean useAllRecords = allRecordCheckbox.isChecked();
                if (!useAllRecords) {
                    dateLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    dateLinearLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.csvRadioButton:
                saveExportChoice(csvRadioButton);
                break;
            case R.id.xlsRadioButton:
                saveExportChoice(xlsRadioButton);
                break;
            case R.id.pdfRadioButton:
                saveExportChoice(pdfRadioButton);
                break;
            case R.id.exportBtn:
                export();
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("New Template");
        setContentView(R.layout.activity_export);
        ButterKnife.inject(this);
        setHasBackButton(true);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        dates = getRecordDates();
        executeRunnable(initRunnable);
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void saveExportChoice(RadioButton radioButton) {
        switch (radioButton.getId()) {
            case R.id.csvRadioButton: {
                exportChoice =
                        Format.CSV;
                break;
            }
            case R.id.xlsRadioButton: {
                exportChoice =
                        Format.XLS;
                break;
            }
            case R.id.pdfRadioButton: {
                exportChoice =
                        Format.PDF;
                break;
            }
        }

    }

    private void export() {
        if (exportNameEditText.getText() == null ||
                exportNameEditText.getText()
                        .toString().isEmpty()) {
            shakeView(
                    exportNameEditText);
            notifyToast("No export name entered");
            return;
        }
        if (dataGroupChoice == defaultChoice) {
            shakeView(
                    R.id.dataGroupRadioGroup);
            notifyToast("No data group selected");
            return;
        }
        if (recordsChoice == defaultChoice) {
            shakeView(
                    R.id.recordTypeRadioGroup);
            notifyToast("No records type selected");
            return;
        }
        if (exportChoice.getFormat() == Format.DE) {
            shakeView(
                    R.id.choiceGroup);
            notifyToast("No choice selected");
            return;
        }
        doConfirmedExport();
    }

    private void doConfirmedExport() {
        String exportName =
                exportNameEditText.getText().toString();
        if (exportChoice.getFormat() == Format.XL) {
            notifyToast("Other formats coming soon");
            return;
        }
        filter.setTypes(recordsChoice);
        AccountManager manager =
                new AccountManager(this);
        switch (dataGroupChoice) {
            case basicChoice: {
                List<Record> records = getDatabase().getRecords();
                template = new Basic(
                        exportName.trim(),
                        includeBalance,
                        filter,
                        getDayRecords(),
                        manager.getInfo().getTotalBal(),
                        getRecordTotal(records, 0),
                        getRecordTotal(records, 1));
                break;
            }
            case advancedChoice: {
                template = new Ledger(
                        exportName.trim(),
                        includeBalance,
                        filter,
                        getDayRecords(),
                        manager.getInfo().getTotalBal());
                break;
            }
        }
        transact(new SaveFile(this, 100, template, exportChoice),
                new Tx.OnComplete<FExportData>() {
                    @Override
                    public void onComplete(int id, FExportData exportData) {
                        if (exportData.isExported()) {
                            notifyToast("File exported successfully");
                            finish();
                        } else {
                            notifyDialog("Info",
                                    "Operation failed",
                                    new NDialog.DButton("Try again",
                                            new NDialog.DButton.BListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            }),
                                    null);
                        }
                    }
                });
        notifyProgress("Exporting, please wait...");
    }
    private int getRecordTotal(List<Record> records, int type) {
        int sum = 0;
        switch (type) {
            case 0 :{
                for (Record record : records) {
                    if (record.isIncome()) {
                        sum += record.getAmount();
                    }
                }
            }
            case 1 : {
                for (Record record : records) {
                    if (record.isExpense()) {
                        sum += record.getAmount();
                    }
                }
            }
        }
        return sum;
    }

    private ArrayList<Long> getRecordDates() {
        ArrayList<Long> dates = new ArrayList<>();
        ArrayList<DayRecords> records = getDayRecords();
        records = Func.sortDayRecords(records,
                new ArrangeOrder(ArrangeOrder.TA));
        for (DayRecords records1 : records) {
            long time = records1.getDay().getDate().getTime();
            dates.add(time);
        }
        return dates;
    }

    public static class SaveFile extends FLTrans<FExportData> {

        private String TAG = LogUtil.makeTag(SaveFile.class);
        private Template data;
        private Format choice;

        public SaveFile(Context context,
                        int id,
                        Template data,
                        Format choice) {
            super(context, id);
            this.data = data;
            this.choice = choice;
        }

        @Override
        public Progress<FExportData, FExportData> getProgress() {
            return null;
        }

        @Override
        public CallBacks<FExportData, FExportData> getCallBacks() {
            return new CallBacks<FExportData, FExportData>() {
                @Override
                public void onStart() {

                }

                @Override
                public FExportData onExecute() {
                    LogUtil.i(TAG,
                            "execute: ");
                    FExportData exportData =
                            new FExportData(
                                    "export_confirmation");
                    ReportExporter exporter = new ReportExporter(
                            data,
                            choice);
                    exportData.setExported(
                            getFManager().saveData(exporter));
                    return exportData;
                }

                @Override
                public void onProgress(FExportData... x) {

                }


                @Override
                public void onEnd(FExportData simpleFileExportData) {
                    LogUtil.i(TAG,
                            "endTransaction: " +
                                    simpleFileExportData.toString());

                }
            };
        }
    }
}
