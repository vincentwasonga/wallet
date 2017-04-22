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

package com.octopus.wallet.u.a.t;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.octopus.wallet.R;
import com.octopus.wallet.m.a.AdapterDivider;
import com.octopus.wallet.m.a.CategoriesAdapter;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.u.a.BActivity;

import java.util.List;

import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.util.LogUtil;

public class CategoryActivity extends BActivity {
    private static final String IncomeType = "income";
    private static final String ExpenseType = "Expense";
    private String TAG = LogUtil.makeTag(CategoryActivity.class);
    private boolean isChosen = false;
    private boolean isEditing = false;
    private Record.Category category;
    private RecyclerView categoryList;
    private EditText categoryEditText;
    private TextInputLayout textLayout;
    private Button addCategoryBtn;
    private List<Record.Category> categories;
    private String type;
    private View.OnClickListener shownewItemEditText =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.i(TAG,
                            "onClick: shownewitemedit");
                    textLayout.setVisibility(View.VISIBLE);
                    addCategoryBtn.setText("Save ...");
                    addCategoryBtn.setOnClickListener(saveNewItem);
                }
            };
    private View.OnClickListener saveNewItem =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtil.i(TAG,
                            "onClick: savenewitem");
                    String defaultItem = " ";
                    String itemName = categoryEditText.getText() != null ?
                            categoryEditText.getText().toString() :
                            defaultItem;
                    if (!itemName.equals(defaultItem)) {
                        saveNewCategory(itemName);
                        categoryEditText.setText(defaultItem);
                    }
                }
            };
    private CategoriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        this.addCategoryBtn = (Button) getView(
                R.id.addItemBtn);
        this.categoryEditText = (EditText) getView(
                R.id.itemEditText);
        this.categoryList = (RecyclerView) getView(
                R.id.itemList);
        textLayout = (TextInputLayout) getView(
                R.id.textLayout);
        setTitle("Select Category");
        setHasBackButton(true);
        categories = getCategories();
        Intent intent = getIntent();
        if (intent.hasExtra("type")) {
            String type =
                    intent.getStringExtra("type");
            switch (type) {
                case "income": {
                    this.type =
                            IncomeType;
                    categories = getCategories(0);
                    break;
                }
                case "expense": {
                    this.type =
                            ExpenseType;
                    categories = getCategories(1);
                    break;
                }
            }
        }
        if (intent.hasExtra("editable")) {
            setTitle("Category Settings");
            isEditing = true;
            addCategoryBtn.setVisibility(View.GONE);
        }

    }
    public SList<Record.Category> getCategories(int type) {
        List<Record.Category> list = categories;
        SList<Record.Category> list1 = new SList<>();
        switch (type) {
            case 0 :
                for (Record.Category category : list) {
                    if (category.isIncome()) {
                        list1.add(category);
                    }
                }
                break;
            case 1:
                for (Record.Category category : list) {
                    if (category.isExpense()) {
                        list1.add(category);
                    }
                }
                break;
        }
        return list1;
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LogUtil.i(TAG,
                "onPostCreate: ");
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(this);
        categoryList.setLayoutManager(mLayoutManager);
        listCategories(new SList<>(categories));
        addCategoryBtn.setOnClickListener(shownewItemEditText);
    }

    private void listCategories(
            SList<Record.Category> categories) {
        LogUtil.i(TAG,
                "listCategories: ");
        adapter =
                new CategoriesAdapter(
                        categories,
                        new OnCategoryClickedListener() {
                            @Override
                            public void onClicked(
                                    Record.Category category) {
                                LogUtil.i(TAG,
                                        "displayDetail: ");
                                if (isEditing) {
                                    updateCategory(
                                            category);
                                    return;
                                }
                                if (!isChosen) {
                                    CategoryActivity.this.category =
                                            category;
                                    isChosen = true;
                                }
                                finish();
                            }

                            @Override
                            public void onUpdate(
                                    Record.Category category) {
                                updateCategory(
                                        category);
                            }

                        });
        adapter.setSeeEditBtns(
                isEditing);
        categoryList.addItemDecoration(new AdapterDivider(this,
                LinearLayout.VERTICAL));
        categoryList.setAdapter(adapter);
    }

    private void updateCategory(final Record.Category category) {
        notifyDialog("Provide a name for the category",
                null,
                new NDialog.DButton("OK",
                        null),
                new NDialog.DButton("Delete Category",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                getDatabase().delete(category);
                                adapter.remove(category);
                            }
                        }),
                new NDialog.OnAnswer() {
                    @Override
                    public void onAnswer(String answer) {
                        if (answer.isEmpty()) {
                            notifyToast("Empty response");
                            return;
                        }
                       category.setName(answer);
                        getDatabase().update(category);
                        adapter.update(category);
                    }
                });
    }

    private void saveNewCategory(String categoryName) {
        LogUtil.i(TAG,
                "saveNewCategory: ");
        for (Record.Category category : getCategories()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                notifyToast("Please use another name," +
                        " name exists");
                return;
            }
        }
        Record.Category category = null;
        switch (type) {
            case IncomeType: {
                category = new Record.Category(
                        Record.Category.INCOME_TYPE,
                        categoryName);
                break;
            }
            case ExpenseType: {
                category = new Record.Category(
                        Record.Category.EXPENSE_TYPE,
                        categoryName);
                break;
            }
        }
        assert category != null;
        getDatabase().save(category);
        categories.add(category);
        addCategoryBtn.setText("Add ...");
        textLayout.setVisibility(View.INVISIBLE);
        addCategoryBtn.setOnClickListener(shownewItemEditText);
        switch (type) {
            case IncomeType :
                listCategories(getCategories(0));
                break;
            case ExpenseType:
                listCategories(getCategories(1));
                break;
        }
    }


    @Override
    public void finish() {
        Intent intent = new Intent();
        if (category != null) {
            intent.putExtra("category",
                    category.getId());
            setResult(RESULT_OK,
                    intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }

    public interface OnCategoryClickedListener {
        void onClicked(Record.Category category);

        void onUpdate(Record.Category category);
    }
}
