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

package com.octopus.wallet.u.a.m;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ViewSwitcher;

import com.octopus.wallet.R;
import com.octopus.wallet.m.h.DayRecords;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.f.r.Graphs;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecordsActivity extends BActivity
        implements Graphs.OnInteraction {
    @InjectView(R.id.tabs)
    TabLayout tabLayout;
    @InjectView(R.id.content)
    ViewPager viewPager;
    @InjectView(R.id.activity_records)
    ViewSwitcher switcher;
    @InjectView(R.id.tabs_choices)
    TabLayout tabsChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        setTitle("Records Analysis");
        ButterKnife.inject(this);
        setHasBackButton(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        tabsChoices.addTab(tabsChoices.newTab().setText("Days"));
        tabsChoices.addTab(tabsChoices.newTab().setText("Weeks"));
        tabsChoices.addTab(tabsChoices.newTab().setText("Months"));
        tabsChoices.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        prepViewPager(new Filter(Filter.DAY));
                        break;
                    case 1 :
                        prepViewPager(new Filter(Filter.WEEK));
                        break;
                    case 2 :
                        prepViewPager(new Filter(Filter.MONTH));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        prepViewPager(new Filter(Filter.DAY));
                        break;
                    case 1 :
                        prepViewPager(new Filter(Filter.WEEK));
                        break;
                    case 2 :
                        prepViewPager(new Filter(Filter.MONTH));
                        break;
                }
            }
        });
        prepViewPager(new Filter(Filter.DAY));
    }

    private void prepViewPager(final Filter filter) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager =
                        getSupportFragmentManager();
                SectionsAdapter adapter = new SectionsAdapter(
                        fragmentManager,
                        filter);
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        };
        executeRunnable(runnable);
    }

    @Override
    public void onInteraction(Uri uri) {

    }

    public class Filter {
        public static final int DAY = 0;
        public static final int WEEK = 1;
        public static final int MONTH = 2;
        public Filter(int type) {
            this.type = type;
        }

        private int type;

        public int getType() {
            return type;
        }
    }

    private class SectionsAdapter extends FragmentPagerAdapter {
        Sections sections;
        List<String> titles;

        SectionsAdapter(FragmentManager manager,
                        Filter filter) {
            super(manager);
            setFilter(filter);
        }

        @Override
        public Fragment getItem(int position) {
            assert sections != null;
            return sections.getSections().get(position);
        }

        @Override
        public int getCount() {
            assert sections != null;
            return sections.getSections().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            assert titles != null;
            return titles.get(position);
        }

        void setFilter(Filter filter) {
            if (sections != null) {
                sections.clear();
            }
            sections = new Sections(filter);
            titles = sections.titles;
        }
    }

    private class Sections {
        private List<Graphs> sections;
        private List<String> titles;

        Sections(Filter filter) {
            sections = new ArrayList<>();
            titles = new ArrayList<>();
            switch (filter.getType()) {
                case Filter.DAY:
                    List<DayRecords> list_day =
                            getDayRecords();
                    for (DayRecords records :list_day) {
                        sections.add(Graphs.newInstance(records));
                        titles.add(records.getName());
                    }
                    break;
                case Filter.WEEK:
                    /*
                    List<WeekRecords> list_week =
                            getAccount().getAll(Account.WEEK_RECORDS);
                    for (WeekRecords records :list_week) {
                        sections.add(Graphs.newInstance(records));
                        titles.add(records.getName());
                    }*/
                    break;
                case Filter.MONTH:
                    /*
                    List<MonthRecords> list_month =
                            getAccount().getAll(Account.MONTH_RECORDS);
                    for (MonthRecords records :list_month) {
                        sections.add(Graphs.newInstance(records));
                        titles.add(records.getName());
                    }*/
                    break;
            }
        }

        void clear() {
            sections.clear();
            titles.clear();
            sections = null;
        }

        List<Graphs> getSections() {
            return sections;
        }
    }
}
