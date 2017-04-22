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

import com.octopus.wallet.m.b.S;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by octopus on 10/4/16.
 */
public class FGroup implements S {
    private Dir dir;
    private ArrayList<SFile> files;


    public FGroup(Dir dir) {
        this.dir = dir;
        files = new ArrayList<>();
    }
    public String getShortDirectoryName() {
        File file = new File(dir.getName());
        return file.getName();
    }

    public boolean hasFiles(){
        return !getFiles().isEmpty();
    }

    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public ArrayList<SFile> getFiles() {
        return files;
    }



    @Override
    public int getId() {
        return 0;
    }
}
