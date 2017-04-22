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

import com.octopus.wallet.m.pb.Currency;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;

/**
 * Created by yoctopus on 3/20/17.
 */

public class Currencies extends Model<Currency> {
    private static final String tb_name = "crcs";
    public static Column<Integer> id;
    public static Column<String> code;
    public static Column<String> country;
    public static Column<Float> rate;
    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        code = new Column<>("cd",
                Column.Type.TEXT.NOT_NULL(), 1);
        country = new Column<>("ct",
                Column.Type.TEXT.NOT_NULL(), 2);
        rate = new Column<>("rt",
                Column.Type.REAL.NOT_NULL(), 3);
    }
    @Override
    public List<Column> onGetColumns() {
        List<Column> columns = new ArrayList<>();
        columns.add(id);
        columns.add(code);
        columns.add(country);
        columns.add(rate);
        return columns;
    }

    @Override
    public String onGetName() {
        return tb_name;
    }

    @Override
    public Currency onLoad(Cursor cursor) {
        Currency currency = new Currency(cursor.getString(code.getIndex()),
                cursor.getString(country.getIndex()),
                cursor.getFloat(rate.getIndex()));
        currency.setId(cursor.getInt(id.getIndex()));
        return currency;
    }

    @Override
    public ContentValues onGetValues(Currency currency) {
        ContentValues values = new ContentValues();
        values.put(code.getName(), currency.getCode());
        values.put(country.getName(), currency.getCountry());
        values.put(rate.getName(), currency.getRate());
        return values;
    }
}
