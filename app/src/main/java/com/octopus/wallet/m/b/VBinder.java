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

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class VBinder<V extends S>
        extends RecyclerView.Adapter<VBinder<V>.Holder> {
    private SList<V> list;
    @LayoutRes
    private int layout;
    private View parent;

    public VBinder(SList<V> list,
                   @LayoutRes int layout) {
        this.list = list;
        this.layout = layout;
    }

    public void add(V v) {
        list.add(v);
        notifyItemInserted(list.size() - 1);
    }

    public void add(SList<V> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(V v) {
        int index = list.getIndex(v);
        list.remove(index);
        notifyItemRemoved(index);
    }

    public void update(V v) {
        list.set(list.getIndex(v), v);
        notifyDataSetChanged();
    }

    public void updateAll(SList<V> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent,
                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout,
                        parent,
                        false);
        return new Holder(view);
    }


    @Override
    public void onBindViewHolder(Holder holder,
                                 int position) {
        V v = list.get(position);
        holder.bind(v);
        onBind(v, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public abstract void onInit(View parent);

    public abstract void onBind(V v);

    public void onBind(V v, int position) {

    }

    public List<V> getList() {
        return list;
    }

    public void setList(SList<V> list) {
        this.list = list;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public View getParent() {
        return parent;
    }

    class Holder extends RecyclerView.ViewHolder {
        Holder(View itemView) {
            super(itemView);
            parent = itemView;
            onInit(parent);
        }

        void bind(V v) {
            onBind(v);
        }
    }
}

