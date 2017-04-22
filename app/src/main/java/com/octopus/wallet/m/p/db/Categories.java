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

class Categories extends Model<Record.Category> {
    public static Column<Integer> id;
    public static Column<String> type;
    public static Column<String> name;
    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        type = new Column<>("typ",
                Column.Type.TEXT.NOT_NULL(),
                1);
        name = new Column<>("nm",
                Column.Type.TEXT.NOT_NULL(),
                2);
    }
    @Override
    public List<Column> onGetColumns() {
        List<Column> columns =
                new ArrayList<>();
        columns.add(id);
        columns.add(type);
        columns.add(name);
        return columns;
    }

    @Override
    public String onGetName() {
        return "ctg";
    }

    @Override
    public Record.Category onLoad(Cursor cursor) {
        String type = cursor.getString(Categories.type.getIndex());
        String name = cursor.getString(Categories.name.getIndex());
        int id = cursor.getInt(Categories.id.getIndex());
        Record.Category category = new Record.Category(type,
                name);
        category.setId(id);
        return category;
    }

    @Override
    public ContentValues onGetValues(Record.Category category) {
        ContentValues values = new ContentValues();
        values.put(Categories.type.getName(),
                category.getType());
        values.put(Categories.name.getName(),
                category.getName());
        return values;
    }
}
