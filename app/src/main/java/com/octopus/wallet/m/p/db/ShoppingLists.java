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

import com.octopus.wallet.m.pb.ShoppingList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;


class ShoppingLists extends Model<ShoppingList> {
    public static Column<Integer> id;
    public static Column<Integer> number;
    public static Column<String> name;
    public static Column<Long> date;

    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        number = new Column<>("nmb",
                Column.Type.INTEGER.DEFAULT(0),
                1);
        name = new Column<>("nm",
                Column.Type.TEXT.NULLABLE(),
                2);
        date = new Column<>("dt",
                Column.Type.INTEGER.NULLABLE(),
                3);
    }

    ShoppingLists() {
        setPrimary(id);
    }

    @Override
    public List<Column> onGetColumns() {
        List<Column> columns =
                new ArrayList<>();
        columns.add(id);
        columns.add(number);
        columns.add(name);
        columns.add(date);
        return columns;
    }

    @Override
    public String onGetName() {
        return "spl";
    }

    @Override
    public ShoppingList onLoad(Cursor c) {
        int id = c.getInt(ShoppingLists.id.getIndex());
        String name = c.getString(ShoppingLists.name.getIndex());
        long date = c.getLong(ShoppingLists.date.getIndex());
        ShoppingList list = new ShoppingList(name);
        list.setId(id);
        list.setShoppingDate(new Date(date));
        return list;
    }

    @Override
    public ContentValues onGetValues(ShoppingList shoppingList) {
        ContentValues values = new ContentValues();
        values.put(ShoppingLists.name.getName(),
                shoppingList.getName());
        values.put(ShoppingLists.date.getName(),
                shoppingList.getShoppingDate().getTime());
        return values;
    }
}
