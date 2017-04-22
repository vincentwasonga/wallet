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

package com.octopus.wallet.u.a;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.octopus.wallet.App;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.utl.BitmapUtil;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.p.db.AppDatabase;
import com.octopus.wallet.m.p.fl.Dir;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.v.a.ActivityAnimator;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import me.yoctopus.cac.anim.Anim;
import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.anim.Animator;
import me.yoctopus.cac.notif.Duration;
import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.notif.Notification;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import me.yoctopus.cac.pref.Preferences;
import me.yoctopus.cac.tx.Tx;
import me.yoctopus.cac.util.LogUtil;


public class BActivity extends AppCompatActivity {
    private String TAG = LogUtil.makeTag(BActivity.class);
    private boolean hasBackButton = false;
    private Notification notification;
    private App app;
    private Animator animator;
    private Handler handler;
    private AppDatabase database;
    private Preferences preferences;
    private AccountManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        app = (App) getApplication();
        notification = new Notification(this);
        animator = new Animator();
        handler = new Handler();
        database = new AppDatabase(this);
        preferences = new Preferences(this);
        manager = new AccountManager(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        LogUtil.i(TAG, "onPostCreate");
        super.onPostCreate(savedInstanceState);
        if (isHasBackButton()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

    }
    public SList<DayRecords> getDayRecords() {
        return Func.getDayRecords(getDatabase().getRecords());
    }
    public SList<Record.Category> getCategories() {
        return sortDescending(getDatabase().getCategories());
    }


    private SList<Record.Category> sortDescending(List<Record.Category> list) {
        Collections.sort(list, new Comparator<Record.Category>() {
            @Override
            public int compare(Record.Category o1, Record.Category o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return new SList<>(list);
    }

    public <T, X> void transact(Tx<T, X> tx,
                                Tx.OnComplete<T> onComplete) {
        tx.setOnComplete(onComplete);
        tx.execute();
    }

    public <T, X> void transact(Tx<T, X> tx,
                                Tx.OnComplete<T> onComplete,
                                int delay) {
        tx.setOnComplete(onComplete);
        tx.execute(delay);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.i(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public View getView(@IdRes int id) {
        return findViewById(id);
    }
    public void prepSpinner(Spinner spinner,
                            List<String> items,
                            final SpinnerChoice choice){
        ArrayAdapter<String> startAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        items);
        startAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {
                        choice.onSelected(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        choice.onNotSelected();
                    }
                });
        spinner.setAdapter(startAdapter);

    }
    public interface SpinnerChoice {
        void onSelected(int id);
        void onNotSelected();
    }


    public void notifyToast(String message) {
        notification.showToast(message);
    }

    public void notifyToast(String message,
                            Duration duration) {
        notification.showToast(message, duration);
    }
    public void notifyDialog(String title,
                             String message) {
        notification.showDialog(title, message, null, null, null);
    }


    public void notifyDialog(String title,
                             String message,
                             NDialog.DButton positive,
                             NDialog.DButton negative) {
        notification.showDialog(title, message, positive, negative, null);
    }

    public void notifyDialog(String title,
                             String message,
                             NDialog.DButton positive,
                             NDialog.DButton negative,
                             NDialog.OnAnswer listener) {
        notification.showDialog(title, message, positive, negative, null, listener);
    }

    public void notifyProgress(String message) {
        notification.showProgress(message);
    }

    public void notifySound(String sourceFile) {
        notification.soundTone(sourceFile);
    }

    public void notifyNotificationBar(String message,
                                      PendingIntent pendingIntent) {
        Boolean notif;
        try {
            notif = preferences.getPreference(
                    new Pref<>("notification_bar", false));
            if (!notif) {
                return;
            }
        } catch (InvalidPreferenceType e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapUtil.getBitmap(this, R.drawable.budget);
        notification.showNotification(bitmap, "Wallet", message,
                R.drawable.app_notification_icon, pendingIntent);
        com.octopus.wallet.m.pb.Notification notification =
                new com.octopus.wallet.m.pb.Notification("Message",
                        message,
                        new Date());
        database.save(notification);
    }

    public void notifyNotificationBar(String message) {
        Boolean notif;
        try {
            notif = preferences.getPreference(
                    new Pref<>("notification_bar", false));
            if (!notif) {
                return;
            }
        } catch (InvalidPreferenceType e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapUtil.getBitmap(this, R.drawable.budget);
        notification.showNotification(bitmap, "Wallet", message,
                R.drawable.app_notification_icon, null);
        com.octopus.wallet.m.pb.Notification notification =
                new com.octopus.wallet.m.pb.Notification("Message",
                        message,
                        new Date());
        database.save(notification);
    }

    public void notifyVibration() {
        notification.vibrate();
    }

    public void animateActivity() {
        animator = new ActivityAnimator(this);
        animator.animate();
    }

    public void shakeView(@NonNull View view) {
        animator = new Animator(view,
                Anim.Attention.shake(),
                AnimDuration.small());
        animator.animate();
    }

    public void shakeView(@IdRes int id) {
        shakeView(getView(id));
    }

    public Intent getIntent(@NonNull Class<? extends Activity> activity) {
        return new Intent(this, activity);
    }

    public String getAppFolder() {
        return Dir.Main.getName();
    }

    public void startActivity(@NonNull final Class<? extends Activity> activity,
                              final Bundle args) {
        Runnable activityRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent(activity);
                intent.putExtra("bundle", args);
                startActivity(intent);
            }
        };
        getHandler().postDelayed(activityRunnable, 500);
    }

    public void executeRunnable(@NonNull Runnable runnable) {
        getHandler().post(runnable);
    }

    public void executeRunnable(@NonNull Runnable runnable,
                                long millis) {
        getHandler().postDelayed(runnable, millis);
    }

    public void dismissNotification() {
        getNotification().dismiss();
    }

    public Context getBudgetContext() {
        assert getApp().getBudgetContext() != null;
        return getApp().getBudgetContext();
    }

    public BActivity getActivity() {
        return this;
    }

    public boolean isHasBackButton() {
        return hasBackButton;
    }

    public void setHasBackButton(boolean hasBackButton) {
        this.hasBackButton = hasBackButton;
    }

    public Notification getNotification() {
        return notification;
    }

    public App getApp() {
        return app;
    }

    public Animator getAnimator() {
        return animator;
    }

    public Handler getHandler() {
        return handler;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public AccountManager getManager() {
        return manager;
    }
}
