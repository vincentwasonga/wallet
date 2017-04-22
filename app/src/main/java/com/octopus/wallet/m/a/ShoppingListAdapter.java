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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.ShoppingList;
import com.octopus.wallet.m.v.a.ListAnimator;
import com.octopus.wallet.m.v.w.CircularProgressLabel;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.cac.anim.Animator;

public class ShoppingListAdapter extends VBinder<ShoppingList> {
    private OnOptionsClicked optionsClicked;
    private TextView shoppingDate;
    private TextView listNameText;
    private TextView listItemsText;
    private CircularProgressLabel circleView;
    private ProgressBar progressBar;
    private TextView totalAmount;
    private TextView itemsNumber;
    private ImageButton optionsButton;

    public ShoppingListAdapter(SList<ShoppingList> shoppingLists) {
        super(shoppingLists, R.layout.shopping_list);

    }

    private void showTotalAmount(TextView textView,
                                 TextView textView1,
                                 ShoppingList list) {
        int sum = 0;
        int bought = 0;
        for (ShoppingList.ListItem item : list.getListItems()) {
            sum += item.getEstimatedCost();
            if (item.isBought()) {
                bought += item.getEstimatedCost();
            }
        }
        String money = Func.getMoney(sum);
        textView1.setText(money);
        String number = Func.getMoney(bought);
        textView.setText(number);
    }

    private void showListItems(TextView textView,
                               ShoppingList list) {
        List<String> string =
                new ArrayList<>(3);
        int itemSize = list.getListItems().size();
        if (itemSize <= 3) {
            for (ShoppingList.ListItem item : list.getListItems()) {
                String name = item.getItemName()
                        .split(ShoppingList.AT)[0];
                string.add(name);
            }
        } else {
            for (ShoppingList.ListItem item : list.getListItems()
                    .subList(0,
                    3)) {
                String name = item.getItemName()
                        .split(ShoppingList.AT)[0];
                string.add(name);
            }
        }
        textView.setText(getWithCommas((ArrayList<String>) string));
    }

    private String getWithCommas(ArrayList<String> strings) {
        String data = "";
        for (String s : strings) {
            data = data.concat(", " +
                    s);
        }
        if (data.startsWith(",")) {
            data = data.substring(1);
        }
        return data;
    }

    private void showProgress(CircularProgressLabel view,
                              ProgressBar bar,
                              ShoppingList list) {
        int allItems = list.getListItems().size();
        int bought = 0;
        int total = 0;
        int alreadyBought = 0;
        for (ShoppingList.ListItem item : list.getListItems()) {
            total += item.getEstimatedCost();
            if (item.isBought()) {
                bought++;
                alreadyBought += item.getEstimatedCost();
            }
        }
        view.setData(bought,
                allItems);
        bar.setMax(100);
        int progress = (int)
                Func.getPercentage(alreadyBought,
                total);
        bar.setProgress(progress);
    }

    @Override
    public void onInit(View parent) {
        shoppingDate = (TextView) parent
                .findViewById(R.id.shoppingDate);
        progressBar = (ProgressBar) parent
                .findViewById(R.id.progressBar);
        listNameText = (TextView) parent
                .findViewById(R.id.listNameTextView);
        listItemsText = (TextView) parent
                .findViewById(R.id.listItemsTextView);
        circleView = (CircularProgressLabel) parent
                .findViewById(R.id.progress_label);
        totalAmount = (TextView) parent
                .findViewById(R.id.totalAmountTextView);
        itemsNumber = (TextView) parent
                .findViewById(R.id.itemNumberTextView);
        optionsButton = (ImageButton) parent
                .findViewById(R.id.optionsButton);
        Animator animator = new ListAnimator(parent);
        animator.animate();
    }

    @Override
    public void onBind(final ShoppingList list) {
        listNameText.setText(list.getName());
        shoppingDate.setText(Func.getDateddMMM(list.getShoppingDate()));
        showListItems(listItemsText,
                list);
        showTotalAmount(itemsNumber,
                totalAmount,
                list);
        showProgress(circleView,
                progressBar,
                list);
        circleView.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (optionsClicked != null) {
                    optionsClicked.onListClicked(list);
                }
            }
        });
        listNameText.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (optionsClicked != null) {
                    optionsClicked.onListClicked(list);
                }
            }
        });
        optionsButton.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (optionsClicked != null) {
                    optionsClicked.onRequestOptions(list,
                            view);
                }
            }
        });
    }

    public interface OnOptionsClicked {
        void onRequestOptions(ShoppingList shoppingList,
                                     View view);
        void onListClicked(ShoppingList shoppingList);
    }

    public void setOptionsClicked(OnOptionsClicked optionsClicked) {
        this.optionsClicked = optionsClicked;
    }
}
