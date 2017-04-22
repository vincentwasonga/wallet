/*
 * ﻿Copyright [2016] [Peter Vincent]
 * Licensed under the Apache License, Version 2.0 (Personal Budget);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.octopus.budget.activities.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.octopus.budget.BudgetApp;
import com.octopus.budget.R;
import com.octopus.budget.models.Item;
import com.octopus.budget.models.Transaction;
import com.octopus.budget.models.adapters.ItemsRecycleAdapter;
import com.octopus.budget.models.adapters.RecyclerDivider;
import com.octopus.budget.models.adapters.RecyclerTouchListener;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements Transaction.TransactionCompleteListener {
    private final String DefaultItem = " ";
    private boolean isChosen = false;
    private boolean isEditing = false;
    private String category;
    private RecyclerView itemList;
    private EditText itemEditText;
    private ArrayList<Item> items;
    private Button addItemBtn;
    private FrameLayout view;
    private ItemsRecycleAdapter mAdapter;
    private String TAG = "CategoryActivity";
    private RecyclerView.LayoutManager mLayoutManager;
    private BudgetApp app;
    private Intent intent;
    private View.OnClickListener saveNewItem = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: savenewitem");
            String itemName = itemEditText.getText() != null ? itemEditText.getText().toString() : DefaultItem;
            if (!itemName.equals(DefaultItem)) {
                saveNewItem(itemName);
                itemEditText.setText(DefaultItem);
            }
        }
    };
    private View.OnClickListener shownewItemEditText = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: shownewitemedit");
            itemEditText.setVisibility(View.VISIBLE);
            addItemBtn.setText("Save ...");
            addItemBtn.setOnClickListener(saveNewItem);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Pick Category...");
        setContentView(R.layout.activity_items);
        app = (BudgetApp) getApplication();
        this.view = (FrameLayout) findViewById(R.id.view);
        this.addItemBtn = (Button) findViewById(R.id.addItemBtn);
        this.itemEditText = (EditText) findViewById(R.id.itemEditText);
        this.itemList = (RecyclerView) findViewById(R.id.itemList);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        intent = getIntent();
        if (intent.hasExtra("items")) {
            items = intent.getParcelableArrayListExtra("items");
        }
        if (intent.hasExtra("editable")) {
            isEditing = true;
        }

    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i(TAG, "onPostCreate: ");
        itemList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        itemList.setLayoutManager(mLayoutManager);
        listItems(prepareAllItems());
        addItemBtn.setOnClickListener(shownewItemEditText);

    }
    private ArrayList<Item> prepareAllItems() {
        Log.i(TAG, "prepareAllRecords: ");
        return app.getAccount().getItems();
    }

    private void listItems(ArrayList<Item> items) {
        Log.i(TAG, "listItems: ");
        mAdapter = new ItemsRecycleAdapter(items);
        mAdapter.setSeeEditBtns(isEditing);
        itemList.addItemDecoration(new RecyclerDivider(this, LinearLayoutManager.VERTICAL));
        itemList.addOnItemTouchListener(new RecyclerTouchListener(this, itemList, new RecyclerTouchListener.RecordClickListener() {
            @Override
            public void onClick(View view, int position) {
                displayDetail(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        itemList.setAdapter(mAdapter);
    }
    private void displayDetail(int i) {

        Log.i(TAG, "displayDetail: ");
        if (isEditing) {
            // TODO
            return;
        }
        if (!isChosen) {
            category = items.get(i).getName();
            isChosen = true;
        }
        finish();
    }
    private void saveNewItem(String itemName) {
        Log.i(TAG, "saveNewItem: ");

        Item item = new Item(Item.DEFAULT, itemName);
        app.getAccount().getItems().add(item);
        items = app.getAccount().getItems();
        app.getAccount().addNewItem(new Item(Item.DEFAULT, itemName));
        app.getAccount().setOnTransactionCompleteListener(this);

    }


    @Override
    public void finish() {
        Intent intent = new Intent();
        if (category != null) {
            intent.putExtra("category", category);
        }
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onTransactionComplete(int id, boolean success) {
        switch (id) {
            case Transaction.ITEMS : {
                if (success) {
                    addItemBtn.setText("Add ...");
                    itemEditText.setVisibility(View.INVISIBLE);
                    listItems(prepareAllItems());
                    addItemBtn.setOnClickListener(shownewItemEditText);
                }
                break;
            }
        }
    }
}
