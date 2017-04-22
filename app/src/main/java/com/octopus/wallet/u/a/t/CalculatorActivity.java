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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.v.v.AmountTextView;
import com.octopus.wallet.u.a.BActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CalculatorActivity extends BActivity implements View.OnClickListener {
    public static final String RETURN_LABEL = "result";
    public static final String DATA_LABEL = "data_label";
    @InjectView(R.id.amountContainer)
    AmountTextView screen;
    @InjectView(R.id.backButton)
    ImageButton backButton;
    @InjectView(R.id.titleText)
    TextView titleText;
    @InjectView(R.id.btndone)
    ImageButton btnDone;
    @InjectView(R.id.add_btn)
    ImageButton addButton;
    @InjectView(R.id.minus_btn)
    ImageButton minusButton;
    @InjectView(R.id.times_btn)
    ImageButton multiplyButton;
    @InjectView(R.id.divide_btn)
    ImageButton divideButton;
    @InjectView(R.id.equal_btn)
    ImageButton equalButton;
    private Calculator calculator;
    private List<Button> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        ButterKnife.inject(this);

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (titleText != null) {
            titleText.setText(title);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        calculator = new Calculator(screen);
        setTitle("Add Amount");
        Intent intent = getIntent();
        if (intent.hasExtra(DATA_LABEL)) {
            calculator.setAmount(
                    String.valueOf(intent.getIntExtra(DATA_LABEL, 0)));
            setTitle("Update Amount");
        }
        buttons = new ArrayList<>();
        configureKeypad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        animateActivity();
    }

    private void configureKeypad() {
        buttons.add((Button) getView(R.id.btn0));
        buttons.add((Button) getView(R.id.btn1));
        buttons.add((Button) getView(R.id.btn2));
        buttons.add((Button) getView(R.id.btn3));
        buttons.add((Button) getView(R.id.btn4));
        buttons.add((Button) getView(R.id.btn5));
        buttons.add((Button) getView(R.id.btn6));
        buttons.add((Button) getView(R.id.btn7));
        buttons.add((Button) getView(R.id.btn8));
        buttons.add((Button) getView(R.id.btn9));
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
        getView(R.id.btnx).setOnClickListener(this);
        getView(R.id.btnx).setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        calculator.deleteAll();
                        return true;
                    }
                });
        backButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        multiplyButton.setOnClickListener(this);
        divideButton.setOnClickListener(this);
        equalButton.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        Runnable click =
                new Runnable() {
                    @Override
                    public void run() {
                        switch (view.getId()) {
                            case R.id.btn0:
                            case R.id.btn1:
                            case R.id.btn2:
                            case R.id.btn3:
                            case R.id.btn4:
                            case R.id.btn5:
                            case R.id.btn6:
                            case R.id.btn7:
                            case R.id.btn8:
                            case R.id.btn9: {
                                Button b =
                                        (Button) view;
                                processDigit(Integer.valueOf(
                                        b.getText().toString()));
                                break;
                            }
                            case R.id.add_btn: {
                                processSign(Calculator.ADD);
                                break;
                            }
                            case R.id.minus_btn: {
                                processSign(Calculator.MINUS);
                                break;
                            }
                            case R.id.times_btn: {
                                processSign(Calculator.MULTIPLY);
                                break;
                            }
                            case R.id.divide_btn: {
                                processSign(Calculator.DIVIDE);
                                break;
                            }
                            case R.id.equal_btn: {
                                processSign(Calculator.EQUAL);
                                break;
                            }
                            case R.id.btnx: {
                                calculator.backSpace();
                                break;
                            }
                            case R.id.btndone: {
                                doMainAction();
                                break;
                            }
                            case R.id.backButton: {
                                finish();
                                break;
                            }
                        }
                    }
                };
        getHandler().post(click);
    }

    private void doMainAction() {
        if (calculator.hasIncompleteEExpression()) {
            return;
        }
        String number = calculator.getCurrentExpression();
        if (number.length() <= 0 ||
                number.isEmpty()) {
            sendBackResult(0);

        } else {
            calculator.process(
                    number);
            int cash = calculator.getValue();
            sendBackResult(cash);
        }
    }

    private void sendBackResult(int result) {
        Intent intent = new Intent();
        intent.putExtra(RETURN_LABEL, result);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void processDigit(int digit) {
        calculator.addDigit(digit);
    }

    private void processSign(char sign) {
        calculator.addSign(sign);
    }
}
