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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
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
import com.octopus.wallet.m.p.fl.expoimpo.data.Shopping;
import com.octopus.wallet.m.p.fl.expoimpo.data.Template;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.pb.ShoppingList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.yoctopus.cac.util.LogUtil;


class PdfExporter extends Exporter {
    private String TAG = LogUtil.makeTag(PdfExporter.class);
    private Document document;
    private Font font;
    private FileOutputStream fileOutputStream;

    PdfExporter(Template data) throws ReportExportException, FileNotFoundException {
        super(data, Ext.PDF);
        LogUtil.i(TAG,
                "PdfExporter: ");
        document = new Document();
        font = new Font(
                Font.FontFamily.TIMES_ROMAN,
                12,
                Font.BOLD,
                new BaseColor(0,
                        0,
                        0));
        fileOutputStream = new FileOutputStream(getFileName());
    }
    @Override
    public boolean export(Template data) {
        LogUtil.i(TAG, "export: ");
        document.addAuthor("Personal Budget");
        document.addCreationDate();
        document.addCreator("Personal Budget");
        document.addSubject(data.getName());
        try {
            PdfWriter.getInstance(
                    document,
                    fileOutputStream);
        } catch (DocumentException e) {
            return false;
        }
        if (data instanceof Basic) {
            return exportData(
                    (Basic) data);
        }
        else if (data instanceof Ledger) {
            return exportData(
                    (Ledger) data);
        }
        else if (data instanceof Shopping) {
            return exportData(
                    (Shopping) data);
        }
        return false;
    }
    private boolean exportData(Basic data) {
        LogUtil.i(TAG,
                "exportData: ");
        document.addKeywords(
                "basic data");
        document.addTitle(
                data.getName());
        //specify column widths
        float[] columnWidths = {1,
                3,
                4,
                4,
                4,
                3,
                3,
                3};
        PdfPTable table =
                new PdfPTable(
                        columnWidths);
        // update table width a percentage
        // of the page width
        table.setWidthPercentage(
                100);
        document.open();
        try {
            document.add(
                    new Paragraph("Records as at," +
                            " " +
                            Func.getDateDayddMMM(
                                    System.currentTimeMillis())));
            document.add(
                    new LineSeparator());
            document.add(
                    new Paragraph("\n"));
            insertCell(table,
                    "ID",
                    Element.ALIGN_RIGHT,
                    0,
                    font);
            insertCell(table,
                    "Category",
                    Element.ALIGN_LEFT,
                    0,
                    font);
            insertCell(table,
                    "Amount",
                    Element.ALIGN_LEFT,
                    0,
                    font);
            insertCell(table,
                    "Time/Date",
                    Element.ALIGN_RIGHT,
                    0,
                    font);
            insertCell(table,
                    "Description",
                    Element.ALIGN_RIGHT,
                    0,
                    font);
            insertCell(table,
                    "Location",
                    Element.ALIGN_RIGHT,
                    0,
                    font);
            insertCell(table,
                    "Receipt",
                    Element.ALIGN_RIGHT,
                    0,
                    font);
            insertCell(table,
                    "Type of Tx",
                    Element.ALIGN_RIGHT,
                    0,
                    font);
            font.setStyle(
                    Font.NORMAL);
            int i = 1;
            for (DayRecords
                    dayRecords : data.getDayRecords()) {
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
                for (Record record :
                        records) {
                    LogUtil.d(TAG,"writeToPDF: Saving record" +
                                    record.getName());
                    insertCell(table,
                            ""+
                                    i,
                            Element.ALIGN_RIGHT,
                            1,
                            font);
                    insertCell(table,
                            record.getName()
                                    .trim().length() == 0 ?
                            " " :
                                    getColumnString(
                                            record.getName()),
                            Element.ALIGN_LEFT,
                            1,
                            font);
                    insertCell(table,
                            record.getAmount() < 0 ?
                            " " :
                                    Func.getMoney(record.getAmount()),
                            Element.ALIGN_RIGHT,
                            1,
                            font);
                    insertCell(table,
                            Func.getDateMPassed(
                                    record.getDate_millis()),
                            Element.ALIGN_RIGHT,
                            1,
                            font);
                    insertCell(table,
                            record.getDescriptionSet()
                                    .getDescription().equals(
                                    Record.DescriptionSet.DEFAULT) ?
                            " " :
                                    getColumnString(
                                            record.getDescriptionSet()
                                                    .getDescription()),
                            Element.ALIGN_RIGHT,
                            1,
                            font);
                    insertCell(table,
                            record.getDescriptionSet()
                                    .getLocationName().equals(
                                    Record.DescriptionSet.DEFAULT) ?
                            " " :
                                    getColumnString(
                                            record.getDescriptionSet()
                                                    .getLocationName()),
                            Element.ALIGN_RIGHT,
                            1,
                            font);
                    insertCell(table,
                            record.getDescriptionSet()
                                    .getReceiptName().equals(
                                    Record.DescriptionSet.DEFAULT) ?
                            " " :
                                    getReceiptName(
                                            record.getDescriptionSet()
                                                    .getReceiptName()),
                            Element.ALIGN_RIGHT,
                            1,
                            font);
                    insertCell(table,
                            record.isIncome() ?
                                    "Credit" :
                                    "Debit",
                            Element.ALIGN_RIGHT,
                            1,
                            font);
                    i++;
                }
            }
            document.add(
                    table);
            document.add(
                    new Paragraph("\n"));
            if (isWriteBalance()) {
                document.add(
                        new Paragraph(
                                " Current Balance , " +
                                        ""+ Func.getMoney(data.getTotal())));
                document.add(
                        new Paragraph("\n"));
                document.add(
                        new Paragraph(("" +
                                " Incomes verses Expenses")
                                .toUpperCase()));
                document.add(
                        new Paragraph(""));
                document.add(
                        new Paragraph(
                                "  Incomes , " +
                                        Func.getMoney(data.getIncomes()) +
                                        ", " +
                        getPercentageIncome(
                                data.getIncomes(),
                                data.getExpenses())));
                int expenses = data.getExpenses();
                document.add(
                        new Paragraph(
                                "  Expenses , " +
                                        Func.getMoney(expenses) +
                                        ", "
                                        +
                        getPercentageExpense(
                                data.getIncomes(),
                                data.getExpenses())));

            }
            document.add(new Paragraph(
                    "\nPrepared by MWallet"
            ));
            LogUtil.d(TAG,
                    "exportData: ");

        } catch(DocumentException de) {
            return false;
        }
        document.close();
        try {
            closeWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    private boolean exportData(Ledger data) {
        LogUtil.i(TAG,
                "exportData: ");
        // step 4 onCreate PDF contents
        document.open();
        LogUtil.i(TAG,
                "exportPdf: Saving to " +
                        getFileName());
        document.addKeywords(
                "ledger data");
        document.addTitle(
                data.getName());
        //specify column widths
        float[] columnWidths = {1,
                3,
                3,
                3,
                3};
        //onCreate PDF table with
        // the given widths
        PdfPTable table =
                new PdfPTable(
                        columnWidths);
        // update table width a percentage
        // of the page width
        table.setWidthPercentage(
                100);
        try {
            Font font = new Font(
                    Font.FontFamily.HELVETICA,
                    14,
                    Font.BOLD,
                    BaseColor.WHITE);

            document.add(new Paragraph(
                    " Ledger as at, " +
                            Func.getDateDayddMMM(
                                    System.currentTimeMillis()),
                    font
            ));
            insertCell(table,
                    "No",
                    Element.ALIGN_RIGHT,
                    1,
                    font);
            insertCell(table,
                    "Credits",
                    Element.ALIGN_RIGHT,
                    1,
                    font);
            insertCell(table,
                    "",
                    Element.ALIGN_RIGHT,
                    1,
                    font);
            insertCell(table,
                    "Debits",
                    Element.ALIGN_RIGHT,
                    1,
                    font);
            insertCell(table,
                    "",
                    Element.ALIGN_RIGHT,
                    1,
                    font);
            // heading by cell merging
            font.setStyle(
                    Font.NORMAL);
            int i = 1;
            for (DayRecords dayRecords :
                    Func.reverse(data.getDayRecords())) {
                document.add(
                        new Paragraph(
                                " " +
                                        dayRecords.getDay()
                                                .getDayName()));
                RecordsToPairsConverter
                        converter =
                        new RecordsToPairsConverter(
                                dayRecords.getRecords());
                try {
                    converter.convert();
                } catch (RecordsEmpty e) {
                    e.printStackTrace();
                    return false;
                }
                ArrayList<RecordPair>
                        recordPairs =
                        converter.getRecordPairs();
                RecordPairList pairList =
                        new RecordPairList(
                                recordPairs);
                pairList.reArrange();
                recordPairs = pairList.getRecordPairs();
                for (RecordPair
                        pair : recordPairs) {
                    if (pair.hasBoth()) {
                        insertCell(table,
                                ""+
                                        i,
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                pair.getIncome().getName(),
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                " " + Func.getMoney(pair.getIncome().getAmount()),
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                pair.getExpense().getName(),
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                " " + Func.getMoney(pair.getExpense().getAmount()),
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                    } else if (pair.hasIncome()) {
                        insertCell(table,
                                ""+
                                        i,
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                pair.getIncome().getName(),
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                " " + Func.getMoney(pair.getIncome().getAmount()),
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                "",
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                "",
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                    } else if (pair.hasExpense()) {
                        insertCell(table,
                                ""+
                                        i,
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                "",
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                "",
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                pair.getExpense().getName(),
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                        insertCell(table,
                                " " + Func.getMoney(pair.getExpense().getAmount()),
                                Element.ALIGN_RIGHT,
                                1,
                                font);
                    }
                    i++;
                }
                document.add(
                        table);
                document.add(
                        new Paragraph(" "));
            }

            document.add(
                    new Paragraph(" "));
            document.add(
                    new Paragraph(" "));

            if (isWriteBalance()) {
                document.add(
                        new Paragraph(
                                "  Current Balance ," +
                                        Func.getMoney(data.getTotal())));
            }
            document.add(
                    new Paragraph(
                            "\nPrepared by Personal Budget"));

            LogUtil.i(TAG,
                    "writeToPDF: closing writer");
            document.close();
            try {
                closeWriter();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG,
                "writeToPDF: closing writer");
        document.close();
        try {
            closeWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean exportData(Shopping data) {
        LogUtil.i(TAG,
                "exportData: ");
        document.addKeywords(
                "shopping data");
        document.addTitle(
                data.getName());
        //specify column widths
        float[] columnWidths = {1,
                5,
                2,
                1};
        //onCreate PDF table with
        // the given widths
        PdfPTable table =
                new PdfPTable(
                        columnWidths);
        // update table width a percentage
        // of the page width
        table.setWidthPercentage(
                100);
        document.open();
        try {
            document.add(
                    new Paragraph(data.getList().getName()+" for," +
                            " " +
                            Func.getDateDayddMMM(
                                    data.getList().getShoppingDate())));
            document.add(
                    new LineSeparator());
            document.add(
                    new Paragraph("\n"));
            insertCell(table,
                    "ID",
                    Element.ALIGN_RIGHT,
                    0,
                    font);
            insertCell(table,
                    "Item",
                    Element.ALIGN_LEFT,
                    0,
                    font);
            insertCell(table,
                    "Cost",
                    Element.ALIGN_LEFT,
                    0,
                    font);
            insertCell(table,
                    "Acquired",
                    Element.ALIGN_RIGHT,
                    0,
                    font);
            font.setStyle(
                    Font.NORMAL);
            int i = 1;
            for (ShoppingList.ListItem item :
                    data.getList().getListItems()) {
                LogUtil.d(TAG,"writeToPDF: Saving record" +
                                item.getItemName());
                insertCell(table,
                        ""+
                                i,
                        Element.ALIGN_RIGHT,
                        1,
                        font);
                String itemName = item.getItemName();
                String[] utils = itemName.split(ShoppingList.AT);
                int quantity = Integer.parseInt(utils[1]);
                int cost = item.getEstimatedCost() / quantity;
                String text = utils[0] +
                        " " +
                        ShoppingList.AT +
                        " (" + quantity +
                        " X " +
                        Func.getMoney(cost) +
                        ") ";
                insertCell(table,
                        text,
                        Element.ALIGN_LEFT,
                        1,
                        font);
                insertCell(table,
                        Func.getMoney(item.getEstimatedCost()),
                        Element.ALIGN_RIGHT,
                        1,
                        font);
                insertCell(table,
                        item.isBought() ?
                                "YES" :
                                "NO",
                        Element.ALIGN_RIGHT,
                        1,
                        font);
                i++;
            }
            document.add(
                    table);
            document.add(
                    new Paragraph("\n"));
            int sum = 0, total = 0;
            for (ShoppingList.ListItem item :
                    data.getList().getListItems()) {
                total += item.getEstimatedCost();
                if (item.isBought()) {
                    sum += item.getEstimatedCost();
                }

            }
            document.add(
                    new Paragraph(
                            " Currently spent" +
                                    " "+
                                    Func.getMoney(sum)+
                                    "\t\t"+
                                    " Total "+
                                    Func.getMoney(total)));
            document.add(new Paragraph(
                    "\nPrepared by Personal Budget"
            ));
            LogUtil.d(TAG,
                    "exportData: ");

        } catch(DocumentException de) {
            return false;
        }
        document.close();
        try {
            closeWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public void closeWriter() throws IOException {
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
    }

    private void insertCell(PdfPTable table,
                            String text,
                            int align,
                            int colspan,
                            Font font){

        //onCreate a new cell with
        // the specified Text add Font
        PdfPCell cell = new PdfPCell(
                new Phrase(
                        text,
                        font));
        //update the cell alignment
        cell.setHorizontalAlignment(
                align);
        //update the cell column span in
        // case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text add
        // you wan to onCreate an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(
                    10f);
        }
        //add the call to the table
        table.addCell(cell);

    }
    private void insertRow(PdfPTable table,
                           Font font,
                           String... text) {
        for (String s : text) {
            insertCell(table,
                    s,
                    Element.ALIGN_LEFT,
                    1,
                    font);
        }
    }
    private String getPercentageIncome(int income,
                                       int expense) {
        int total = income +
                expense;
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
    private String getColumnString(String columnData) {
        if (columnData.contains(",")) {
            return columnData
                    .replaceAll(",",
                    " ");
        }
        return columnData;
    }
    private String getReceiptName(String receiptID) {
        File file = new File(
                receiptID);
        return getColumnString(
                file.getName());
    }



    private class TableBuilder {
        Style style;
        float[] widths;
        PdfPTable createTable() throws DocumentException {
            LogUtil.i(TAG,
                    "createTable: ");
            style = new Style();

            // onCreate 6 column table
            PdfPTable table =
                    new PdfPTable(
                            6);

            // update the width of
            // the table to 100% of page
            table.setWidthPercentage(
                    100);

            // update relative columns width
            table.setWidths(
                    new float[]{0.6f,
                    1.4f,
                            0.8f,
                            0.8f,
                            1.8f,
                            2.6f});

            // ----------------Table
            // Header "Title"----------------
            // font
            Font font = new Font(
                    Font.FontFamily.HELVETICA,
                    14,
                    Font.BOLD,
                    BaseColor.WHITE);
            // onCreate header cell
            PdfPCell cell = new PdfPCell(
                    new Phrase(" - iText PDFTable Example",
                            font));
            // update Column span "1
            // cell = 6 cells width"
            cell.setColspan(
                    6);
            // update style
            style.headerCellStyle(
                    cell);
            // add to table
            table.addCell(cell);

            //-----------------Table
            // Cells Label/Value------------------
            LogUtil.i(TAG,
                    "createTable: first row");
            // 1st Row
            table.addCell(
                    createLabelCell(
                            "Label 1"));
            table.addCell(
                    createValueCell(
                            "Value 1"));
            table.addCell(
                    createLabelCell(
                            "Label 2"));
            table.addCell(
                    createValueCell(
                            "Value 2"));
            table.addCell(
                    createLabelCell(
                            "Label 3"));
            table.addCell(
                    createValueCell(
                            "Value 3"));

            // 2nd Row
            LogUtil.i(TAG,
                    "createTable: second row");
            table.addCell(
                    createLabelCell(
                            "Label 4"));
            table.addCell(
                    createValueCell(
                            "Value 4"));
            table.addCell(
                    createLabelCell(
                            "Label 5"));
            table.addCell(
                    createValueCell(
                            "Value 5"));
            table.addCell(
                    createLabelCell(
                            "Label 6"));
            table.addCell(
                    createValueCell(
                            "Value 6"));
            return table;
        }

        // onCreate cells
        private PdfPCell createLabelCell(String text){
            LogUtil.i(TAG,
                    "createLabelCell: "+text);
            // font
            Font font = new Font(
                    Font.FontFamily.HELVETICA,
                    8,
                    Font.BOLD,
                    BaseColor.BLUE);

            // onCreate cell
            PdfPCell cell = new PdfPCell(
                    new Phrase(text,
                            font));

            // update style
            style.labelCellStyle(
                    cell);
            return cell;
        }

        // onCreate cells
        private PdfPCell createValueCell(String text){
            LogUtil.i(TAG,
                    "createValueCell: "+
                            text);
            // font
            Font font = new Font(
                    Font.FontFamily.HELVETICA,
                    8,
                    Font.NORMAL,
                    BaseColor.BLACK);

            // onCreate cell
            PdfPCell cell =
                    new PdfPCell(
                            new Phrase(text,
                                    font));

            // update style
            style.valueCellStyle(
                    cell);
            return cell;
        }
    }
    private class Style {

        void headerCellStyle(PdfPCell cell){
            LogUtil.i(TAG,
                    "headerCellStyle: ");

            // alignment
            cell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);

            // padding
            cell.setPaddingTop(
                    0f);
            cell.setPaddingBottom(
                    7f);

            // background color
            cell.setBackgroundColor(
                    new BaseColor(0,
                            121,
                            182));

            // border
            cell.setBorder(
                    0);
            cell.setBorderWidthBottom(
                    2f);

        }
        void labelCellStyle(PdfPCell cell){
            LogUtil.i(TAG,
                    "labelCellStyle: ");
            // alignment
            cell.setHorizontalAlignment(
                    Element.ALIGN_LEFT);
            cell.setVerticalAlignment(
                    Element.ALIGN_MIDDLE);

            // padding
            cell.setPaddingLeft(
                    3f);
            cell.setPaddingTop(
                    0f);

            // background color
            cell.setBackgroundColor(
                    BaseColor.LIGHT_GRAY);

            // border
            cell.setBorder(
                    0);
            cell.setBorderWidthBottom(
                    1);
            cell.setBorderColorBottom(
                    BaseColor.GRAY);

            // height
            cell.setMinimumHeight(
                    18f);
        }

        void valueCellStyle(PdfPCell cell){
            LogUtil.i(TAG,
                    "valueCellStyle: ");
            // alignment
            cell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);
            cell.setVerticalAlignment(
                    Element.ALIGN_MIDDLE);

            // padding
            cell.setPaddingTop(
                    0f);
            cell.setPaddingBottom(
                    5f);

            // border
            cell.setBorder(
                    0);
            cell.setBorderWidthBottom(
                    0.5f);

            // height
            cell.setMinimumHeight(
                    18f);
        }
    }
}
