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

package com.octopus.wallet.m.p.fl.fmodels;

import com.octopus.wallet.m.p.fl.FData;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by octopus on 10/22/16.
 */
public class FImportData extends FData {
    private ArrayList<File> files;
    public FImportData(String nameOfData) {
        super(nameOfData);
        files = new ArrayList<>();
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return getNameOfData();
    }
}
