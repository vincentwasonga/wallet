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

package com.octopus.wallet.u.a.t;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.u.a.BActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.yoctopus.cac.notif.NDialog;

public class DateTimeActivity extends BActivity {
    public static final int RETURN_BEFORE = 0;
    public static final int RETURN_AFTER = 1;
    public static final String DATA_LABEL = "date_millis";
    public static final String RESULT_LABEL = "date_time";
    public static String RETURN_TYPE;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private Date date, date2;
    private boolean updating =
            false;
    private boolean setting_future =
            false;
    private boolean canceled = false;
    private TextView dateTextView;
    private TextView title;
    private DatePicker.OnDateChangedListener dListener =
            new DatePicker.OnDateChangedListener() {
                public void onDateChanged(DatePicker view,
                                          int year,
                                          int monthOfYear,
                                          int dayOfMonth) {
                    Calendar calendar =
                            Calendar.getInstance(Locale.getDefault());
                    calendar.set(year, monthOfYear, dayOfMonth);
                    date = calendar.getTime();
                    refreshDate(date);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        animateActivity();
        setHasBackButton(true);
        date = new Date();
        Runnable create = new Runnable() {
            @Override
            public void run() {
                ImageView back = (ImageView) getView(
                        R.id.backButton);
                back.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                title = (TextView) getView(
                        R.id.titleText);
                datePicker = (DatePicker) getView(
                        R.id.datePicker);
                timePicker = (TimePicker) getView(
                        R.id.timePicker);
                dateTextView = (TextView) getView(
                        R.id.dateTextView);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.setLenient(true);
                datePicker.init(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        dListener);
                setTitle("Set Time");
                Intent intent = getIntent();
                if (intent.hasExtra(DATA_LABEL)) {
                    long longtime = intent.getLongExtra(DATA_LABEL,
                            0);
                    date = new Date(longtime);
                    setTitle("Update Time");
                    updating = true;
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(date);
                    calendar.setLenient(true);
                    timePicker.setCurrentHour(
                            calendar.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(
                            calendar.get(Calendar.MINUTE));
                    datePicker.updateDate(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                }
                if (intent.hasExtra(RETURN_TYPE)) {
                    setting_future = intent.getIntExtra(
                            RETURN_TYPE,
                            RETURN_BEFORE) == RETURN_AFTER;
                }
                date2 = new Date();
                refreshDate(date);
                Button cancelButton = (Button) getView(
                        R.id.cancelButton);
                cancelButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelUpdate();
                            }
                        });
                Button okButton = (Button) getView(
                        R.id.okButton);
                okButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });

            }
        };
        getHandler().post(create);
    }


    @Override
    public void setTitle(CharSequence title) {
        this.title.setText(title);
        super.setTitle(title);
    }

    private void refreshDate(Date date) {
        dateTextView.setText(Func.getDateDayddMMM(date));
    }

    private void setCurrentTime() {
        date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setLenient(true);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        datePicker.updateDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));

    }

    private void cancelUpdate() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                canceled = true;
                finish();
            }
        };
        getHandler().postDelayed(runnable,
                200);
    }

    private boolean checkDate() {
        try {
            Func.getDateMPassed(date);
        } catch (UnsupportedOperationException e) {
            notifyDialog("Cannot save this time",
                    "Try to update a closer date");
            return false;
        }
        if (!setting_future) {
            if (date.after(new Date())) {
                Runnable check =
                        new Runnable() {
                            @Override
                            public void run() {
                                notifyDialog("Invalid date " +
                                                "add time ",
                                        "you have entered " +
                                                "a future time ",
                                        new NDialog.DButton(
                                                "Set now",
                                                new NDialog.DButton.BListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        setCurrentTime();
                                                    }
                                                }),
                                        new NDialog.DButton(
                                                "Cancel",
                                                new NDialog.DButton.BListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        cancelUpdate();
                                                    }
                                                }));
                            }
                        };
                getHandler().postDelayed(check,
                        200);
                return false;
            } else {

                return true;
            }
        } else {
            if (date.before(new Date())) {
                Runnable check = new Runnable() {
                    @Override
                    public void run() {

                        notifyDialog("Invalid date " +
                                        "add time",
                                "you have entered " +
                                        "a passed time",
                                new NDialog.DButton(
                                        "Set now",
                                        new NDialog.DButton.BListener() {
                                            @Override
                                            public void onClick(View v) {
                                                setCurrentTime();
                                            }
                                        }),
                                new NDialog.DButton(
                                        "Cancel",
                                        new NDialog.DButton.BListener() {
                                            @Override
                                            public void onClick(View v) {
                                                cancelUpdate();
                                            }
                                        }));
                    }
                };
                getHandler().postDelayed(check,
                        200);
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void finish() {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        date.setHours(timePicker.getCurrentHour());
                        date.setMinutes(timePicker.getCurrentMinute());
                        Intent intent = new Intent();
                        if (canceled) {
                            if (updating) {
                                date = date2;
                            } else {
                                date = new Date();
                            }
                            setResult(RESULT_CANCELED,
                                    intent);
                            intent.putExtra(RESULT_LABEL,
                                    date.getTime());
                            DateTimeActivity.super.finish();
                        } else {
                            if (checkDate()) {
                                setResult(RESULT_OK,
                                        intent);
                                intent.putExtra(RESULT_LABEL,
                                        date.getTime());
                                DateTimeActivity.super.finish();
                            } else {
                                setResult(RESULT_CANCELED);
                            }
                        }
                    }
                };
        getHandler().postDelayed(runnable,
                500);
    }
}
