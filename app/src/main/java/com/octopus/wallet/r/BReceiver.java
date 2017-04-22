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

package com.octopus.wallet.r;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.PowerManager;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.h.ntnt.INTENT_ACTION;
import com.octopus.wallet.m.h.utl.BitmapUtil;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import me.yoctopus.cac.pref.Preferences;
import com.octopus.wallet.s.BService;
import com.octopus.wallet.u.a.st.SplashActivity;

import me.yoctopus.cac.notif.Notification;
import me.yoctopus.cac.util.Scheduler;

public class BReceiver extends BroadcastReceiver {
    public static final String ACTION_REMINDER_ADD_EXPENSES =
            INTENT_ACTION.DAILY_REMINDER_ACTION;
    public static final String ACTION_SHOPPING_REMINDER =
            INTENT_ACTION.SHOPPING_ACTION;
    public static final String ACTION_RESET_EXPENDITURE =
            INTENT_ACTION.DAILY_RESET_ACTION;
    public static final String ACTION_WEAK_REMINDER =
            INTENT_ACTION.WEEK_REMINDER_ACTION;
    public static final String ACTION_BOOT_COMPLETED =
            INTENT_ACTION.BOOT_COMPLETE_ACTION;
    public static final String MESSAGE = Scheduler.MESSAGE_TITLE;
    private Notification notification;

    public BReceiver() {
    }

    @Override
    public void onReceive(Context context,
                          Intent intent) {
        PowerManager pm = (PowerManager)
                context.getSystemService(
                        Context.POWER_SERVICE);
        PowerManager.WakeLock wl =
                pm.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK,
                        "");
        wl.acquire();
        String message = "Message from Personal Budget";
        if (intent.hasExtra(Scheduler.MESSAGE_TITLE)) {
            message = intent.getStringExtra(
                    Scheduler.MESSAGE_TITLE);
        }
        switch (intent.getAction()) {
            case ACTION_REMINDER_ADD_EXPENSES: {
                Preferences preferences =
                        new Preferences(context);
                boolean reminder;
                try {
                    reminder = preferences.getPreference(
                            new Pref<>("reminder", true));
                    if (reminder) {
                        notification = new Notification(context);
                        notify(context, message);
                    }
                } catch (InvalidPreferenceType e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_RESET_EXPENDITURE: {
                AccountManager manager = new AccountManager(context);
                manager.updateSpent(0);
                break;
            }
            case ACTION_SHOPPING_REMINDER: {
                notify(context, message);
                break;
            }
            case ACTION_WEAK_REMINDER: {
                Intent intent1 = new Intent(context, BService.class);
                intent1.setAction(ACTION_WEAK_REMINDER);
                intent1.putExtra(MESSAGE, message);
                context.startService(intent1);
                break;
            }
            case ACTION_BOOT_COMPLETED: {
                Intent intent1 = new Intent(context,
                        BService.class);
                intent1.setAction(ACTION_BOOT_COMPLETED);
                context.startService(intent1);
            }
        }
        wl.release();
    }

    private void notify(Context context,
                        String message) {
        Bitmap bitmap = BitmapUtil.getBitmap(context,
                R.drawable.budget);
        notification = new Notification(context);
        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.showNotification(bitmap, "Wallet", message,
                R.drawable.app_notification_icon, pIntent);
    }
}
