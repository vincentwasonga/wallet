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

package com.octopus.wallet.m.p.fl.expoimpo.data;

import java.util.Date;

public abstract class Template {
    private String name;
    private Filter filter;
    private boolean printBalance = true;

    public Template(String name, boolean printBalance) {
        this.name = name;
        this.printBalance = printBalance;
    }

    public Template(String name, boolean printBalance, Filter filter) {
        this(name, printBalance);
        this.filter = filter;
    }

    public String getName() {
        return name;
    }

    public Filter getFilter() {
        return filter;
    }

    public boolean isPrintBalance() {
        return printBalance;
    }

    public static class Filter  {
        public static final int ALL_TYPES = 0;
        public static final int INCOMES = 1;
        public static final int EXPENSES = 2;
        private int types;
        private Date dateStart, dateEnd;
        public Filter(int types) {
            this.types = types;
        }

        public Filter(int types,
                      Date dateStart,
                      Date dateEnd) {
            this(types);
            this.dateStart = dateStart;
            this.dateEnd = dateEnd;
        }

        public void setTypes(int types) {
            this.types = types;
        }

        public int getTypes() {
            return types;
        }

        public Date getDateStart() {
            return dateStart;
        }

        public void setDateStart(Date dateStart) {
            this.dateStart = dateStart;
        }

        public Date getDateEnd() {
            return dateEnd;
        }

        public void setDateEnd(Date dateEnd) {
            this.dateEnd = dateEnd;
        }
    }
}
