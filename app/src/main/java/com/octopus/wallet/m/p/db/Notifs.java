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

import com.octopus.wallet.m.pb.Notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.Model;

class Notifs extends Model<Notification> {
    public static Column<Integer> id;
    public static Column<String> title;
    public static Column<String> description;
    public static Column<Long> date;
    static {
        id = new Column<>("id",
                Column.Type.INTEGER.PRIMARY_KEY_AUTOINCREMENT());
        title = new Column<>("ttl",
                Column.Type.TEXT.NOT_NULL(),
                1);
        description = new Column<>("dst",
                Column.Type.TEXT.NULLABLE(),
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
        columns.add(title);
        columns.add(description);
        columns.add(date);
        return columns;
    }

    @Override
    public String onGetName() {
        return "ntf";
    }

    @Override
    public Notification onLoad(Cursor cursor) {
        int id = cursor.getInt(Notifs.id.getIndex());
        String title = cursor.getString(Notifs.title.getIndex());
        String description = cursor.getString(Notifs.description.getIndex());
        long date = cursor.getLong(Notifs.date.getIndex());
        Notification notification =
                new Notification(title,
                description,
                new Date(date));
        notification.setId(id);
        return notification;
    }

    @Override
    public ContentValues onGetValues(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(title.getName(),
                notification.getTitle());
        values.put(description.getName(),
                notification.getDescription());
        values.put(date.getName(),
                notification.getDate().getTime());
        return values;
    }
}
