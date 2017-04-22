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

package com.octopus.wallet.m.p.nt;

/**
 * Created by octopus on 10/19/16.
 */
public class NData {
    private String dataName;
    private Object data;

    public NData(String dataName) {
        this.dataName = dataName;
    }

    public NData(String dataName,
                 Object data) {
        this.dataName = dataName;
        this.data = data;
    }



    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
