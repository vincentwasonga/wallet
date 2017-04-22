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

import com.octopus.wallet.m.pb.Record;

/**
 * Created by octopus on 9/12/16.
 */
public class UpdatePackage {
    private Record record;
    private Record old;

    public UpdatePackage(Record record) {
        this.record = record;
        Record.Category category = new Record.Category(record.getType(),
                record.getName());
        this.old = new Record(category,
                record.getAmount(),
                record.getDescriptionSet());
    }
    public void setCategory(Record.Category category) {
        record.setType(category.getType());
        record.setName(category.getName());
    }

    public void setDescription(Record.DescriptionSet description) {
        this.record.setDescriptionSet(description);
    }

    public void setAmount(int amount) {
        this.record.setAmount(amount);
    }

    public void setDateMillis(long millis) {
        this.record.setDate_millis(millis);
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Record getOld() {
        return old;
    }




}
