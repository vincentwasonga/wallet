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

public class Location implements S {
    private long longitude;
    private long latitude;
    private String name;
    private  int id;

    public Location(long longitude, long latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location(String name) {
        this.name = name;
    }

   public void set(long longitude, long latitude) {
       this.latitude = latitude;
       this.longitude = longitude;
   }

    public long getLongitude() {
        return longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
