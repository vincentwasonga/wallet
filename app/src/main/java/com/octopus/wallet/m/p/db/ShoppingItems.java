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
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;


class ShoppingItems extends Model<ShoppingList.ListItem> {
    public static Column<Integer> id;
    public static Column<Integer> number;
    public static Column<String> name;
    public static Column<Integer> estAmount;
    public static Column<Integer> actAmount;
    public static Column<String> bought;
    private static String ACTED_UPON = "Y";

    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        number = new Column<>("nmb",
                Column.Type.INTEGER.DEFAULT(0),
                1);
        name = new Column<>("nm",
                Column.Type.TEXT.NOT_NULL(),
                2);
        estAmount = new Column<>("est",
                Column.Type.INTEGER.DEFAULT(0),
                3);
        actAmount = new Column<>("act",
                Column.Type.INTEGER.DEFAULT(0),
                4);
        bought = new Column<>("bt",
                Column.Type.TEXT.NULLABLE(),
                5);
    }

    @Override
    public List<Column> onGetColumns() {
        List<Column> columns =
                new ArrayList<>();
        columns.add(id);
        columns.add(number);
        columns.add(name);
        columns.add(estAmount);
        columns.add(actAmount);
        columns.add(bought);
        return columns;
    }

    @Override
    public String onGetName() {
        return "spt";
    }

    @Override
    public ShoppingList.ListItem onLoad(Cursor cursor) {
        String name = cursor.getString(ShoppingItems.name.getIndex());
        int estimatedCost = cursor.getInt(estAmount.getIndex());
        int actualCost = cursor.getInt(actAmount.getIndex());
        String s = cursor.getString(bought.getIndex());
        boolean bought = s.equalsIgnoreCase(ACTED_UPON);
        int id = cursor.getInt(ShoppingItems.id.getIndex());
        int num = cursor.getInt(number.getIndex());
        ShoppingList.ListItem item =
                new ShoppingList.ListItem(name,
                        estimatedCost,
                        actualCost,
                        bought,
                        id);
        item.setNumber(num);
        return item;
    }

    @Override
    public ContentValues onGetValues(ShoppingList.ListItem listItem) {
        ContentValues values = new ContentValues();
        values.put(number.getName(),
                listItem.getNumber());
        values.put(name.getName(),
                listItem.getItemName());
        values.put(estAmount.getName(),
                listItem.getEstimatedCost());
        values.put(actAmount.getName(),
                listItem.getActualCost());
        String s;
        if (listItem.isBought()) {
            s = ACTED_UPON;
        } else {
            s = "N";
        }
        values.put(bought.getName(),
                s);
        return values;
    }
}
