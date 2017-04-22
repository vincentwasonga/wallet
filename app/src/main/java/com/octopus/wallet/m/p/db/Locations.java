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

import com.octopus.wallet.m.pb.Location;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;

public class Locations extends Model<Location> {
    private static final String tb_name = "lctns";
    public static Column<Integer> id;
    public static Column<String> name;
    public static Column<Long> lon;
    public static Column<Long> lat;
    static {
        id = new Column<>("id", Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        name = new Column<>("nm", Column.Type.TEXT.NULLABLE(), 1);
        lon = new Column<>("ln", Column.Type.REAL.NOT_NULL(), 2);
        lat = new Column<>("lt", Column.Type.REAL.NOT_NULL(), 3);
    }

    @Override
    public List<Column> onGetColumns() {
        List<Column> columns = new ArrayList<>();
        columns.add(lat);
        columns.add(id);
        columns.add(name);
        columns.add(lon);
        return columns;
    }

    @Override
    public String onGetName() {
        return tb_name;
    }

    @Override
    public Location onLoad(Cursor cursor) {
        Location location = new Location(cursor.getLong(lon.getIndex()),
                cursor.getLong(lat.getIndex()));
        location.setName(cursor.getString(name.getIndex()));
        location.setId(cursor.getInt(id.getIndex()));
        return location;
    }

    @Override
    public ContentValues onGetValues(Location location) {
        ContentValues values = new ContentValues();
        values.put(name.getName(), location.getName());
        values.put(lon.getName(), location.getLongitude());
        values.put(lat.getName(), location.getLatitude());
        return values;
    }


}
