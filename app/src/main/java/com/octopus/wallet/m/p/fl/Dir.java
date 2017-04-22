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

package com.octopus.wallet.m.p.fl;

import android.os.Environment;

import java.io.File;


public enum Dir {
    Main("Personal Budget"),
    Camera("Personal Budget/Camera"),
    Receipts("Personal Budget/Receipts"),
    Reports("Personal Budget/Reports"),
    DownLoads("Personal Budget/Downloads");
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getState() {
        return Environment.getExternalStorageState();
    }

    public static boolean isWritable() {
        return Environment.MEDIA_MOUNTED.equals(
                getState());
    }
    Dir(String name) {
        String path = Environment.getExternalStorageDirectory().toString();
        setName(path + File.separator + name);
        File file = new File(getName());
        if (!file.exists()) {
            final boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                setName(file.getAbsolutePath());
            }
        }
    }
}
