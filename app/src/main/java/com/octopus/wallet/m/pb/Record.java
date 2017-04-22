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

import android.os.Parcel;
import android.os.Parcelable;

import com.octopus.wallet.m.b.S;
import com.octopus.wallet.m.h.utl.Func;

public class Record implements Parcelable, S {
    public final static long DEFAULT_MILLIS = 0;
    public final static String INCOME = "I";
    private String type;
    private String name;
    private int id;

    private int Amount;
    private DescriptionSet descriptionSet;
    private long date_millis;

    public Record(int id,
                  Category category,
                  int amount,
                  long date_millis,
                  DescriptionSet descriptionSet) {
        this.setId(id);
        this.type = category.type;
        this.name = category.name;
        this.setAmount(amount);
        this.descriptionSet = descriptionSet;
        this.setDate_millis(date_millis);
    }

    public Record(Category category,
                  int amount,
                  DescriptionSet descriptionSet) {
        this.name = category.name;
        this.type = category.type;
        setAmount(amount);
        date_millis = DEFAULT_MILLIS;
        this.descriptionSet = descriptionSet;
    }

    public Record() {

    }

    public String getType() {
        return type;
    }
    public Category getCategory() {
        return new Category(type, name);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public boolean isIncome() {
        return type.equals(INCOME);
    }

    public boolean isExpense() {
        return !isIncome();
    }

    public String getDateString() {
        return Func.getDateMMSS(getDate_millis());
    }




    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        if (this.isIncome()) {
            return "Income";
        } else {
            return "Expense";
        }
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public long getDate_millis() {
        return date_millis;
    }

    public void setDate_millis(long date_millis) {
        this.date_millis = date_millis;
    }

    public DescriptionSet getDescriptionSet() {
        return descriptionSet;
    }

    public void setDescriptionSet(DescriptionSet descriptionSet) {
        this.descriptionSet = descriptionSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (id != record.id) return false;
        if (Amount != record.Amount) return false;
        if (date_millis != record.date_millis) return false;

        if (descriptionSet != null ?
                !descriptionSet.equals(record.descriptionSet) : record.descriptionSet != null)
            return false;
        return true;

    }

    public static class DescriptionSet implements Parcelable {
        public static final String DEFAULT = " ";
        public static final Creator<DescriptionSet> CREATOR = new Creator<DescriptionSet>() {
            @Override
            public DescriptionSet createFromParcel(Parcel source) {
                return new DescriptionSet(source);
            }

            @Override
            public DescriptionSet[] newArray(int size) {
                return new DescriptionSet[size];
            }
        };
        private String description;
        private String locationName;
        private String receiptName;

        public DescriptionSet(String description,
                              String locationName,
                              String receiptName) {
            this.description = description;
            this.locationName = locationName;
            this.receiptName = receiptName;
        }

        public DescriptionSet() {
            this(DEFAULT, DEFAULT, DEFAULT);

        }

        protected DescriptionSet(Parcel in) {
            this.description = in.readString();
            this.locationName = in.readString();
            this.receiptName = in.readString();
        }

        public boolean isDefault() {
            return description.equals(DEFAULT) &&
                    locationName.equals(DEFAULT) &&
                    receiptName.equals(DEFAULT);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public String getReceiptName() {
            return receiptName;
        }

        public void setReceiptName(String receiptName) {
            this.receiptName = receiptName;
        }

        @Override
        public String toString() {
            return "" + description + "\t" + locationName + "\t" + receiptName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.description);
            dest.writeString(this.locationName);
            dest.writeString(this.receiptName);

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DescriptionSet)) return false;

            DescriptionSet that = (DescriptionSet) o;

            if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
                return false;
            if (getLocationName() != null ? !getLocationName().equals(that.getLocationName()) : that.getLocationName() != null)
                return false;
            return getReceiptName() != null ? getReceiptName().equals(that.getReceiptName()) : that.getReceiptName() == null;

        }

        @Override
        public int hashCode() {
            int result = getDescription() != null ? getDescription().hashCode() : 0;
            result = 31 * result + (getLocationName() != null ? getLocationName().hashCode() : 0);
            result = 31 * result + (getReceiptName() != null ? getReceiptName().hashCode() : 0);
            return result;
        }
    }

    public static class Category implements Parcelable, S {
        public static final String DEFAULT = "DEFAULT";
        public static String INCOME_TYPE = "I";
        public static String EXPENSE_TYPE = "E";
        private String type;
        private String name;
        private int id;

        public Category(String type,
                        String name) {
            this.setType(type);
            this.setName(name);

        }

        public boolean isIncome() {
            return this.type.equals(INCOME_TYPE);
        }

        public boolean isExpense() {
            return this.type.equals(EXPENSE_TYPE);
        }

        public boolean isDefault() {
            return this.type.equals(DEFAULT);
        }

        @Override
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }



        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Category category = (Category) o;

            return getType() != null ?
                    getType().equals(category.getType()) :
                    category.getType() == null &&
                            (getName() != null ?
                                    getName().equals(category.getName()) :
                                    category.getName() == null);
        }

        @Override
        public int hashCode() {
            int result = getType() != null ? getType().hashCode() : 0;
            result = 31 * result + (getName() != null ? getName().hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Category{" +
                    "type='" + type + '\'' +
                    ", name='" + name + '\'' +

                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.type);
            dest.writeString(this.name);
            dest.writeInt(this.id);
        }

        protected Category(Parcel in) {
            this.type = in.readString();
            this.name = in.readString();
            this.id = in.readInt();
        }

        public static final Creator<Category> CREATOR = new Creator<Category>() {
            @Override
            public Category createFromParcel(Parcel source) {
                return new Category(source);
            }

            @Override
            public Category[] newArray(int size) {
                return new Category[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeInt(this.id);
        dest.writeInt(this.Amount);
        dest.writeParcelable(this.descriptionSet, flags);
        dest.writeLong(this.date_millis);
    }

    protected Record(Parcel in) {
        this.type = in.readString();
        this.name = in.readString();
        this.id = in.readInt();
        this.Amount = in.readInt();
        this.descriptionSet = in.readParcelable(DescriptionSet.class.getClassLoader());
        this.date_millis = in.readLong();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}
