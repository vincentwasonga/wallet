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

import com.octopus.wallet.m.e.ReportExportException;
import com.octopus.wallet.m.p.fl.Dir;
import com.octopus.wallet.m.p.fl.Ext;
import com.octopus.wallet.m.p.fl.FUri;
import com.octopus.wallet.m.p.fl.expoimpo.data.Template;

import java.io.File;

abstract class Exporter {
    private Template template;
    private String fileName;
    Exporter(Template template,
             Ext ext) throws ReportExportException {
        this.template = template;
        FUri fUri = new FUri(Dir.Reports, template.getName());
        fUri.setExt(ext);
        fileName = fUri.getUri().toString();
        testEnvironment();
    }

    private boolean testEnvironment() throws ReportExportException {
        if (!Dir.isWritable()) {
            throw new ReportExportException();
        }
        File file = new File(fileName);
        if (!file.exists()) {
            throw new ReportExportException("Unable to create file");
        }
        return true;
    }

    public String getFileName() {
        return fileName;
    }

    public void exportData() {
        export(template);
    }

    public abstract boolean export(Template template);


    public boolean isWriteBalance() {
        return template.isPrintBalance();
    }
}
