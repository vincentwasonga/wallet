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

package com.octopus.budget.models.math;

import android.text.format.DateUtils;
import android.util.Log;

import com.octopus.budget.models.ClassifiedRecords;
import com.octopus.budget.models.Record;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by octopus on 6/27/16.
 */
public class HelperFunctions {
    public static final String TAG = "HelperFunctions";

    public static int getTotalAngle() {
        return totalAngle;
    }

    public static int getTotalPercentage() {
        return totalPercentage;
    }

    public static String getSecondUtil() {
        return secondUtil;
    }

    public static String getMinuteUtil() {
        return minuteUtil;
    }

    public static String getHourUtil() {
        return hourUtil;
    }

    public static String getDayUtil() {
        return dayUtil;
    }

    public static String getYesterdayUtil() {
        return yesterdayUtil;
    }

    private enum Days {
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }

    private static final int totalAngle = 360;
    private static final int totalPercentage = 100;
    private static final String secondUtil = "second";
    private static final String minuteUtil = "minute";
    private static final String hourUtil = "hour";
    private static final String dayUtil = "day";
    private static final String yesterdayUtil = "yesterday";

    public static String getMoney(int amount) throws NumberFormatException {
        Log.i(TAG, "getMoney: " + amount);
        String cash = NumberFormat.getNumberInstance(Locale.US).format(amount);
        return "KES " + cash;

    }

    public static ArrayList<String> getDaysSinceLastRecord(Record last) {
        Log.i(TAG, "getDaysSinceLastRecord: ");
        ArrayList<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String currentday = getDay(calendar.get(Calendar.DAY_OF_WEEK));
        String timeelapsed = getDateString(last.getDate_millis());
        int daysback = getRangeBackWards(timeelapsed);
        Days days1 = Days.valueOf(currentday);
        int dayindex = days1.ordinal();
        for (int i = dayindex; i > daysback; i--) {
            String thisday = Days.values()[dayindex].toString();
            days.add(thisday);
        }
        return days;
    }

    private static int getRangeBackWards(String elapsed) {
        Log.i(TAG, "getRangeBackWards: ");
        String[] timeutiles = elapsed.split(" ");
        if (timeutiles[1] == "days") {
            int range = Integer.parseInt(timeutiles[0]);
            return range;
        } else return 0;
    }

    public static String getDayString(Date date) {
        Log.i(TAG, "getDayString: ");
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDate());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return Days.valueOf(getDay(day)).toString();
    }

    public static int getRandom(int size) {
        Log.i(TAG, "getRandom: " + size);
        return new Random().nextInt(size);
    }

    private static String getDay(int number) {
        Log.i(TAG, "getDay: ");
        String day = null;
        switch (number) {
            case 1: {
                day = Days.Sunday.toString();
                break;
            }
            case 2: {
                day = Days.Monday.toString();
                break;
            }
            case 3: {
                day = Days.Tuesday.toString();
                break;
            }
            case 4: {
                day = Days.Wednesday.toString();
                break;
            }
            case 5: {
                day = Days.Thursday.toString();
                break;
            }
            case 6: {
                day = Days.Friday.toString();
                break;
            }
            case 7: {
                day = Days.Saturday.toString();
                break;
            }
        }
        return day;
    }

    public static double getPercentage(int dailySpent, int dailyLimit) {
        Log.i(TAG, "getPercentage: ");
        if (dailyLimit == 0) {
            return 0.0;
        }
        double percent = dailySpent / dailyLimit;
        return percent * 100;
    }


    public static String getStringDate(long millis) {
        Log.i(TAG, "getStringDate: ");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date MyDate = getDate(millis);
        newDateFormat.applyPattern("EEEE d MMM yyyy");
        return newDateFormat.format(MyDate);
    }

    public static Date getDate(long millis) {
        Log.i(TAG, "getDate: " + millis);
        return new Date(millis);
    }

    public static String getTimeString(Date date) {
        return date.getHours() + " : " + date.getMinutes();
    }

    public static String getDateString(long millis) {
        Log.i(TAG, "getDateString: " + millis);
        return getDateString(getDate(millis));
    }

    public static int getDateDifference(Date first, Date last) {
        Log.i(TAG, "getDateDifference: ");
        String diff = getDateString(first, last);
        int dateDiff = 0;
        String[] timeUtils;
        if (diff.contains(" ")) {
            timeUtils = diff.split(" ");
            if (timeUtils[1].contains(getSecondUtil())) {
                dateDiff = 0;
            } else if (timeUtils[1].contains(getMinuteUtil())) {
                dateDiff = 0;
            } else if (timeUtils[1].contains(getHourUtil())) {
                dateDiff = 0;
            } else if (timeUtils[1].contains(getDayUtil())) {
                dateDiff = Integer.parseInt(timeUtils[0]);
            }
        } else {
            dateDiff = 1;
        }
        return dateDiff;
    }

    public static String getDateString(Date first, Date second) {
        Log.i(TAG, "getDateString: ");
        long df = first.getTime() - second.getTime();
        long dfs = df / DateUtils.SECOND_IN_MILLIS;
        String stringdf = DateUtils.formatElapsedTime(dfs);
        String[] utils = stringdf.split(":");
        String string = null;
        if (utils.length == 2) {
            if (Integer.parseInt(utils[0]) == 0) {
                if (Integer.parseInt(utils[1]) == 1) {
                    string = "1 " + getSecondUtil() + " ago";
                } else {
                    string = Integer.parseInt(utils[1]) + " seconds ago";
                }
            }
            if (Integer.parseInt(utils[0]) > 0) {
                if (Integer.parseInt(utils[0]) == 1) {
                    string = "1 " + getMinuteUtil() + " ago";
                } else {
                    string = Integer.parseInt(utils[0]) + " minutes ago";
                }
            }
        } else if (utils.length == 3) {
            if (Integer.parseInt(utils[0]) > 0) {
                if (Integer.parseInt(utils[0]) == 1) {
                    string = "1 " + getHourUtil() + " ago";
                } else {
                    string = Integer.parseInt(utils[0]) + " hours ago";
                }
            }
            if (Integer.parseInt(utils[0]) > 24) {
                int days = Integer.parseInt(utils[0]) / 24;
                if (days == 1) {
                    string = getYesterdayUtil();
                } else if (days > 1) {
                    string = days + " days ago";
                }

            }
        }
        return string;
    }

    public static String getDateString(Date then) throws NumberFormatException {
        Log.i(TAG, "getDateString: ");
        Date now = new Date();
        long df = now.getTime() - then.getTime();
        long dfs = df / DateUtils.SECOND_IN_MILLIS;
        String stringdf = DateUtils.formatElapsedTime(dfs);
        String[] utils = stringdf.split(":");
        String string = null;
        if (utils.length == 2) {
            if (Integer.parseInt(utils[0]) == 0) {
                if (Integer.parseInt(utils[1]) == 1) {
                    string = "one " + getSecondUtil() + " ago";
                } else {
                    string = Integer.parseInt(utils[1]) + " seconds ago";
                }
            }
            if (Integer.parseInt(utils[0]) > 0) {
                if (Integer.parseInt(utils[0]) == 1) {
                    string = "a " + getMinuteUtil() + " ago";
                } else {
                    string = Integer.parseInt(utils[0]) + " minutes ago";
                }
            }
        } else if (utils.length == 3) {
            if (Integer.parseInt(utils[0]) > 0) {
                if (Integer.parseInt(utils[0]) == 1) {
                    string = "an " + getHourUtil() + " ago";
                } else {
                    string = Integer.parseInt(utils[0]) + " hours ago";
                }
            }
            if (Integer.parseInt(utils[0]) > 24) {
                int days = Integer.parseInt(utils[0]) / 24;
                if (days == 1) {
                    string = getYesterdayUtil();
                } else {
                    string = days + " days ago";
                }

            }
        }

        /*if (now.getHours() == then.getHours()) {
            if (then.getMinutes() == now.getMinutes()) {
                int sd = now.getSeconds() - then.getSeconds();
                stringdf = sd + " seconds ago";
            } else {
                int md = now.getMinutes() - then.getMinutes();
                stringdf = md + " minutes ago";
            }

        } else if (now.getHours() > then.getHours()) {
            int hd = now.getHours() - then.getHours();
            stringdf = hd + " hours ago";
        } else if ((now.getDate() - then.getDate()) == 1) {
            stringdf = "Yesterday";
            if (then.getHours() <= 12) {
                stringdf += " morning";
            } else if (then.getHours() > 12 && then.getHours() <= 16) {
                stringdf += " afternoon";
            } else if (then.getHours() > 16) {
                stringdf += " evening";
            }
        } else if (now.getDay() - then.getDay() > 1) {
            int dd = now.getDay() - then.getDay();
            stringdf = dd + " days ago";
        }*/

        return string;
    }

    public static Date getFirstDate(ArrayList<Record> records) {
        Date date;
        ArrayList<Long> millis = new ArrayList<>();
        for (Record r : records) {
            millis.add(r.getDate_millis());
        }
        date = getDate(maxValue(millis));
        return date;
    }

    public static Date getLastDate(ArrayList<Record> records) {
        Date date;
        ArrayList<Long> millis = new ArrayList<>();
        for (Record r : records) {
            millis.add(r.getDate_millis());
        }
        date = getDate(minValue(millis));
        return date;

    }

    private static long maxValue(ArrayList<Long> millis) {
        Log.i(TAG, "maxValue: ");
        long max = 0;
        if (!millis.isEmpty()) {
            max = millis.get(0);
            for (int i = 1; i < millis.size(); i++) {
                if (millis.get(i) > max) {
                    max = millis.get(i);
                }
            }
        }
        return max;
    }

    private static long minValue(ArrayList<Long> millis) {
        Log.i(TAG, "minValue: ");
        long min = 0;
        if (!millis.isEmpty()) {
            min = millis.get(0);
            for (int i = 1; i < millis.size(); i++) {
                if (millis.get(i) < min) {
                    min = millis.get(i);
                }
            }
        }
        return min;
    }

    public static ArrayList<Record> organizeRecords(ArrayList<Record> records) {
        Log.i(TAG, "organizeRecords: " + records.toString());
        ArrayList<Record> records1 = new ArrayList<>();
        for (int i = records.size() - 1; i >= 0; i--) {
            records1.add(records.get(i));
        }
        return records1;
    }
    public static ArrayList<ClassifiedRecords> sortClassifiedRecords(ArrayList<ClassifiedRecords> classifiedRecordses) {
        Log.i(TAG, "sortClassifiedRecords: ");
        ArrayList<ClassifiedRecords> myrecords = classifiedRecordses;
        int n = myrecords.size();
        for (int j = 1; j < n; j++) {
            ClassifiedRecords key = myrecords.get(j);
            int i = j - 1;
            while ((i > -1) && (myrecords.get(i).getId() > key.getId())) {
                myrecords.set((i + 1), myrecords.get(i));
                i--;
            }
            myrecords.set((i + 1), key);
        }
        return myrecords;
    }
}
