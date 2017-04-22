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

import com.octopus.wallet.m.pb.RecurringPayment;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;

public class RecurringPayments extends Model<RecurringPayment> {
    private static final String tb_name = "rcpymts";
    public static Column<Integer> id;
    public static Column<String> name;
    public static Column<Long> startDate;
    public static Column<Long> duration;
    public static Column<Integer> amount;
    public static Column<String> description;
    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        name = new Column<>("nm",
                Column.Type.TEXT.NOT_NULL(),
                1);
        startDate = new Column<>("st",
                Column.Type.INTEGER.NOT_NULL(),
                2);
        duration = new Column<>("du",
                Column.Type.INTEGER.NOT_NULL(),
                3);
        amount = new Column<>("am",
                Column.Type.INTEGER.DEFAULT(0),
                4);
        description = new Column<String>("ds",
                Column.Type.TEXT.NULLABLE(),
                5);
    }
    @Override
    public List<Column> onGetColumns() {
        List<Column> columns = new ArrayList<>();
        columns.add(id);
        columns.add(name);
        columns.add(duration);
        columns.add(amount);
        columns.add(startDate);
        columns.add(description);
        return columns;
    }

    @Override
    public String onGetName() {
        return tb_name;
    }

    @Override
    public RecurringPayment onLoad(Cursor cursor) {
        RecurringPayment payment = new RecurringPayment(
                cursor.getString(name.getIndex()),
                cursor.getLong(startDate.getIndex()),
                cursor.getLong(duration.getIndex()));
        payment.setId(cursor.getInt(id.getIndex()));
        payment.setAmount(cursor.getInt(amount.getIndex()));
        payment.setDescription(cursor.getString(description.getIndex()));
        return payment;
    }

    @Override
    public ContentValues onGetValues(RecurringPayment recurringPayment) {
        ContentValues values = new ContentValues();
        values.put(name.getName(), recurringPayment.getName());
        values.put(startDate.getName(), recurringPayment.getStartDate());
        values.put(duration.getName(), recurringPayment.getDuration());
        values.put(amount.getName(), recurringPayment.getAmount());
        values.put(description.getName(), recurringPayment.getDescription());
        return values;
    }
}
