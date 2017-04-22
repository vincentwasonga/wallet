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

import android.text.TextUtils;
import android.widget.TextView;

import com.octopus.wallet.m.h.utl.Func;

import me.yoctopus.cac.util.LogUtil;

public class Calculator {
    static final char ADD = '+';
    static final char MINUS = '-';
    static final char MULTIPLY = '*';
    static final char DIVIDE = '/';
    static final char EQUAL = '=';
    private final String entering = "[0-9]*+[+,-,*,/]";
    private final String closed = "[0-9]*+[+,-,*,/]+[0-9]*";
    private String TAG = LogUtil.makeTag(Calculator.class);
    private TextView textView;
    private int value = 0;
    Calculator(TextView textView) {
        this.textView = textView;
    }

    boolean hasIncompleteEExpression() {
        LogUtil.d(TAG,
                "checking incomplete expression");
        return !TextUtils.isEmpty(textView.getText().toString()) &&
                Func.isMatch(textView.getText().toString(),
                        entering);

    }

    boolean hasCompleteExpression() {
        LogUtil.d(TAG,
                "checking complete expression");
        return !TextUtils.isEmpty(textView.getText().toString()) &&
                Func.isMatch(textView.getText().toString(),
                        closed);
    }

    void addDigit(int digit) {
        String input =
                getCurrentExpression();
        input = input.concat(
                String.valueOf(digit));
        setAmount(input);
    }

    void addSign(char sign) {
        //TODO checking add remove commas first
        LogUtil.d(TAG,
                sign);
        if (getCurrentExpression() == null ||
                getCurrentExpression().isEmpty() &&
                        sign != EQUAL) {
            String input =
                    String.valueOf(0)
                            + sign;
            setAmount(input);
            return;
        }
        if (sign == EQUAL) {
            String number =
                    getCurrentExpression();
            process(number);
            return;
        }
        if (hasIncompleteEExpression()) {
            LogUtil.d(TAG,
                    "processSign has " +
                            "incomplete expression" +
                            "waiting for complete " +
                            "expression");

            return;
        }
        if (hasCompleteExpression()) {
            LogUtil.d(TAG,
                    "processSign has " +
                            "complete expression" +
                            "solving expression");
            int val =
                    evaluate(getCurrentExpression());
            String newValue =
                    String.valueOf(val);
            setAmount(newValue);
            addSign(sign);
            return;
        }
        String number =
                getCurrentExpression();
        number = number.concat(
                String.valueOf(sign));
        setAmount(number);
    }

    void process(String input) {
        if (Func.isMatch(input,
                entering)) {
            setAmount(input);
            LogUtil.d(TAG,
                    "process," +
                            " waiting for extra operand");
            return;
        }
        if (input.contains(String.valueOf(MINUS)) &&
                !input.endsWith(String.valueOf(MINUS)) ||
                Func.isMatch(input,
                        closed)) {
            LogUtil.d(TAG,
                    "executing expression");
            int ansi = evaluate(input);
            String anss = String.valueOf(ansi);
            setAmount(anss);
            return;
        }
        process(input,
                2);
    }

    int evaluate(String expression) {
        Double val =
                Func.eval(expression);
        String ans = Double.toString(val);
        if (ans.contains(String.valueOf('.'))) {
            ans = ans.substring(0,
                    ans.indexOf('.'));
        }
        int ansi = Integer.parseInt(ans);
        if (ansi < 0) {
            ansi = Math.abs(ansi);
        }
        setValue(ansi);
        return ansi;
    }


    void process(String input,
                 int i) {
        LogUtil.d(TAG,
                "process" + input);
        try {
            setAmount(input);
            this.value = Integer.parseInt(input);
        } catch (NumberFormatException e) {

        }
    }

    int getValue(String s) {
        LogUtil.d(TAG,
                "getting int value of " +
                        s);
        return Integer.valueOf(s);
    }


    void backSpace() {
        LogUtil.i(TAG,
                "onClick: btnx");
        String number =
                getCurrentExpression();
        if (number
                .length() <=
                1) {
            setAmount(
                    null);
        } else {
            String newEntry =
                    number.substring(
                            0,
                            number.length() -
                                    1);
            setAmount(
                    newEntry);
        }
    }

    void deleteAll() {
        textView.setText(null);
    }

    String getCurrentExpression() {
        return textView.getText().toString();
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }

    void setAmount(String amount) {
        textView.setText(amount);
    }
}
