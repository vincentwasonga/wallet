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

package com.octopus.wallet.s;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;

import com.octopus.wallet.App;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.h.scdlr.DailyReminder;
import com.octopus.wallet.m.h.scdlr.ResetReminder;
import com.octopus.wallet.m.h.scdlr.WeekReportReminder;
import com.octopus.wallet.m.h.utl.BitmapUtil;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.p.db.AppDatabase;
import com.octopus.wallet.m.p.fl.expoimpo.data.Basic;
import com.octopus.wallet.m.p.fl.expoimpo.exp.Format;
import com.octopus.wallet.m.p.fl.fmodels.FExportData;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import me.yoctopus.cac.pref.Preferences;
import com.octopus.wallet.m.pb.Migration;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.r.BReceiver;
import com.octopus.wallet.u.a.mo.ExportActivity;

import java.util.Date;
import java.util.List;

import me.yoctopus.cac.notif.Notification;
import me.yoctopus.cac.tx.Tx;


public class BService extends Service {
    private final IBinder binder = new PBBinder();
    private App app;
    private String message;
    private Notification notification;
    private AppDatabase database;

    public BService() {

    }

    public static void setAlarms(Context context) {
        context = context.getApplicationContext();
        new DailyReminder(context).schedule();
        new WeekReportReminder(context).schedule();
        new ResetReminder(context).schedule();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case BReceiver.ACTION_WEAK_REMINDER: {
                    if (intent.hasExtra(BReceiver.MESSAGE)) {
                        this.message = intent.getStringExtra(
                                BReceiver.MESSAGE);
                    } else {
                        this.message = "Weekly Report available";
                    }
                    app = (App) getApplication();
                    notification = new Notification(
                            app.getBudgetContext());
                    doAction(app.getBudgetContext());
                    break;
                }
                case BReceiver.ACTION_BOOT_COMPLETED: {
                    app = (App) getApplication();
                    setAlarms(app.getBudgetContext());
                    com.octopus.wallet.m.pb.Notification notification =
                            new com.octopus.wallet.m.pb.Notification("Restart",
                                    "Service started successfully", new Date());
                    new AppDatabase(getApplicationContext()).save(notification);
                }
            }
        }
        return START_STICKY;
    }

    private void doAction(final Context context) {
        database = new AppDatabase(context);
        saveRecordsToFile(context);
    }

    private void saveRecordsToFile(Context context) {
        Preferences appPreferences =
                new Preferences(context);
        String name;
        try {
            name = appPreferences.getPreference(
                    new Pref<>("backup_name", "Weekly Report"));
        } catch (InvalidPreferenceType e) {
            e.printStackTrace();
            name = "Weekly Report";
        }
        AccountManager manager = new AccountManager(context);
        List<Record> records = database.getRecords();
        Basic basicData =
                new Basic(
                        name,
                        true,
                        Func.getDayRecords(records),
                        manager.getInfo().getTotalBal(),
                        getRecordTotal(records,
                                0),
                        getRecordTotal(records,
                                1));
        ExportActivity.SaveFile saveFile = new ExportActivity.SaveFile(context,
                100, basicData, Format.PDF);
        saveFile.setOnComplete(new Tx.OnComplete<FExportData>() {
            @Override
            public void onComplete(int id, FExportData fExportData) {
                BService.this.notify("Weekly report available");
                Migration migration = new Migration(Migration.TYPE_FILE,
                        "records saved to file",
                        new Date());
                database.save(migration);
            }
        });
        saveFile.execute();

    }

    private int getRecordTotal(List<Record> records, int type) {
        int sum = 0;
        switch (type) {
            case 0 :{
                for (Record record : records) {
                    if (record.isIncome()) {
                        sum += record.getAmount();
                    }
                }
            }
            case 1 : {
                for (Record record : records) {
                    if (record.isExpense()) {
                        sum += record.getAmount();
                    }
                }
            }
        }
        return sum;
    }

    public void notify(String message) {
        Bitmap bitmap = BitmapUtil.getBitmap(getApplicationContext(),
                R.drawable.budget);

        if (!this.message.isEmpty()) {
            message = this.message;
        }
        notification.showNotification(bitmap, "Wallet", message,
                R.drawable.app_notification_icon, null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class PBBinder extends Binder {
        public BService getService() {
            return BService.this;
        }
    }
}
