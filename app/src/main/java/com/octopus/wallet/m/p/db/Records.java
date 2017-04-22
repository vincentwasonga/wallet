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

package com.octopus.wallet.m.p.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.octopus.wallet.m.pb.Record;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;

class Records extends Model<Record> {
    public static Column<Integer> id;
    public static Column<Integer> amount;
    public static Column<Long> date;
    public static Column<String> description;
    public static Column<String> location;
    public static Column<String> receipt;
    public static Column<String> type;
    public static Column<String> name;

    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        amount = new Column<>("amt",
                Column.Type.INTEGER.NOT_NULL(),
                1);
        date = new Column<>("dt",
                Column.Type.INTEGER.NOT_NULL(),
                2);
        description = new Column<>("dct",
                Column.Type.TEXT.NULLABLE(),
                3);
        location = new Column<>("lct",
                Column.Type.TEXT.NULLABLE(),
                4);
        receipt = new Column<>("rct",
                Column.Type.TEXT.NULLABLE(),
                5);
        type = new Column<>("typ",
                Column.Type.TEXT.NULLABLE(),
                6);
        name = new Column<>("nm",
                Column.Type.TEXT.NULLABLE(),
                7);
    }

    @Override
    public List<Column> onGetColumns() {
        ArrayList<Column> columns =
                new ArrayList<>();
        columns.add(location);
        columns.add(receipt);
        columns.add(id);
        columns.add(amount);
        columns.add(date);
        columns.add(description);
        columns.add(type);
        columns.add(name);
        return columns;
    }

    @Override
    public String onGetName() {
        return "rcd";
    }

    @Override
    public Record onLoad(Cursor cursor) {
        int id = cursor.getInt(
                Records.id.getIndex());
        String type, name;
        try {
            type = cursor.getString(Records.type.getIndex());
        }
        catch (IllegalStateException e) {
            type = "";
        }
        try {
            name = cursor.getString(Records.name.getIndex());
        }
        catch (IllegalStateException e) {
            name = "";
        }

        int amount = cursor.getInt(
                Records.amount.getIndex());
        long date = cursor.getLong(
                Records.date.getIndex());
        String description = cursor.getString(
                Records.description.getIndex());
        String locationName = cursor.getString(
                Records.location.getIndex());
        String receiptName = cursor.getString(
                Records.receipt.getIndex());
        Record.DescriptionSet descriptionSet =
                new Record.DescriptionSet(description,
                        locationName,
                        receiptName);
        Record.Category category = new Record.Category(type,
                name);
        return new Record(id,
                category,
                amount,
                date,
                descriptionSet);
    }

    @Override
    public ContentValues onGetValues(Record record) {
        ContentValues values = new ContentValues();
        values.put(type.getName(), record.getType());
        values.put(name.getName(), record.getName());
        values.put(amount.getName(),
                record.getAmount());
        values.put(date.getName(),
                (record.getDate_millis() != Record.DEFAULT_MILLIS ?
                        record.getDate_millis() :
                        System.currentTimeMillis()));
        if (record.getDescriptionSet() != null) {
            String description = !record.getDescriptionSet().getDescription()
                    .equals(Record.DescriptionSet.DEFAULT)
                    ? record.getDescriptionSet().getDescription() :
                    Record.DescriptionSet.DEFAULT;
            String locationName = !record.getDescriptionSet().getLocationName()
                    .equals(Record.DescriptionSet.DEFAULT)
                    ? record.getDescriptionSet().getLocationName() :
                    Record.DescriptionSet.DEFAULT;
            String receiptName = !record.getDescriptionSet().getReceiptName()
                    .equals(Record.DescriptionSet.DEFAULT)
                    ? record.getDescriptionSet().getReceiptName() :
                    Record.DescriptionSet.DEFAULT;
            values.put(
                    Records.description.getName(),
                    description);
            values.put(
                    Records.location.getName(),
                    locationName);
            values.put(
                    Records.receipt.getName(),
                    receiptName);

        } else {
            values.put(
                    description.getName(),
                    Record.DescriptionSet.DEFAULT);
            values.put(
                    location.getName(),
                    Record.DescriptionSet.DEFAULT);
            values.put(
                    receipt.getName(),
                    Record.DescriptionSet.DEFAULT);
        }
        return values;
    }
}
