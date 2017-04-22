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

import com.octopus.wallet.m.e.InvalidExportChoice;
import com.octopus.wallet.m.e.ReportExportException;
import com.octopus.wallet.m.p.fl.expoimpo.data.Template;

import java.io.FileNotFoundException;

import me.yoctopus.cac.util.LogUtil;

public class ReportExporter {
    private String TAG = LogUtil.makeTag(ReportExporter.class);
    private Format format;
    private Template template;

    public ReportExporter(Template template,
                          Format format) {
        LogUtil.i(TAG, "ReportExporter: init");
        this.format = format;
        this.template = template;
    }

    public boolean export() throws InvalidExportChoice {
        LogUtil.i(TAG, "export: exporting");
        Exporter exporter;
        switch (format.getFormat()) {
            case Format.CS: {
                try {
                    exporter = new CsvExporter(template);
                } catch (ReportExportException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            case Format.PD: {
                try {
                    exporter = new PdfExporter(template);
                } catch (ReportExportException | FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            case Format.XL: {
                try {
                    exporter = new XlsExporter(template);
                } catch (ReportExportException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            default : {
                throw new InvalidExportChoice();
            }
        }
        exporter.exportData();
        return true;
    }
}
