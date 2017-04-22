/*
 * ﻿Copyright [2016] [Peter Vincent]
 * Licensed under the Apache License, Version 2.0 (Personal Budget);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.octopus.budget.activities.transaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.octopus.budget.R;
import com.octopus.budget.models.math.HelperFunc;
import com.octopus.budget.models.notification.DialogButton;
import com.octopus.budget.models.notification.Notification;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Calendar;
import java.util.Date;

public class DateTimePickerActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private DatePicker datePicker;
    private Date date;
    private Date date2;
    private Notification notification;
    private boolean isUpdating = false;
    private boolean canceled = false;
    private int failedAttempt = 0;
    private TextView dateTextView;
    private DatePicker.OnDateChangedListener dListener = new DatePicker.OnDateChangedListener() {

        public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

            Calendar c = Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth);
            int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
            Log.v("day of year", String.valueOf(dayOfYear));
            date.setYear(c.get(Calendar.YEAR) - 1900);
            date.setMonth(c.get(Calendar.MONTH));
            date.setDate(c.get(Calendar.DAY_OF_MONTH));
            refreshDate(date);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        date = new Date();
        this.datePicker = (DatePicker) findViewById(R.id.datePicker);
        this.timePicker = (TimePicker) findViewById(R.id.timePicker);
        this.dateTextView = (TextView) findViewById(R.id.dateTextView);
        Calendar cNow= Calendar.getInstance();
        cNow.setLenient(true);
        datePicker.init(cNow.get(Calendar.YEAR), cNow.get(Calendar.MONTH), cNow.get(Calendar.DAY_OF_MONTH), dListener);


        setTitle("Set Time ...");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("date_millis")) {
            long longtime = intent.getLongExtra("date_millis", 0);
            date = new Date(longtime);
            setTitle("Update Time ...");
            date2 = date;
            isUpdating = true;
            timePicker.setCurrentHour(date.getHours());
            timePicker.setCurrentMinute(date.getMinutes());
            Calendar cal = Calendar.getInstance();
            DateTime now = DateTime.now();
            DateTime then = new DateTime(date);
            int days = Days.daysBetween(then.withTimeAtStartOfDay(), now.withTimeAtStartOfDay()).getDays();
            cal.add(Calendar.DATE, 0 - days);
            cal.setLenient(true);
            datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        }
        refreshDate(date);
        notification = new Notification(this);
    }



    private void refreshDate(Date date) {
        dateTextView.setText(HelperFunc.getStringDate(date.getTime()));
    }

    private void setCurrentTime() {
        date = new Date();
        timePicker.setCurrentHour(date.getHours());
        timePicker.setCurrentMinute(date.getMinutes());

        Calendar cNow= Calendar.getInstance();
        cNow.setLenient(true);
        datePicker.updateDate(cNow.get(Calendar.YEAR), cNow.get(Calendar.MONTH), cNow.get(Calendar.DATE));

    }

    private void cancelUpdate() {
        canceled = true;
        finish();
    }

    private boolean checkDate() {
        if (date.after(new Date())) {
            notification.setNotificationBundle("Invalid date and time ...", "you have entered a future time",
                    new DialogButton("Update to now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setCurrentTime();
                        }
                    }),
                    new DialogButton("Cancel update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelUpdate();
                        }
                    }));
            notification.notify(Notification.DIALOG);
            failedAttempt = 1;
            return false;
        } else {

            return true;
        }

    }

    @Override
    public void finish() {
        date.setHours(timePicker.getCurrentHour());
        date.setMinutes(timePicker.getCurrentMinute());
        if (canceled) {
            if (isUpdating) {
                date = date2;
            }
            else {
                date = new Date();
            }
            Intent intent = new Intent();
            intent.putExtra("datetime", date.getTime());
            setResult(RESULT_OK, intent);
            super.finish();
        }
        else {
            if (checkDate()) {
                Intent intent = new Intent();
                intent.putExtra("datetime", date.getTime());
                setResult(RESULT_OK, intent);
                super.finish();
            }
        }
    }
}
