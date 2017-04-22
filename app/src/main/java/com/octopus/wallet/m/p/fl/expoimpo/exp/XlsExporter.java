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
import com.octopus.wallet.m.p.fl.Ext;
import com.octopus.wallet.m.p.fl.expoimpo.data.Basic;
import com.octopus.wallet.m.p.fl.expoimpo.data.Ledger;
import com.octopus.wallet.m.p.fl.expoimpo.data.Template;

import java.io.IOException;

import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by octopus on 10/1/16.
 */
public class XlsExporter extends Exporter {

    public XlsExporter(Template template) throws ReportExportException {
        super(template, Ext.XLS);

    }
    @Override
    public boolean export(Template template) {
        if (template instanceof Basic) {
            exportData((Basic) template);
        }
        else if (template instanceof Ledger) {
            exportData((Ledger) template);
        }
        return true;
    }

    private void exportData(Basic data) {

        try {
            Workbook workbook = Workbook.getWorkbook(
                    new java.io.File(getFileName()));
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }


    }
    private void exportData(Ledger data) {

    }
}
