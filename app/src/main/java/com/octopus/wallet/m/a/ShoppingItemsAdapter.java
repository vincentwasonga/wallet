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

package com.octopus.wallet.m.a;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.ShoppingList;
import com.octopus.wallet.m.v.a.ListAnimator;

import java.util.ArrayList;

import me.yoctopus.cac.anim.Animator;


public class ShoppingItemsAdapter extends VBinder<ShoppingList.ListItem> {
    private CheckBox listItem;
    private TextView costTextView;
    private ImageButton delete;


    public ShoppingItemsAdapter(SList<ShoppingList.ListItem> listItems) {
        super(listItems, R.layout.shopping_item);

    }

    @Override
    public void onInit(View parent) {
        listItem = (CheckBox) parent
                .findViewById(R.id.shoppingListItem);
        costTextView = (TextView) parent
                .findViewById(R.id.costTextView);
        delete = (ImageButton)
                parent.findViewById(R.id.delete_button);
        Animator animator =
                new ListAnimator(parent);
        animator.animate();
    }

    @Override
    public void onBind(ShoppingList.ListItem item,
                       final int position) {
        super.onBind(item, position);
        String itemName = item.getItemName();
        String[] utils = itemName.split(ShoppingList.AT);
        int quantity = Integer.parseInt(utils[1]);
        int cost = item.getEstimatedCost() / quantity;
        String text = utils[0] +
                " " +
                ShoppingList.AT +
                " (" + quantity +
                " X " +
                Func.getMoney(cost) +
                ") ";
        listItem.setChecked(item.isBought());
        listItem.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        getList().get(position).setBought(isChecked);
                    }
                });
        listItem.setText(text);
        costTextView.setText(Func.getMoney(
                item.getEstimatedCost()));
        delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getList().remove(position);

                    }
                });
    }

    @Override
    public void onBind(ShoppingList.ListItem item) {

    }
    public ArrayList<ShoppingList.ListItem> getListItems() {
        return new ArrayList<>(getList());
    }
}
