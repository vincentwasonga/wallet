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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.octopus.wallet.R;
import com.octopus.wallet.m.a.ShoppingListAdapter;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.p.fl.expoimpo.data.Shopping;
import com.octopus.wallet.m.p.fl.expoimpo.data.Template;
import com.octopus.wallet.m.p.fl.expoimpo.exp.Format;
import com.octopus.wallet.m.p.fl.fmodels.FExportData;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.pb.ShoppingList;
import com.octopus.wallet.m.v.w.PopupList;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.t.TransactionActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.tx.Tx;

public class ShoppingListActivity extends BActivity {
    private final int DELETE_SHOPPING_LIST = 2;
    private final int UPDATE_SHOPPING_LIST = 3;
    private final int PRINT_SHOPPING_LIST = 4;
    private final int FINALIZE_SHOPPING_LIST = 5;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.shopping_list)
    RecyclerView shoppingList;
    @OnClick(R.id.fab)
    public void onViewClicked() {
        startActivity(getIntent(
                NewShoppingListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        setHasBackButton(true);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this);
        shoppingList.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                deleteAllLists();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getModuleData();
            }
        };
        getHandler().post(runnable);
    }

    private void getModuleData() {
        displayShoppingLists();
    }

    private void displayShoppingLists() {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        SList<ShoppingList> lists =
                                getDatabase().getShoppingLists();
                        if (lists.isEmpty()) {
                            return;
                        }
                        ShoppingListAdapter adapter =
                                new ShoppingListAdapter(lists);
                        adapter.setOptionsClicked(
                                new ShoppingListAdapter.OnOptionsClicked() {
                                    @Override
                                    public void onRequestOptions(ShoppingList shoppingList,
                                                                 View view) {
                                        actionRequestOptions(shoppingList,
                                                view);
                                    }

                                    @Override
                                    public void onListClicked(ShoppingList shoppingList) {
                                        Intent intent = getIntent(
                                                NewShoppingListActivity.class);
                                        intent.putExtra("shopping_list",
                                                shoppingList);
                                        startActivity(intent);
                                    }
                                });
                        shoppingList.setAdapter(adapter);
                    }
                };
        getHandler().post(runnable);
    }

    private void actionRequestOptions(final ShoppingList shoppingList,
                                      View view) {
        PopupList popupList = new PopupList(this,
                view);
        popupList.setOnItemClickListener(
                new PopupList.OnItemClickListener() {
                    @Override
                    public boolean onItemClick(int itemId) {
                        doChosenOption(shoppingList,
                                itemId);
                        return false;
                    }
                });
        popupList.addItem(UPDATE_SHOPPING_LIST,
                "Update ");
        popupList.addItem(PRINT_SHOPPING_LIST,
                "Print ");
        popupList.addItem(DELETE_SHOPPING_LIST,
                "Delete ");
        if (shoppingList.isFinalized()) {
            popupList.addItem(FINALIZE_SHOPPING_LIST,
                    "Finalize");
        }
        popupList.show();
    }

    private void doChosenOption(ShoppingList list,
                                int option) {
        switch (option) {
            case DELETE_SHOPPING_LIST: {
                deleteList(list);
                break;
            }
            case PRINT_SHOPPING_LIST: {
                printList(list);
                break;
            }
            case UPDATE_SHOPPING_LIST: {
                updateList(list);
                break;
            }
            case FINALIZE_SHOPPING_LIST: {
                finalizeList(list);
            }
        }
    }

    private void finalizeList(final ShoppingList list) {
        notifyDialog("Mark final",
                "Enables you to add this list" +
                        " as a record since all its " +
                        "items have been acquired " +
                        "add after wards " +
                        "deletes it",
                new NDialog.DButton("Proceed",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                recordAndDelete(list);
                            }
                        }),
                null);
    }

    private void recordAndDelete(final ShoppingList list) {
        Record.Category category =
                new Record.Category(
                        Record.Category.EXPENSE_TYPE,
                        list.getName());
        getDatabase().save(category);
        int sum = 0;
        for (ShoppingList.ListItem item :
                list.getListItems()) {
            sum += item.getEstimatedCost();
        }
        Record.DescriptionSet set =
                new Record.DescriptionSet();
        List<String> string =
                new ArrayList<>();
        for (ShoppingList.ListItem item :
                list.getListItems()) {
            String name = item.getItemName()
                    .split(ShoppingList.AT)[0];
            string.add(name);
        }
        set.setDescription(getWithCommas(
                (ArrayList<String>) string));
        Record record = new Record(category,
                sum,
                set);
        saveRecordAndDeleteList(record,
                list);
    }

    private void saveRecordAndDeleteList(final Record record,
                                         final ShoppingList list) {
        transact(
                new TransactionActivity.AddExpense(this,
                        100,
                        record),
                new Tx.OnComplete<Boolean>() {
                    @Override
                    public void onComplete(int id,
                                           Boolean success) {
                        notifyToast("List finalized successfully");
                        getDatabase().delete(list);
                        getModuleData();
                    }
                });
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

    private void deleteAllLists() {
        notifyDialog("Delete All Lists",
                "Are you sure to delete all lists",
                new NDialog.DButton("Confirm",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                doConfirmedDelete();
                            }
                        }),
                null);
    }

    private void doConfirmedDelete() {
        getDatabase().deleteAllShoppingLists();
    }

    private void updateList(ShoppingList list) {
        Intent intent = getIntent(
                NewShoppingListActivity.class);
        intent.putExtra("shopping_list",
                list);
        startActivity(intent);
    }

    private void deleteList(final ShoppingList shoppingList) {
        notifyDialog("Warning",
                "You are about to delete a list",
                new NDialog.DButton("Confirm",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                getDatabase().delete(shoppingList);
                                getModuleData();
                            }
                        }),
                null);
    }

    private void printList(ShoppingList shoppingList) {
        Template data = new Shopping(shoppingList);
        transact(
                new ExportActivity.SaveFile(this,
                        100,
                        data,
                        Format.PDF),
                new Tx.OnComplete<FExportData>() {
                    @Override
                    public void onComplete(int id,
                                           FExportData exportData) {
                        if (exportData.isExported()) {
                            notifyDialog("List printed",
                                    "Would you like to preview",
                                    new NDialog.DButton("Yes",
                                            new NDialog.DButton.BListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent =
                                                            getIntent(ReportsActivity.class);
                                                    startActivity(intent);
                                                }
                                            }),
                                    new NDialog.DButton("No",
                                            new NDialog.DButton.BListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            }));
                        } else {
                            notifyDialog("Error",
                                    "Printing Failed",
                                    new NDialog.DButton("Try again",
                                            new NDialog.DButton.BListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            }),
                                    null);
                        }
                        dismissNotification();
                    }
                },
                1000);
        notifyProgress("printing, please wait...");
    }

}
