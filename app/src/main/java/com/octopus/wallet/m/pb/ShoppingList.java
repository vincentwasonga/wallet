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

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yoctopus on 11/12/16.
 */
public class ShoppingList implements Parcelable, S {
    public static final String AT = "@";
    public static final Creator<ShoppingList> CREATOR = new Creator<ShoppingList>() {
        @Override
        public ShoppingList createFromParcel(Parcel source) {
            return new ShoppingList(source);
        }

        @Override
        public ShoppingList[] newArray(int size) {
            return new ShoppingList[size];
        }
    };
    private int id;
    private Date shoppingDate;
    private String name;
    private ArrayList<ListItem> listItems;


    public ShoppingList(String name) {
        this.name = name;
        listItems = new ArrayList<>();
    }

    public ShoppingList(String name,
                        ArrayList<ListItem> listItems) {
        this.name = name;
        this.listItems = listItems;
    }


    public ShoppingList(int id,
                        Date shoppingDate,
                        String name,
                        ArrayList<ListItem> listItems) {
        this.id = id;
        this.shoppingDate = shoppingDate;
        this.name = name;
        this.listItems = listItems;
    }

    protected ShoppingList(Parcel in) {
        this.id = in.readInt();

        long tmpShoppingDate = in.readLong();
        this.shoppingDate = tmpShoppingDate == -1 ? null : new Date(tmpShoppingDate);
        this.name = in.readString();
        this.listItems = in.createTypedArrayList(ListItem.CREATOR);
    }
    public boolean isFinalized() {
        boolean f = true;
        for (ListItem item : getListItems()) {
            f = f && item.isBought();
        }
        return f;
    }
    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getShoppingDate() {
        return shoppingDate;
    }

    public void setShoppingDate(Date shoppingDate) {
        this.shoppingDate = shoppingDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ListItem> getListItems() {
        return listItems;
    }

    public void setListItems(ArrayList<ListItem> listItems) {
        this.listItems = listItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);

        dest.writeLong(this.shoppingDate != null ? this.shoppingDate.getTime() : -1);
        dest.writeString(this.name);
        dest.writeTypedList(this.listItems);
    }

    public static class ListItem implements Parcelable, S {
        public static final Creator<ListItem> CREATOR = new Creator<ListItem>() {
            @Override
            public ListItem createFromParcel(Parcel source) {
                return new ListItem(source);
            }

            @Override
            public ListItem[] newArray(int size) {
                return new ListItem[size];
            }
        };
        private String itemName;
        private int estimatedCost;
        private int actualCost;
        private boolean bought;
        private int id;
        private int number;

        public ListItem(String itemName) {
            this.itemName = itemName;
        }


        public ListItem(String itemName,
                        int estimatedCost) {
            this(itemName);
            this.estimatedCost = estimatedCost;
        }

        public ListItem(String itemName,
                        int estimatedCost,
                        int actualCost,
                        boolean bought,
                        int id) {
            this(itemName, estimatedCost);
            this.actualCost = actualCost;
            this.bought = bought;
            this.id = id;
        }

        protected ListItem(Parcel in) {
            this.itemName = in.readString();
            this.estimatedCost = in.readInt();
            this.actualCost = in.readInt();
            this.bought = in.readByte() != 0;
            this.id = in.readInt();
            this.number = in.readInt();
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
        @Override
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isBought() {
            return bought;
        }

        public void setBought(boolean bought) {
            this.bought = bought;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getEstimatedCost() {
            return estimatedCost;
        }

        public void setEstimatedCost(int estimatedCost) {
            this.estimatedCost = estimatedCost;
        }

        public int getActualCost() {
            return actualCost;
        }

        public void setActualCost(int actualCost) {
            this.actualCost = actualCost;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.itemName);
            dest.writeInt(this.estimatedCost);
            dest.writeInt(this.actualCost);
            dest.writeByte(this.bought ? (byte) 1 : (byte) 0);
            dest.writeInt(this.id);
            dest.writeInt(this.number);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListItem listItem = (ListItem) o;

            if (getEstimatedCost() != listItem.getEstimatedCost()) return false;
            if (getActualCost() != listItem.getActualCost()) return false;
            if (isBought() != listItem.isBought()) return false;
            if (getId() != listItem.getId()) return false;
            if (getNumber() != listItem.getNumber()) return false;
            return getItemName() != null ? getItemName().equals(listItem.getItemName()) : listItem.getItemName() == null;

        }

        @Override
        public int hashCode() {
            int result = getItemName() != null ? getItemName().hashCode() : 0;
            result = 31 * result + getEstimatedCost();
            result = 31 * result + getActualCost();
            result = 31 * result + (isBought() ? 1 : 0);
            result = 31 * result + getId();
            result = 31 * result + getNumber();
            return result;
        }

        @Override
        public String toString() {
            return "ListItem{" +
                    "itemName='" + itemName + '\'' +
                    ", estimatedCost=" + estimatedCost +
                    ", actualCost=" + actualCost +
                    ", bought=" + bought +
                    ", id=" + id +
                    ", number=" + number +
                    '}';
        }
    }
}
