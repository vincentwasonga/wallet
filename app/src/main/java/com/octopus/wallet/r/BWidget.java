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
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.u.a.st.SplashActivity;

import me.yoctopus.cac.util.LogUtil;

public class BWidget extends AppWidgetProvider {
    private static AccountManager manager;
    private static String TAG = LogUtil.makeTag(BWidget.class);

    static void updateAppWidget(Context context,
                                AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        LogUtil.d(TAG,
                "updateAppWidget");
        Intent intent = new Intent(context,
                SplashActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                context,
                0,
                intent,
                0);
        CharSequence widgetText =
                Func.getMoney(
                        manager.getInfo().getTotalBal());
        RemoteViews views =
                new RemoteViews(
                        context.getPackageName(),
                        R.layout.balance_widget);
        views.setTextViewText(
                R.id.appwidget_text,
                widgetText);
        views.setOnClickPendingIntent(
                R.id.appwidget_text,
                pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId,
                views);
    }

    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context,
                appWidgetManager,
                appWidgetIds);
        manager = new AccountManager(context);
        for (int appWidgetId : appWidgetIds) {
            LogUtil.d(TAG,
                    "onProgress" +
                            appWidgetId);
            updateAppWidget(context,
                    appWidgetManager,
                    appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }
}

