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

package com.octopus.wallet.m.pb;

import com.octopus.wallet.m.b.S;

public class Currency implements S {
    public static Currency DEFAULT_CURRENCY() {
        return new Currency(
                "KES",
                "Kenya",
                1);
    }
    private int id;
    private String code;
    private String country;
    private float rate;

    public Currency(String code,
             String country,
                    float rate) {
        this.code = code;
        this.country = country;
        this.rate = rate;
    }

    public String getCountry() {
        return country;
    }

    public String getCode() {
        return code;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return code + " " + country;
    }
}
