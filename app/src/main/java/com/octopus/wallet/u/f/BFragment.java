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

package com.octopus.wallet.u.f;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.octopus.wallet.App;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.AccountManager;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.e.Classify;
import com.octopus.wallet.m.h.ArrangeOrder;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.RecordClassifier;
import com.octopus.wallet.m.h.RecordsArranger;
import com.octopus.wallet.m.h.utl.BitmapUtil;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.p.db.AppDatabase;
import com.octopus.wallet.m.p.fl.Dir;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import me.yoctopus.cac.pref.Preferences;
import com.octopus.wallet.m.pb.Record;

import java.util.ArrayList;
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
import me.yoctopus.cac.util.LogUtil;


public abstract class BFragment extends Fragment {
    private String TAG = LogUtil.makeTag(BFragment.class);
    private App app;
    private Handler handler;
    private Notification notification;
    private Animator animator;
    private Preferences preferences;
    private AppDatabase database;
    private AccountManager manager;
    private ViewSwitcher switcher;

    public abstract String getName();

    public abstract int onGetLayout();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.i(TAG,
                "onCreate: ");
        super.onCreate(savedInstanceState);
        app = (App) getActivity().getApplication();
        handler = new Handler();
        notification = new Notification(getActivity());
        animator = new Animator();
        database = new AppDatabase(getActivity());
        preferences = new Preferences(getActivity());
        manager = new AccountManager(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                onGetLayout(),
                container,
                false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (switcher != null) {
            switcher.showNext();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (switcher != null) {
            switcher.showNext();
        }
    }

    public SList<DayRecords> getDayRecords() {
        return Func.getDayRecords(getDatabase().getRecords());
    }

    public SList<Record.Category> getCategories() {
        return sortDescending(getDatabase().getCategories());
    }


    private SList<Record.Category> sortDescending(List<Record.Category> list) {
        Collections.sort(list,
                new Comparator<Record.Category>() {
                    @Override
                    public int compare(Record.Category o1,
                                       Record.Category o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
        return new SList<>(list);
    }

    public SList<DayRecords> classifyRecords(
            List<Record> records, ArrangeOrder order) {
        RecordsArranger arranger =
                new RecordsArranger(
                        new ArrayList<>(records));
        arranger.sort(
                new ArrangeOrder(order.getOrder()).getOrder());
        SList<DayRecords> classifiedRecodes;
        RecordClassifier classifier =
                new RecordClassifier(arranger.getRecords());
        LogUtil.i(TAG,
                "classifyRecords: ");
        try {
            classifier.classify();
        } catch (Classify e) {
            e.printStackTrace();
        }
        classifiedRecodes = new SList<>(classifier.getDayRecords());
        return classifiedRecodes;
    }

    public View getView(@IdRes int id) {
        return getActivity().findViewById(id);
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
        Bitmap bitmap = BitmapUtil.getBitmap(getActivity(), R.drawable.budget);
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
        Bitmap bitmap = BitmapUtil.getBitmap(getActivity(), R.drawable.budget);
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

    public void shakeView(@NonNull View view) {
        animator = new Animator(view,
                Anim.Attention.shake(),
                AnimDuration.small());
        animator.animate();
    }

    public void shakeView(@IdRes int id) {
        shakeView(getView(id));
    }

    public Intent getIntent(@NonNull Class<? extends AppCompatActivity> activity) {
        return new Intent(getActivity(),
                activity);
    }

    public String getAppFolder() {
        return getMainDirectory().getName();
    }

    public Dir getMainDirectory() {
        return Dir.Main;
    }

    public void startActivity(@NonNull final Class activity,
                              final Bundle args) {
        Runnable activityRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = getIntent(
                                activity);
                        intent.putExtra("bundle",
                                args);
                        startActivity(intent);
                    }
                };
        getHandler().postDelayed(activityRunnable,
                500);
    }


    public void executeRunnable(@NonNull Runnable runnable) {
        getHandler().post(runnable);
    }

    public void executeRunnable(@NonNull Runnable runnable,
                                long millis) {
        getHandler().postDelayed(runnable,
                millis);
    }

    public void dismissNotification() {
        getNotification().dismiss();
    }

    public Context getBudgetContext() {
        assert getApp().getBudgetContext() != null;
        return getApp().getBudgetContext();
    }

    public int getBalance() {
        AccountManager manager = new AccountManager(getContext());
        return manager.getInfo().getTotalBal();
    }

    public Notification getNotification() {
        return notification;
    }

    public App getApp() {
        return app;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public AccountManager getManager() {
        return manager;
    }

    public Animator getAnimator() {
        return animator;
    }

    public Handler getHandler() {
        return handler;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public ViewSwitcher getSwitcher() {
        return switcher;
    }

    public void setSwitcher(ViewSwitcher switcher) {
        this.switcher = switcher;
    }
}
