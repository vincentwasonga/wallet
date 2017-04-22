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

package com.octopus.wallet.m.h;

import com.octopus.wallet.m.b.S;
import com.octopus.wallet.m.pb.Record;

/**
 * Created by octopus on 9/30/16.
 */
public class RecordPair implements S {
    private Record income;
    private Record expense;

    public RecordPair(Record income,
                      Record expense) {
        this.income = income;
        this.expense = expense;
    }

    public RecordPair(Record income) {
        this.income = income;
    }

    public RecordPair(Record expense,
                      int arg) {
        this.expense = expense;
    }

    public RecordPair() {
        this(null,
                null);
    }

    public boolean hasIncome() {
        return this.income != null;
    }

    public boolean hasExpense() {
        return this.expense != null;
    }

    public boolean hasBoth() {
        return hasExpense() &&
                hasIncome();
    }

    public Record getIncome() {
        return income;
    }

    public Record getExpense() {
        return expense;
    }

    public void setIncome(Record income) {
        this.income = income;
    }

    public void setExpense(Record expense) {
        this.expense = expense;
    }

    @Override
    public int getId() {
        return 0;
    }
}
