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

package com.octopus.wallet.m.p.db;

import android.content.Context;

import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.pb.Budget;
import com.octopus.wallet.m.pb.Currency;
import com.octopus.wallet.m.pb.Debt;
import com.octopus.wallet.m.pb.Location;
import com.octopus.wallet.m.pb.Migration;
import com.octopus.wallet.m.pb.Notification;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.pb.RecurringPayment;
import com.octopus.wallet.m.pb.ShoppingList;

import java.util.ArrayList;
import java.util.List;

import me.yoctopus.fastdb.Column;
import me.yoctopus.fastdb.FastDB;
import me.yoctopus.fastdb.Model;
import me.yoctopus.fastdb.OnCorrupt;

public final class AppDatabase extends FastDB {
    public static final int version = 1;
    private static final String DB_NAME = "db";
    private static Records records;
    private static Categories categories;
    private static ShoppingLists shoppingLists;
    private static ShoppingItems shoppingItems;
    private static Notifs notifs;
    private static Migrations migrations;
    private static RecurringPayments recurringPayments;
    private static Budgets budgets;
    private static Debts debts;
    private static Currencies currencies;
    private static Locations locations;

    static {
        records = new Records();
        categories = new Categories();
        shoppingLists = new ShoppingLists();
        shoppingItems = new ShoppingItems();
        notifs = new Notifs();
        migrations = new Migrations();
        recurringPayments = new RecurringPayments();
        budgets = new Budgets();
        debts = new Debts();
        currencies = new Currencies();
        locations = new Locations();
    }

    public AppDatabase(Context context) {
        super(context, DB_NAME, version, new OnCorrupt() {
            @Override
            public void onCorrupt() {

            }
        });
    }

    @Override
    public boolean shouldBackup(int oldVersion,
                                int newVersion) {
        return newVersion > oldVersion;
    }

    @Override
    public List<Model> onGetModels() {
        List<Model> models = new ArrayList<>();
        models.add(records);
        models.add(categories);
        models.add(shoppingLists);
        models.add(shoppingItems);
        models.add(notifs);
        models.add(migrations);
        models.add(recurringPayments);
        models.add(budgets);
        models.add(debts);
        models.add(currencies);
        models.add(locations);
        return models;
    }

    public boolean save(Record record) {
        return save(record, records);
    }

    public boolean save(Record.Category category) {
        return save(category, categories);
    }

    public boolean save(ShoppingList list) {
        boolean one = save(list, shoppingLists);
        List<ShoppingList.ListItem> items = list.getListItems();
        int id = getLastId(shoppingLists);
        for (int i = 0;
             i < items.size();
             i++) {
            items.get(i).setNumber(id);
        }
        boolean many = save(items, shoppingItems);
        return one && many;
    }

    public boolean save(Notification notification) {
        return save(notification, notifs);
    }

    public boolean save(Migration migration) {
        return save(migration, migrations);
    }

    public boolean save(RecurringPayment payment) {
        return save(payment, recurringPayments);
    }

    public boolean save(Budget budget) {
        return save(budget, budgets);
    }

    public boolean save(Debt debt) {
        return save(debt, debts);
    }

    public boolean save(Currency currency) {
        return save(currency, currencies);
    }

    public boolean save(Location location) {
        return save(location, locations);
    }

    public boolean update(RecurringPayment payment) {
        Column<Integer> column = RecurringPayments.id;
        column.set(payment.getId());
        return update(payment, recurringPayments, column);
    }

    public boolean update(Budget budget) {
        Column<Integer> column = Budgets.id;
        column.set(budget.getId());
        return update(budget, budgets, column);
    }

    public boolean update(Debt debt) {
        Column<Integer> column = Debts.id;
        column.set(debt.getId());
        return update(debt, debts, column);
    }

    public boolean update(Record record) {
        Column<Integer> column = Records.id;
        column.set(record.getId());
        return update(record, records, column);
    }

    public boolean update(Record.Category category) {
        Column<Integer> column = Categories.id;
        column.set(category.getId());
        return update(category, categories, column);
    }


    public boolean update(ShoppingList list) {
        return delete(list) && save(list);
    }

    public boolean update(Migration migration) {
        Column<Integer> column = Migrations.id;
        column.set(migration.getId());
        return update(migration, migrations, column);
    }

    public boolean update(Currency currency) {
        Column<Integer> column = Currencies.id;
        column.set(currency.getId());
        return update(currency, currencies, column);
    }

    public boolean update(Location location) {
        Column<Integer> column = Locations.id;
        column.set(location.getId());
        return update(location, locations, column);
    }


    public boolean delete(Record record) {
        Column<Integer> column = Records.id;
        column.set(record.getId());
        return delete(records, column);
    }

    public boolean delete(Record.Category category) {
        Column<Integer> column = Categories.id;
        column.set(category.getId());
        return delete(categories, column);
    }

    public boolean delete(ShoppingList list) {
        Column<Integer> column = ShoppingLists.id;
        column.set(list.getId());
        Column<Integer> column1 = ShoppingItems.number;
        column1.set(list.getId());
        return delete(shoppingLists, column) &&
                delete(shoppingItems, column1);
    }

    public boolean delete(RecurringPayment payment) {
        Column<Integer> column = RecurringPayments.id;
        column.set(payment.getId());
        return delete(recurringPayments, column);
    }

    public boolean delete(Budget budget) {
        Column<Integer> column = Budgets.id;
        column.set(budget.getId());
        return delete(budgets, column);
    }

    public boolean delete(Debt debt) {
        Column<Integer> column = Debts.id;
        column.set(debt.getId());
        return delete(debts, column);
    }

    public boolean delete(Currency currency) {
        Column<Integer> column = Currencies.id;
        column.set(currency.getId());
        return delete(currencies, column);
    }

    public boolean delete(Notification notification) {
        Column<Integer> column = Notifs.id;
        column.set(notification.getId());
        return delete(notifs, column);
    }

    public boolean delete(Migration migration) {
        Column<Integer> column = Migrations.id;
        column.set(migration.getId());
        return delete(migrations, column);
    }

    public boolean delete(Location location) {
        Column<Integer> column = Locations.id;
        column.set(location.getId());
        return delete(locations, column);
    }

    private boolean saveCategories(
            List<Record.Category> list) {
        return save(list, categories);
    }

    public boolean deleteAllNotifications() {
        return delete(notifs);
    }

    public boolean deleteAllMigrations() {
        return delete(migrations);
    }

    public boolean deleteAllRecurringPayments() {
        return delete(recurringPayments);
    }

    public boolean deleteAllBudgets() {
        return delete(budgets);
    }

    public boolean deleteAllRecords() {
        return delete(records);
    }

    public boolean deleteAllDebts() {
        return delete(debts);
    }

    public boolean deleteAllCurrencies() {
        return delete(currencies);
    }

    public boolean deleteAllLocations() {
        return delete(locations);
    }

    public SList<Currency> getCurrencies() {
        List<Currency> list = loadList(currencies);
        if (list == null || list.isEmpty()) {
            save(getSomeCurrencies(), currencies);
            return new SList<>(loadList(currencies));
        }
        return new SList<>(list);
    }

    public SList<Record> getRecords() {
        return new SList<>(loadList(records));
    }

    public SList<Record.Category> getCategories() {
        List<Record.Category> list = loadList(categories);
        if (list.size() == 0) {
            saveCategories(getSomeCategories());
            return new SList<>(loadList(categories));
        }
        return new SList<>(list);
    }

    public SList<RecurringPayment> getRecurringPayments() {
        return new SList<>(loadList(recurringPayments));
    }

    public SList<Budget> getBudgets() {
        return new SList<>(loadList(budgets));
    }

    public SList<Debt> getDebts() {
        return new SList<>(loadList(debts));
    }

    public SList<ShoppingList> getShoppingLists() {
        List<ShoppingList> list = loadList(shoppingLists);
        Column<Integer> column = ShoppingItems.number;
        for (int i = 0; i < list.size(); i++) {
            ShoppingList list2 = list.get(i);
            column.set(list2.getId());
            list.get(i).setListItems(
                    new ArrayList<>(loadList(
                            shoppingItems,
                            column)));
        }
        return new SList<>(list);
    }

    public SList<Location> getLocations() {
        return new SList<>(loadList(locations));
    }

    public SList<Notification> getNotifications() {
        return new SList<>(loadList(notifs));
    }

    public SList<Migration> getMigrations() {
        return new SList<>(loadList(migrations));
    }

    public boolean refreshData() {
        return deleteAllModels();
    }

    public boolean deleteAllCategories() {
        return delete(categories);
    }

    public boolean deleteAllShoppingLists() {
        return delete(shoppingLists) && delete(shoppingItems);
    }

    private SList<Currency> getSomeCurrencies() {
        List<Currency> list1 = new ArrayList<>();
        Currency ksh = Currency.DEFAULT_CURRENCY();
        list1.add(ksh);
        list1.add(getCurrency("USD", "US Dollars", "1"));
        list1.add(getCurrency("EUR", "Euro", "1"));
        return new SList<>(list1);
    }

    private Currency getCurrency(String code,
                                 String country,
                                 String rate) {
        return new Currency(code,
                country,
                Float.valueOf(rate));
    }

    private SList<Record.Category> getSomeCategories() {
        List<Record.Category> list = new ArrayList<>();
        list.add(init(Record.Category.INCOME_TYPE, "Salary"));
        list.add(init(Record.Category.INCOME_TYPE, "Loans"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Car expenses"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Fee"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Groceries and Vegetables"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Hotels"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Phone and Internet"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Transport and Travel"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Entertainment and Sports"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Wardrobe Accessories"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Personal Effects"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Drugs and Alcohol"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Household Utilities"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Shopping"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Rent"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Vacation"));
        list.add(init(Record.Category.EXPENSE_TYPE, "Miscellaneous"));
        return new SList<>(list);
    }

    private Record.Category init(String type, String name) {
        return new Record.Category(type, name);
    }
}
