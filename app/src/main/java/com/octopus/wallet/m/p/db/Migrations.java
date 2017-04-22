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

import com.octopus.wallet.m.pb.Migration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;


class Migrations extends Model<Migration> {
    public static Column<Integer> id;
    public static Column<Integer> type;
    public static Column<String> description;
    public static Column<Long> date;
    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        type = new Column<>("tp",
                Column.Type.TEXT.NOT_NULL(),
                1);
        description = new Column<>("dst",
                Column.Type.TEXT.NOT_NULL(),
                2);
        date = new Column<>("dt",
                Column.Type.INTEGER.NOT_NULL(),
                3);
    }
    @Override
    public List<Column> onGetColumns() {
        List<Column> columns =
                new ArrayList<>();
        columns.add(id);
        columns.add(type);
        columns.add(description);
        columns.add(date);
        return columns;
    }

    @Override
    public String onGetName() {
        return "mgt";
    }

    @Override
    public Migration onLoad(Cursor cursor) {
        int id = cursor.getInt(Migrations.id.getIndex());
        int type = cursor.getInt(Migrations.type.getIndex());
        String description = cursor.getString(Migrations.description.getIndex());
        long date = cursor.getLong(Migrations.date.getIndex());
        Migration migration = new Migration(type,
                description,
                new Date(date));
        migration.setId(id);
        return migration;
    }

    @Override
    public ContentValues onGetValues(Migration migration) {
        ContentValues values
                = new ContentValues();
        values.put(type.getName(),
                migration.getType());
        values.put(description.getName(),
                migration.getDescription());
        values.put(date.getName(),
                migration.getDate().getTime());
        return values;
    }
}
