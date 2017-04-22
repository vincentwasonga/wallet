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

import com.octopus.wallet.m.pb.Debt;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;

/**
 * Created by yoctopus on 3/19/17.
 */

public class Debts extends Model<Debt> {
    private static final String tb_name = "dbts";
    public static Column<Integer> id;
    public static Column<Integer> type;
    public static Column<Integer> name;
    public static Column<String> description;
    public static Column<Integer> amount;
    public static Column<Integer> payedBack;
    public static Column<Long> effectedDate;
    public static Column<Long> expectedPayback;
    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        type = new Column<>("tp",
                Column.Type.INTEGER.NOT_NULL(), 1);
        name = new Column<>("nm",
                Column.Type.TEXT.NOT_NULL(), 2);
        description = new Column<>("ds",
                Column.Type.TEXT.NOT_NULL(), 3);
        amount = new Column<>("am",
                Column.Type.INTEGER.DEFAULT(0), 4);
        payedBack = new Column<>("pd",
                Column.Type.INTEGER.DEFAULT(0), 5);
        effectedDate = new Column<>("efd",
                Column.Type.INTEGER.NOT_NULL(), 6);
        expectedPayback = new Column<>("exp",
                Column.Type.INTEGER.NULLABLE(), 7);
    }
    @Override
    public List<Column> onGetColumns() {
        List<Column> columns =
                new ArrayList<>();
        columns.add(id);
        columns.add(type);
        columns.add(name);
        columns.add(description);
        columns.add(payedBack);
        columns.add(effectedDate);
        columns.add(amount);
        columns.add(expectedPayback);
        return columns;
    }

    @Override
    public String onGetName() {
        return tb_name;
    }

    @Override
    public Debt onLoad(Cursor cursor) {
        Debt debt = new Debt(cursor.getInt(type.getIndex()),
                cursor.getString(name.getIndex()),
                cursor.getString(description.getIndex()),
                cursor.getInt(amount.getIndex()),
                cursor.getInt(effectedDate.getIndex()));
        debt.setId(cursor.getInt(id.getIndex()));
        debt.setExpectedPayback(cursor.getInt(expectedPayback.getIndex()));
        debt.setPayedBack(cursor.getInt(payedBack.getIndex()));
        return debt;
    }

    @Override
    public ContentValues onGetValues(Debt debt) {
        ContentValues values = new ContentValues();
        values.put(type.getName(), debt.getType());
        values.put(name.getName(), debt.getName());
        values.put(description.getName(), debt.getDescription());
        values.put(amount.getName(), debt.getAmount());
        values.put(payedBack.getName(), debt.getPayedBack());
        values.put(effectedDate.getName(), debt.getEffectedDate());
        values.put(expectedPayback.getName(), debt.getExpectedPayback());
        return values;
    }
}
