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

import com.octopus.wallet.m.pb.Budget;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;

/**
 * Created by yoctopus on 3/19/17.
 */

public class Budgets extends Model<Budget> {
    private static final String tb_name = "bgts";
    public static Column<Integer> id;
    public static Column<String> name;
    public static Column<Integer> cost;
    public static Column<Long> date;
    public static Column<Long> period;
    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        name = new Column<>("nm",
                Column.Type.TEXT.NOT_NULL(), 1);
        cost = new Column<>("ct",
                Column.Type.INTEGER.DEFAULT(0), 2);
        date = new Column<>("dt",
                Column.Type.INTEGER.NOT_NULL(), 3);
        period = new Column<>("pd",
                Column.Type.INTEGER.NULLABLE(), 4);
    }
    @Override
    public List<Column> onGetColumns() {
        List<Column> columns = new ArrayList<>();
        columns.add(id);
        columns.add(name);
        columns.add(cost);
        columns.add(date);
        columns.add(period);
        return columns;
    }

    @Override
    public String onGetName() {
        return tb_name;
    }

    @Override
    public Budget onLoad(Cursor cursor) {
        Budget budget = new Budget(cursor.getString(name.getIndex()),
                cursor.getInt(cost.getIndex()),
                cursor.getLong(date.getIndex()));
        budget.setId(cursor.getInt(id.getIndex()));
        budget.setPeriod(cursor.getLong(period.getIndex()));
        return budget;
    }

    @Override
    public ContentValues onGetValues(Budget budget) {
        ContentValues values = new ContentValues();
        values.put(name.getName(), budget.getName());
        values.put(cost.getName(), budget.getCost());
        values.put(date.getName(), budget.getDate());
        values.put(period.getName(), budget.getPeriod());
        return values;
    }
}
