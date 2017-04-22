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

import com.octopus.wallet.m.pb.Record;

import java.util.ArrayList;

/**
 * Created by octopus on 9/30/16.
 */
public class RecordPairList {
    private ArrayList<RecordPair> recordPairs;
    private ArrayList<Record> incomes;
    private ArrayList<Record> expenses;

    public RecordPairList(ArrayList<RecordPair> recordPairs) {
        this.recordPairs = recordPairs;
        incomes = new ArrayList<>();
        expenses = new ArrayList<>();
    }

    public void reArrange() {
        for (RecordPair pair : recordPairs) {
            if (pair.hasBoth()) {
                incomes.add(
                        pair.getIncome());
                expenses.add(
                        pair.getExpense());
            } else if (pair.hasIncome()) {
                incomes.add(
                        pair.getIncome());
            } else if (pair.hasExpense()) {
                expenses.add(
                        pair.getExpense());
            }
        }
        recordPairs = new ArrayList<>();
        if (incomes.size() >= expenses.size()) {
            for (int i = 0; i < incomes.size(); i++) {
                RecordPair pair =
                        new RecordPair();
                pair.setIncome(
                        incomes.get(i));
                if (i < expenses.size()) {
                    pair.setExpense(
                            expenses.get(i));
                }
                recordPairs.add(pair);
            }
        } else if (incomes.size() < expenses.size()) {
            for (int i = 0; i < expenses.size(); i++) {
                RecordPair pair
                        = new RecordPair();
                pair.setExpense(
                        expenses.get(i));
                if (i < incomes.size()) {
                    pair.setIncome(
                            incomes.get(i));
                }
                recordPairs.add(pair);
            }
        }
    }

    public ArrayList<RecordPair> getRecordPairs() {
        return recordPairs;
    }
}
