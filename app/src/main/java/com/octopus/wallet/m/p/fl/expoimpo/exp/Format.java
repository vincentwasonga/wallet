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

package com.octopus.wallet.m.p.fl.expoimpo.exp;

public enum Format {
    DEFAULT(0),
    CSV(1),
    PDF(2),
    XLS(3),
    TXT(4);
    public static final int DE = 0;
    public static final int CS = 1;
    public static final int PD = 2;
    public static final int XL = 3;
    private int format;

    public int getFormat() {
        return format;
    }

    Format(int format) {
        this.format = format;
    }

}
