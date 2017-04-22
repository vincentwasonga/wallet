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

package com.octopus.wallet.m.b;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class SList<T extends S> extends ArrayList<T> {
    public SList(Collection<? extends T> c) {
        super(c);
    }

    public SList() {
        super();
    }


    public SList(int initialCapacity) {
        super(initialCapacity);
    }

    @Nullable
    public T getWithID(int key) {
        if (key < 1) {
            throw new IllegalArgumentException("key less than 1 is not allowed");
        }
        for (T t : this) {
            if (t.getId() == key) {
                return t;
            }
        }
        return null;
    }

    public int getID(T t) {
        if (getWithID(t.getId()) != null) {
            return t.getId();
        } else {
            return -1;
        }
    }

    public int getIndex(int key) {
        int index = 0;
        for (T t : this) {
            if (t.getId() == key) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public int getIndex(T t) {
        return getIndex(t.getId());
    }

    public boolean removeWithID(int id) {
        int index = getIndex(id);
        if (index != -1) {
            super.remove(index);
            return true;
        }
        return false;
    }
    public void delete(T t) {
        removeWithID(t.getId());
    }

    public void update(T t) {
        super.set(getIndex(t), t);
    }
    public boolean save(T t) {
        return add(t);
    }

    public SList<T> reverse() {
        Collections.reverse(this);
        return this;
    }

    public SList<T> shuffle() {
        Collections.shuffle(this);
        return this;
    }

    public SList<T> reverseWithID() {
        Collections.sort(this,
                new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1.getId() < o2.getId()) {
                    return -1;
                } else if (o1.getId() > o2.getId()) {
                    return 1;
                }
                return 0;
            }
        });
        return reverse();
    }

    public SList<T> sortWithID() {
        Collections.sort(this,
                new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1.getId() < o2.getId()) {
                    return -1;
                } else if (o1.getId() > o2.getId()) {
                    return 1;
                }
                return 0;
            }
        });
        return this;
    }
}
