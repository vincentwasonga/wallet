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

package com.octopus.wallet.m.h.utl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;

import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.e.Classify;
import com.octopus.wallet.m.e.RecordsEmpty;
import com.octopus.wallet.m.h.ArrangeOrder;
import com.octopus.wallet.m.h.Day;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.m.h.RecordClassifier;
import com.octopus.wallet.m.h.RecordComparators;
import com.octopus.wallet.m.h.RecordPair;
import com.octopus.wallet.m.h.RecordPairList;
import com.octopus.wallet.m.h.RecordsArranger;
import com.octopus.wallet.m.h.RecordsToPairsConverter;
import com.octopus.wallet.m.pb.Currency;
import com.octopus.wallet.m.pb.Record;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.yoctopus.cac.util.LogUtil;

public class Func {
    public static final String TAG =
            LogUtil.makeTag(Func.class);

    public static Currency currentCurrency;


    public static void init(Currency currency) {
        currentCurrency = currency;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat(
                "MM" +
                        "dd-" +
                        "HH" +
                        "MM" +
                        "ss",
                Locale.ENGLISH);
    }

    public static String getCurrency() {
        return currentCurrency.getCode();
    }

    public static String getMoney(int amount,
                                  @Nullable Currency currency)
            throws NumberFormatException {
        LogUtil.i(TAG,
                "getMoney: " +
                        amount);
        String cash = getFormatAmount(
                amount);
        if (currency == null) {
            return currentCurrency.getCode() +
                    " " +
                    cash;
        }
        return currency.getCode() +
                " " +
                cash;
    }

    private static String getFormatAmount(int amount) {
        return NumberFormat.getNumberInstance()
                .format(convertToCurrentCurrency(amount));
    }

    public static int convertToCurrentCurrency(int amount) {
        float from = Currency.DEFAULT_CURRENCY().getRate();
        float to = currentCurrency.getRate();
        return getAmount(from, to, amount);
    }

    public static int convertToDefaultCurrency(int amount) {
        float from = currentCurrency.getRate();
        float to = Currency.DEFAULT_CURRENCY().getRate();
        return getAmount(from, to, amount);
    }

    private static int getAmount(float from,
                                 float to,
                                 int amount) {
        if (from != 0) {
            return (int) ((to * amount) / from);
        }
        return 0;
    }

    public static String getMoney(int amount) {
        return getMoney(amount,
                null);
    }

    public static String getDateDayddMMM(Date date) {
        SimpleDateFormat newDateFormat =
                new SimpleDateFormat(
                        "EEEE, dd/MMM",
                        Locale.getDefault());
        return newDateFormat.format(
                date);
    }

    public static String getDateddMMM(Date date) {
        SimpleDateFormat newDateFormat =
                new SimpleDateFormat(
                        "dd/MMM",
                        Locale.getDefault());
        return newDateFormat.format(
                date);
    }

    public static String getDateDDMM(Day day) {
        String[] utils = day.getDayName()
                .split(" ");
        if (utils.length < 2) {
            return day.getDayName();
        }
        return utils[0] +
                "/" +
                utils[1];
    }

    public static String getDateddMMM(long millis) {
        return getDateddMMM(getDate(millis));
    }

    public static String getDateDayddMMM(long millis) {
        return getDateDayddMMM(getDate(millis));
    }

    public static String getDateMMSS(long millis) {
        return getDateMMSS(getDate(millis));
    }

    public static String getDateMMSS(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(
                "HH:mm",
                Locale.ENGLISH);
        return format.format(date);
    }
    public static Date addDays(Date date, int days) {
        return new DateTime(date).plusDays(days).toDate();
    }
    public static Date addWeeks(Date date, int weeks) {
        return new DateTime(date).plusWeeks(weeks).toDate();
    }
    public static Date addMonths(Date date, int months) {
        return new DateTime(date).plusMonths(months).toDate();
    }

    public static int getRandom(int size) {
        LogUtil.i(TAG,
                "getRandom: " +
                        size);
        return new Random().nextInt(size);
    }

    public static double getPercentage(int num1,
                                       int num2) {
        LogUtil.i(TAG,
                "getPercentage: ");
        if (num2 == 0) {
            return 0.0;
        }
        double percent = num1 *
                Math.pow(num2,
                        -1);
        return percent *
                100;
    }
    public static SList<DayRecords> getDayRecords(List<Record> records) {
        RecordsArranger arranger = new RecordsArranger(
                new ArrayList<>(records));
        arranger.sort(new ArrangeOrder(
                ArrangeOrder.TA)
                .getOrder());
        records = new SList<>(arranger.getRecords());

        SList<DayRecords> classifiedRecodes;
        RecordClassifier classifier =
                new RecordClassifier(new ArrayList<>(records));
        try {
            classifier.classify();
        } catch (Classify e) {
            e.printStackTrace();
        }
        classifiedRecodes = new SList<>(classifier.getDayRecords());
        return classifiedRecodes;
    }


    public static List<RecordPair> getRecordPairs(List<Record> records) {
        ArrayList<RecordPair> recordPairs;
        RecordsToPairsConverter converter =
                new RecordsToPairsConverter(
                        new ArrayList<>(records));
        try {
            converter.convert();
        } catch (RecordsEmpty e) {
            LogUtil.e(TAG,
                    "LedgerRecordAdapter: ",
                    e);
        }
        recordPairs = converter.getRecordPairs();
        RecordPairList pairList =
                new RecordPairList(recordPairs);
        pairList.reArrange();
        recordPairs = pairList.getRecordPairs();
        return recordPairs;
    }

    public static ArrayList<DayRecords> sortDayRecordsTA(
            ArrayList<DayRecords> dayRecords) {
        RecordsArranger arranger =
                new RecordsArranger(
                        dayRecords,
                        0);
        arranger.sort2(
                new ArrangeOrder(
                        ArrangeOrder.TA
                ).getOrder());

        return arranger.getDayRecords();
    }



    public static Date getDate(long millis) {
        return new DateTime(millis).toDate();
    }

    public static boolean isToday(long millis) {
        DateTime dateTime = new DateTime(
                millis);
        DateTime now = DateTime.now();
        Period period = new Period(now,
                dateTime);
        try {
            Days days = period
                    .toStandardDays();
            return days.getDays() == 0;
        } catch (UnsupportedOperationException e) {
            LogUtil.e(TAG,
                    "Cannot evaluate difference",
                    e
            );
            return false;
        }
    }

    public static String getDateMPassed(long millis) {
        LogUtil.i(TAG,
                "getDateMPassed: " +
                        millis);
        return getDateMPassed(
                getDate(millis));
    }

    public static String getDateDMMHHMM(long millis) {
        SimpleDateFormat newDateFormat =
                new SimpleDateFormat(
                        "dd/" +
                                "MM/" +
                                "HH:" +
                                "MM",
                        Locale.getDefault());
        Date date = getDate(millis);
        newDateFormat.applyPattern(
                "dd/" +
                        "MM/" +
                        "HH:" +
                        "MM");
        return newDateFormat.format(
                date);
    }

    public static String getDateDMMHHMM(Date date) {
        return getDateDMMHHMM(date.getTime());
    }

    public static String getDateMPassed(Date then) {
        LogUtil.i(TAG,
                "getDateMPassed: ");
        String dateString;
        DateTime firstDate = new DateTime(
                then);
        DateTime lastDate = DateTime.now();
        if (firstDate.isAfter(lastDate)) {
            DateTime temp = lastDate;
            lastDate = firstDate;
            firstDate = temp;
        }
        Period period = new Period(firstDate,
                lastDate);
        Seconds seconds;
        try {
            seconds = period
                    .toStandardSeconds();
        } catch (UnsupportedOperationException e) {
            seconds = null;
        }
        if (seconds == null) {
            return "long ago";
        }
        int diff = seconds.getSeconds();
        if (diff < 60) {
            dateString = diff +
                    " seconds ago";
        } else if (diff >= 60 &&
                diff < 60 *
                        60) {
            Minutes minutes = period.
                    toStandardMinutes();
            int miff = minutes.getMinutes();
            dateString = miff +
                    " minutes ago";
        } else if (diff >= 60 *
                60 &&
                diff < 60 *
                        60 *
                        24) {
            Hours hours = period
                    .toStandardHours();
            int hiff = hours.getHours();
            dateString = hiff +
                    " hours ago";
        } else {
            String dateFormat = getDateDayddMMM(
                    then.getTime());
            String[] utils = dateFormat
                    .split(" ");
            if (utils.length == 4) {
                dateString = utils[1] +
                        " " +
                        utils[2];
            } else {
                dateString = dateFormat;
            }

        }
        return dateString;
    }

    public static Date getFirstDate(ArrayList<Record> records) {
        Date date;
        ArrayList<Long> millis = new ArrayList<>();
        for (Record r : records) {
            millis.add(r.getDate_millis());
        }
        date = getDate(
                maxValue(
                        millis));
        return date;
    }

    public static Date getLastDate(ArrayList<Record> records) {
        Date date;
        ArrayList<Long> millis = new ArrayList<>();
        for (Record r : records) {
            millis.add(r.getDate_millis());
        }
        date = getDate(
                minValue(
                        millis));
        return date;
    }

    private static long maxValue(ArrayList<Long> millis) {
        LogUtil.i(TAG,
                "maxValue: ");
        long max = 0;
        if (!millis.isEmpty()) {
            max = millis.get(0);
            for (int i = 1; i < millis.size(); i++) {
                if (millis.get(i) > max) {
                    max = millis.get(
                            i);
                }
            }
        }
        return max;
    }

    private static long minValue(ArrayList<Long> millis) {
        LogUtil.i(TAG,
                "minValue: ");
        long min = 0;
        if (!millis.isEmpty()) {
            min = millis.get(0);
            for (int i = 1; i < millis.size(); i++) {
                if (millis.get(i) < min) {
                    min = millis.get(
                            i);
                }
            }
        }
        return min;
    }

    public static <T> List<T> reverse(List<T> t) {
        List<T> t1 = new ArrayList<>();
        for (int i = t.size() - 1;
             i >= 0; i--) {
            t1.add(t.get(i));
        }
        return t1;
    }

    public static ArrayList<Record> sortRecords(ArrayList<Record> records,
                                                ArrangeOrder order) {
        ArrayList<Record> newRecords = records;
        switch (order.getOrder()) {
            case ArrangeOrder.NA: {
                Collections.sort(
                        newRecords,
                        RecordComparators
                                .CompareWithName);
                break;
            }
            case ArrangeOrder.ND: {
                Collections.sort(
                        newRecords,
                        RecordComparators
                                .CompareWithName);
                newRecords = new ArrayList<>(reverse(
                        newRecords));
                break;
            }
            case ArrangeOrder.AA: {
                Collections.sort(
                        newRecords,
                        RecordComparators
                                .CompareWithAmount);
                break;
            }
            case ArrangeOrder.AD: {
                Collections.sort(
                        newRecords,
                        RecordComparators
                                .CompareWithAmount);
                newRecords = new ArrayList<>(reverse(
                        newRecords));
                break;
            }
            case ArrangeOrder.TA: {
                Collections.sort(
                        newRecords,
                        RecordComparators
                                .CompareWithTime);
                break;
            }
            case ArrangeOrder.TD: {
                Collections.sort(
                        newRecords,
                        RecordComparators
                                .CompareWithTime);
                newRecords = new ArrayList<>(reverse(
                        newRecords));
                break;
            }
        }
        return newRecords;
    }

    public static ArrayList<DayRecords> sortDayRecords(
            ArrayList<DayRecords> dayRecords,
            ArrangeOrder order) {
        ArrayList<DayRecords> newRecords = dayRecords;
        switch (order.getOrder()) {
            case ArrangeOrder.TA: {
                Collections.sort(
                        newRecords,
                        RecordComparators
                                .CompareWithTime2);
                break;
            }
            case ArrangeOrder.TD: {
                Collections.sort(
                        newRecords,
                        RecordComparators
                                .CompareWithTime2);
                newRecords = new ArrayList<>(reverse(
                        newRecords));
                break;
            }
        }
        return newRecords;
    }

    public static void shuffle(List<?> t) {
        Collections.shuffle(t);
    }


    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ?
                        str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length())
                    throw new RuntimeException("Unexpected: " +
                            (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' &&
                        ch <= '9') ||
                        ch == '.') {
                    while ((ch >= '0' &&
                            ch <= '9') ||
                            ch == '.') nextChar();
                    x = Double.parseDouble(
                            str.substring(startPos,
                                    this.pos));
                } else if (ch >= 'a' &&
                        ch <= 'z') {
                    while (ch >= 'a' &&
                            ch <= 'z') nextChar();
                    String func =
                            str.substring(startPos,
                                    this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x =
                                    Math.sqrt(x);
                            break;
                        case "sin":
                            x =
                                    Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x =
                                    Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x =
                                    Math.tan(Math.toRadians(x));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " +
                                    func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " +
                            (char) ch);
                }

                if (eat('^')) x = Math.pow(x,
                        parseFactor());

                return x;
            }
        }.parse();
    }


    // Returns true if two input Object are both null or equal
    // to each other.
    public static boolean equals(Object a,
                                 Object b) {
        return (a == b) ||
                (a != null &&
                        a.equals(b));
    }


    public static String getUserAgent(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            0);
        } catch (PackageManager.NameNotFoundException e) {
            throw
                    new IllegalStateException("getPackageInfo failed");
        }
        return String.format("%s-%s\n" +
                        "%s-%s\n" +
                        "Android-%s",
                packageInfo.packageName,
                packageInfo.versionName,
                Build.BRAND,
                Build.DEVICE,
                Build.VERSION.RELEASE);
    }


    public static boolean isMatch(String text,
                                  String pattern) {
        try {
            Pattern compile = Pattern.compile(pattern);
            Matcher matcher = compile.matcher(text);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

}
