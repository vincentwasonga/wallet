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

package com.octopus.wallet.u.a.mo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.octopus.wallet.R;
import com.octopus.wallet.m.a.ShoppingItemsAdapter;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.h.scdlr.ShoppingReminder;
import com.octopus.wallet.m.h.utl.Func;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import com.octopus.wallet.m.pb.ShoppingList;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.t.DateTimeActivity;

import java.util.ArrayList;
import java.util.Date;

public class NewShoppingListActivity extends BActivity {
    private final int dateRequestCode =
            1;
    private ShoppingList shoppingList;
    private Button newListItemBtn;
    private Button addDateBtn;
    private Button saveListBtn;
    private TextView totalCostTextView;
    private LinearLayout newItemLayout;
    private EditText itemCostEditText;
    private EditText quantityEditText;
    private EditText itemNameEditText;
    private RecyclerView shoppingItems;
    private TextView timesTextView;
    private EditText nameEditText;
    private boolean isUpdating =
            false;
    private View.OnClickListener updateListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateList();
                }
            };
    private AdView adView;
    private ShoppingItemsAdapter adapter;
    private View.OnClickListener saveItemListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addListItem();

                }
            };
    private View.OnClickListener addItemListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newItemLayout.setVisibility(View.VISIBLE);
                    newListItemBtn.setText(R.string.save_item);
                    newListItemBtn.setOnClickListener(saveItemListener);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shopping_list);
        setHasBackButton(true);
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        timesTextView = (TextView) getView(
                                R.id.timesTextView);
                        timesTextView.setText("X " +
                                Func.getCurrency());
                        newItemLayout = (LinearLayout) getView(
                                R.id.newItemLayout);
                        itemCostEditText = (EditText) getView(
                                R.id.itemCostEditText);
                        quantityEditText = (EditText) getView(
                                R.id.quantityEditText);
                        itemNameEditText = (EditText) getView(
                                R.id.itemNameEditText);
                        newListItemBtn = (Button) getView(
                                R.id.new_list_item_btn);
                        totalCostTextView = (TextView) getView(
                                R.id.totalCostTextView);
                        addDateBtn = (Button) getView(
                                R.id.add_reminder_btn);
                        addDateBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        addDate();
                                    }
                                });
                        saveListBtn = (Button) getView(R.id.saveListBtn);
                        saveListBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        saveList();
                                    }
                                });
                        newListItemBtn.setOnClickListener(addItemListener);
                        shoppingItems = (RecyclerView) getView(
                                R.id.shopping_items);
                        RecyclerView.LayoutManager layoutManager =
                                new LinearLayoutManager(getActivity());
                        shoppingItems.setLayoutManager(layoutManager);
                        nameEditText = (EditText) getView(
                                R.id.nameEditText);
                        shoppingList =
                                new ShoppingList("new list");
                        shoppingList.setShoppingDate(
                                new Date());
                        adapter = new ShoppingItemsAdapter(
                                new SList<>(
                                        new ArrayList<ShoppingList.ListItem>()));
                        shoppingItems.setAdapter(adapter);
                        Intent intent = getIntent();
                        if (intent.hasExtra("shopping_list")) {
                            shoppingList =
                                    intent.getParcelableExtra(
                                            "shopping_list");
                            setTitle(shoppingList.getName());
                            nameEditText.setText(
                                    shoppingList.getName());
                            isUpdating =
                                    true;
                            addDateBtn.setText(
                                    Func.getDateMPassed(
                                            shoppingList.getShoppingDate()));
                            saveListBtn.setText(
                                    R.string.update_list);
                            saveListBtn.setOnClickListener(
                                    updateListener);
                            adapter.add(new SList<>(
                                    shoppingList.getListItems()));
                            showTotalCost();
                        }
                        adView = (AdView)
                                findViewById(R.id.ad_view);
                        AdRequest adRequest =
                                new AdRequest.Builder()
                                        .build();
                        adView.loadAd(adRequest);
                    }
                };
        executeRunnable(runnable);
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


    private void addDate() {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = getIntent(
                                DateTimeActivity.class);
                        intent.putExtra(DateTimeActivity.RETURN_TYPE,
                                DateTimeActivity.RETURN_AFTER);
                        startActivityForResult(intent,
                                dateRequestCode);
                    }
                };
        executeRunnable(runnable);
    }

    private void showTotalCost() {
        int sum = 0;
        for (ShoppingList.ListItem item :
                shoppingList.getListItems()) {
            sum +=
                    item.getEstimatedCost();
        }
        totalCostTextView.setText(
                Func.getMoney(sum));
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        Date date;
        switch (requestCode) {
            case dateRequestCode: {
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra(DateTimeActivity.RESULT_LABEL)) {
                        long time =
                                data.getLongExtra(
                                        DateTimeActivity.RESULT_LABEL,
                                        new Date().getTime());
                        date = Func.getDate(time);
                    } else {
                        date = new Date();
                    }
                    shoppingList.setShoppingDate(
                            date);
                }
                break;
            }
        }
        super.onActivityResult(requestCode,
                resultCode,
                data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("shopping", shoppingList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        shoppingList = savedInstanceState.getParcelable("shopping");
    }

    private void saveList() {
        if (nameEditText.getText() == null ||
                nameEditText.getText().toString().isEmpty()) {
            shakeView(nameEditText);
            notifyToast("Please enter list name");
            return;
        }
        shoppingList.setName(
                nameEditText.getText().toString());
        shoppingList.setListItems(adapter.getListItems());
        getDatabase().save(shoppingList);
        scheduleList();
        finish();
    }

    private void updateList() {
        if (nameEditText.getText() == null ||
                nameEditText.getText().toString().isEmpty()) {
            shakeView(nameEditText);
            notifyToast("Please enter list name");
            return;
        }
        shoppingList.setName(
                nameEditText.getText().toString());
        shoppingList.setListItems(adapter.getListItems());
        getDatabase().update(shoppingList);
        scheduleList();
        finish();
    }

    private void scheduleList() {
        Boolean shopping;
        try {
            shopping = getPreferences().getPreference(
                    new Pref<>("shopping_reminder", true));
            if ( shopping) {
                        new ShoppingReminder(this,
                                shoppingList).schedule();
            }
        } catch (InvalidPreferenceType e) {
            e.printStackTrace();
        }
    }

    private void addListItem() {
        if (itemNameEditText.getText() ==
                null ||
                itemNameEditText.getText()
                        .toString().isEmpty()) {
            shakeView(itemNameEditText);
            notifyToast("Please enter the " +
                            "name of the item");
            return;
        }
        if (quantityEditText.getText() ==
                null ||
                quantityEditText.getText()
                        .toString().isEmpty()) {
            shakeView(quantityEditText);
            notifyToast("Please enter the " +
                            "quantity for this item");
            return;
        }
        if (itemCostEditText.getText() ==
                null ||
                itemCostEditText.getText()
                        .toString().isEmpty()) {
            shakeView(itemCostEditText);
            notifyToast("Please enter the " +
                            "cost of this item");
            return;
        }
        String itemName =
                itemNameEditText.getText().toString();
        for (ShoppingList.ListItem item :
                shoppingList.getListItems()) {
            if (item.getItemName().equals(itemName)) {
                notifyToast("An item with same " +
                                "name already exists");
                return;
            }
        }
        itemNameEditText.setText(null);
        String quantity =
                quantityEditText.getText().toString();
        quantityEditText.setText("1");
        String itemCost =
                itemCostEditText.getText().toString();
        itemCostEditText.setText(null);
        int number = Integer.parseInt(quantity);
        int cost = Func.convertToDefaultCurrency(
                Integer.parseInt(itemCost));
        int totalCost = number * cost;
        String newName = itemName +
                ShoppingList.AT +
                quantity;
        ShoppingList.ListItem listItem =
                new ShoppingList.ListItem(newName,
                        totalCost);
        shoppingList.getListItems().add(
                listItem);
        adapter.add(listItem);
        newItemLayout.setVisibility(
                View.GONE);
        newListItemBtn.setText(R.string.add_item);
        newListItemBtn.setOnClickListener(
                addItemListener);
        showTotalCost();
    }

}
