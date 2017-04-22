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

import com.octopus.wallet.m.h.utl.Func;

/**
 * Created by yoctopus on 4/6/17.
 */
public class ArrangeOrder {
    public static final int NA =
            1;
    public static final int ND =
            2;
    public static final int AA =
            3;
    public static final int AD =
            4;
    public static final int TA =
            5;
    public static final int TD =
            6;
    public int order;

    public ArrangeOrder() {
        this.order = Func.getRandom(
                6);
    }

    public ArrangeOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
