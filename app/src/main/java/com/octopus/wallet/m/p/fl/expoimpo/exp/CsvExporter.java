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

package com.octopus.wallet.m.p.fl.expoimpo.exp;

import com.octopus.wallet.m.e.RecordsEmpty;
import com.octopus.wallet.m.e.ReportExportException;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.RecordPair;
import com.octopus.wallet.m.h.RecordPairList;
import com.octopus.wallet.m.h.RecordsToPairsConverter;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.p.fl.Ext;
import com.octopus.wallet.m.p.fl.expoimpo.data.Basic;
import com.octopus.wallet.m.p.fl.expoimpo.data.Ledger;
import com.octopus.wallet.m.p.fl.expoimpo.data.Template;
import com.octopus.wallet.m.pb.Record;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import me.yoctopus.cac.util.LogUtil;

class CsvExporter extends Exporter {

    private BufferedWriter bufferedWriter;
    private FileOutputStream fileOutputStream;
    private String TAG = LogUtil.makeTag(CsvExporter.class);
    private boolean canWrite;

    public CsvExporter(Template data) throws ReportExportException {
        super(data, Ext.CSV);
        try {
            try {
                fileOutputStream = new FileOutputStream(getFileName());
                FileWriter fw = new FileWriter(new File(getFileName()));
                bufferedWriter = new BufferedWriter(fw);
                canWrite = true;
            } catch (FileNotFoundException e) {
                canWrite = false;
                throw new ReportExportException("Encoding/" +
                        "File not found error ");

            }

        } catch (IOException e) {
            canWrite = false;
            throw new ReportExportException(
                    "Unable to write to " +
                            "or read from file ");
        }
    }
    public void writeHeading(String heading) throws IOException {
        if (canWrite) {
            bufferedWriter.write(
                    heading.toUpperCase());
            writeEmptyLines(2);
        }

    }

    public void writeLine(String line) throws IOException {
        if (canWrite) {
            bufferedWriter.write(
                    line);
            writeEmptyLines(1);
        }

    }

    public void writeEmptyLines(int lines)
            throws IOException {
        if (canWrite) {
            for (int i = 0; i < lines; i++) {
                bufferedWriter.newLine();
            }
        }

    }

    public void flushWriter() throws IOException {
        if (canWrite) {
            bufferedWriter.flush();

        }

    }

    public void closeWriter() throws IOException {
        if (canWrite) {
            bufferedWriter.close();
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }



    @Override
    public boolean export(Template template) {
        if (template instanceof Basic) {
            exportData((Basic) template);
        } else if (template instanceof Ledger) {
            exportData((Ledger) template);
        }
        return true;
    }

    private void exportData(Basic data) {
        LogUtil.i(getTAG(),
                "exportCsv: Saving to " + getFileName());
        try {
            writeHeading("Records as at, " +
                    Func.getDateDayddMMM(
                            System.currentTimeMillis()));
            writeHeading("ID, " +
                    "Category Name ," +
                    " Amount, " +
                    "Time Transacted," +
                    " Description, " +
                    "Location, " +
                    "ReceiptID, " +
                    "NType of Tx");
            int i = 1;
            for (DayRecords dayRecords : data.getDayRecords()) {
                ArrayList<Record> records;
                switch (data.getFilter().getTypes()) {
                    case Template.Filter.INCOMES : {
                        records = dayRecords.getIncomes();
                        break;
                    }
                    case Template.Filter.EXPENSES:  {
                        records = dayRecords.getExpenses();
                        break;
                    }
                    default: {
                        records = dayRecords.getRecords();
                    }

                }
                for (Record record : records) {
                    LogUtil.d(getTAG(),
                            "writeToCSV: Saving record" +
                                    record.getName());
                    String oneLine = ("" + i) +
                            getSEPARATOR() +
                            (record.getName()
                                    .trim().length() == 0 ?
                                    " " :
                                    getColumnString(
                                            record.getName())) +
                            getSEPARATOR() +
                            (record.getAmount() < 0 ?
                                    " " :
                                    record.getAmount()) +
                            getSEPARATOR() +
                            Func.getDate(
                                    record.getDate_millis())
                                    .toString() +
                            getSEPARATOR() +
                            (record.getDescriptionSet()
                                    .getDescription().equals
                                            (Record.DescriptionSet.DEFAULT) ?
                                    " " :
                                    getColumnString(
                                            record.getDescriptionSet()
                                                    .getDescription())) +
                            getSEPARATOR() +
                            (record.getDescriptionSet()
                                    .getLocationName().equals(
                                            Record.DescriptionSet.DEFAULT) ?
                                    " " :
                                    getColumnString(
                                            record.getDescriptionSet()
                                                    .getLocationName())) +
                            getSEPARATOR() +
                            (record.getDescriptionSet()
                                    .getReceiptName().equals(
                                            Record.DescriptionSet.DEFAULT) ?
                                    " " :
                                    getReceiptName(
                                            record.getDescriptionSet()
                                                    .getReceiptName())) +
                            getSEPARATOR() +
                            (record.isIncome() ?
                                    "Credit" :
                                    "Debit");

                    writeLine(oneLine);
                    i++;
                }
            }

            writeEmptyLines(2);
            if (isWriteBalance()) {
                writeLine(" , " +
                        "Current Balance ," +
                        data.getTotal());
                writeEmptyLines(1);
                writeLine((" ," +
                        " Incomes verses Expenses")
                        .toUpperCase());
                writeEmptyLines(1);
                writeLine(" ," +
                        " Incomes , " +
                        data.getIncomes() +
                        ", " +
                        getPercentageIncome(
                                data.getIncomes(),
                                data.getExpenses()));

                int expenses = data.getExpenses();
                writeLine(" , " +
                        "Expenses , " +
                        expenses +
                        ", " +
                        getPercentageExpense(
                                data.getIncomes(),
                                data.getExpenses()));
            }

            flushWriter();
            LogUtil.i(getTAG(),
                    "writeToCSV: closing writer");
            closeWriter();
        } catch (UnsupportedEncodingException e) {
            LogUtil.i(getTAG(),
                    "writeToCSV: Encoding not supported");
        } catch (FileNotFoundException e) {
            LogUtil.i(getTAG(),
                    "writeToCSV: File not found");
        } catch (IOException e) {
            LogUtil.e(getTAG(),
                    "writeToCSV: ",
                    e);
        }
    }

    private String getColumnString(String columnData) {
        if (columnData.contains(getSEPARATOR())) {
            String s = columnData
                    .replaceAll(
                            getSEPARATOR(),
                            " ");
            return s;
        }
        return columnData;
    }

    private void exportData(Ledger data) {
        LogUtil.i(getTAG(),
                "exportCsv: Saving to " + getFileName());
        try {
            writeHeading("," +
                    " Ledger as at, " +
                    Func.getDateDayddMMM(
                            System.currentTimeMillis()));
            int i = 1;
            writeHeading("No," +
                    " Credits," +
                    " , " +
                    " Debits");
            for (DayRecords
                    dayRecords : data.getDayRecords()) {
                writeHeading(" ," +
                        dayRecords.getDay()
                                .getDayName());
                RecordsToPairsConverter converter =
                        new RecordsToPairsConverter(
                                dayRecords.getRecords());
                try {
                    converter.convert();
                } catch (RecordsEmpty e) {
                    e.printStackTrace();
                    return;
                }
                ArrayList<RecordPair> recordPairs =
                        converter.getRecordPairs();
                RecordPairList pairList =
                        new RecordPairList(
                                recordPairs);
                pairList.reArrange();
                recordPairs = pairList.getRecordPairs();
                for (RecordPair
                        pair : recordPairs) {
                    String oneLine = ("" +
                            i);
                    if (pair.hasBoth()) {
                        oneLine =
                                oneLine +
                                        getSEPARATOR() +
                                        pair.getIncome().getName() +
                                        getSEPARATOR() +
                                        pair.getIncome().getAmount() +
                                        getSEPARATOR() +
                                        pair.getExpense().getName() +
                                        getSEPARATOR() +
                                        pair.getExpense().getAmount();
                    } else if (pair.hasIncome()) {
                        oneLine =
                                oneLine +
                                        getSEPARATOR() +
                                        pair.getIncome().getName() +
                                        getSEPARATOR() +
                                        pair.getIncome().getAmount();

                    } else if (pair.hasExpense()) {
                        oneLine =
                                oneLine +
                                        getSEPARATOR() +
                                        getSEPARATOR() +
                                        getSEPARATOR() +
                                        pair.getExpense().getName() +
                                        getSEPARATOR() +
                                        pair.getExpense().getAmount();
                    }
                    writeLine(oneLine);
                    i++;
                }
                writeEmptyLines(1);
            }

            writeEmptyLines(2);
            if (isWriteBalance()) {
                writeLine(" , " +
                        "Current Balance ," +
                        data.getTotal());
            }
            flushWriter();
            LogUtil.i(getTAG(),
                    "writeToCSV: closing writer");
            closeWriter();
        } catch (UnsupportedEncodingException e) {
            LogUtil.i(getTAG(),
                    "writeToCSV: Encoding not supported");
        } catch (FileNotFoundException e) {
            LogUtil.i(getTAG(),
                    "writeToCSV: File not found");
        } catch (IOException e) {
            LogUtil.e(getTAG(),
                    "writeToCSV: ",
                    e);
        }
    }

    private String getReceiptName(String receiptID) {
        File file = new File(receiptID);
        return getColumnString(
                file.getName());
    }

    private String getPercentageIncome(int income,
                                       int expense) {
        int total = income + expense;
        if (total == 0) {
            return null;
        }
        double percent = Func.getPercentage(
                income,
                total);
        return percent +
                "%";

    }

    private String getPercentageExpense(int income,
                                        int expense) {
        int total = income +
                expense;
        if (total == 0) {
            return null;
        }
        double percent = Func.getPercentage(
                expense,
                total);
        return percent +
                "%";
    }

    private String getMessage() {
        return "File saved as " + getFileName();
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    private String getSEPARATOR() {
        return ",";
    }
}
