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

package me.yoctopus.cac.notif;

/**
 * Created by yoctopus on 11/13/16.
 */

public enum Duration {
    LONG(1),
    SHORT(2);
    static final int LON = 1;
    static final int SHO = 2;
    private int length;
    Duration(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
