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


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Func;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import com.octopus.wallet.m.v.a.ButtonAnimator;
import com.octopus.wallet.m.v.a.DrawerAnimator;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.mo.BudgetsActivity;
import com.octopus.wallet.u.a.mo.DebtsActivity;
import com.octopus.wallet.u.a.mo.ReportsActivity;
import com.octopus.wallet.u.a.mo.ShoppingListActivity;
import com.octopus.wallet.u.a.s.SettingsActivity;
import com.octopus.wallet.u.a.t.AssistantActivity;
import com.octopus.wallet.u.f.BFragment;
import com.octopus.wallet.u.f.m.Analysis;
import com.octopus.wallet.u.f.m.DashBoard;
import com.octopus.wallet.u.f.m.OverView;
import com.octopus.wallet.u.f.m.Savings;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import im.delight.apprater.AppRater;
import me.yoctopus.cac.anim.Animator;
import me.yoctopus.cac.util.LogUtil;
import wb.android.google.camera.CameraActivity;

public class MainActivity extends BActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DashBoard.OnFragmentInteractionListener,
        Analysis.OnFragmentInteractionListener,
        OverView.OnFragmentInteractionListener,
        Savings.OnFragmentInteractionListener {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabs)
    TabLayout tabLayout;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.nav_view)
    NavigationView navigationView;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.container)
    ViewPager viewPager;
    InterstitialAd interstitialAd;
    private String TAG =
            LogUtil.makeTag(MainActivity.class);
    private SectionsAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(MainActivity.this);
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        setSupportActionBar(toolbar);
                        FragmentManager fragmentManager =
                                getSupportFragmentManager();
                        pagerAdapter = new SectionsAdapter(
                                fragmentManager);
                        viewPager.setAdapter(pagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                        fab.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        actionFabClicked(view);
                                    }
                                });
                        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                                getActivity(),
                                drawerLayout,
                                toolbar,
                                R.string.navigation_drawer_open,
                                R.string.navigation_drawer_close);
                        drawerLayout.setDrawerListener(toggle);
                        toggle.syncState();
                        navigationView.setNavigationItemSelectedListener(
                                MainActivity.this);
                    }
                };
        executeRunnable(runnable);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(
                getResources().getString(R.string.ad_big_id));
        interstitialAd.setAdListener(
                new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        interstitialAd.show();
                    }
                });
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        interstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,
                menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification: {
                startActivity(getIntent(
                        NotificationActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId,
                                Menu menu) {
        Runnable menuRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        new DrawerAnimator(navigationView,
                                drawerLayout)
                                .animate();
                    }
                };
        executeRunnable(menuRunnable);
        return super.onMenuOpened(featureId,
                menu);
    }

    private void actionFabClicked(View view) {
        new ButtonAnimator(view)
                .addAnimatorListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onStartAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onRepeatAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onStopAnimator(
                                    Animator animator) {
                                startActivity(getIntent(
                                        NewItemActivity.class));
                            }
                        })
                .animate();
    }

    @Override
    public void onBackPressed() {
        LogUtil.i(TAG,
                "onBackPressed: ");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int id = item.getItemId();
                        if (id == R.id.nav_camera) {
                            Intent intent = getIntent(CameraActivity.class);
                            startActivity(intent);
                        }
                        if (id == R.id.nav_report) {
                            startActivity(getIntent(
                                    ReportsActivity.class));
                        } else if (id == R.id.nav_shopping_list) {
                            startActivity(getIntent(
                                    ShoppingListActivity.class));
                        } else if (id == R.id.nav_debt_list) {
                            startActivity(getIntent(
                                    DebtsActivity.class));
                        } else if (id == R.id.nav_budget_list) {
                            startActivity(getIntent(
                                    BudgetsActivity.class));
                        } else if (id == R.id.nav_assistant) {
                            startActivity(getIntent(
                                    AssistantActivity.class));
                        } else if (id == R.id.nav_about) {
                            startActivity(getIntent(
                                    AboutActivity.class));
                        } else if (id == R.id.nav_settings) {
                            startActivity(getIntent(
                                    SettingsActivity.class));
                        } else if (id == R.id.nav_faqs) {
                            startFAQs();
                        } else if (id == R.id.nav_records) {
                            startActivity(getIntent(
                                    RecordsActivity.class));
                        } else if (id == R.id.nav_charts) {
                            startActivity(getIntent(
                                    GraphActivity.class));
                        } else if (id == R.id.nav_share) {
                            startShareActivity();
                        }
                        onBackPressed();
                    }
                };
        executeRunnable(runnable);
        return true;
    }

    private void startShareActivity() {
        Intent intent = new Intent(
                Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "https://play.google.com/store/" +
                        "apps/details?id=com.octopus.budget");
        startActivity(intent);
        startActivity(Intent.createChooser(intent,
                "Share with..."));
    }

    private void startFAQs() {
        notifyToast("Sorry, this forum will be available soon");
    }


    @Override
    public void onFragmentInteraction(int uri) {

    }

    @Override
    public void finish() {
        AppRater rater = new AppRater(this);
        rater.setDaysBeforePrompt(2);
        rater.setPhrases("Support",
                "Kindly take a moment to rate our app on play store",
                "Now", "Later", "");
        rater.show();
        rater.demo();

    }

    private class SectionsAdapter extends FragmentPagerAdapter {
        Sections sections;

        SectionsAdapter(FragmentManager manager) {
            super(manager);
            Boolean shuffle;
            try {
                shuffle = getPreferences().getPreference(
                        new Pref<>("shuffle_fragments", false));
                sections = new Sections(shuffle);
            } catch (InvalidPreferenceType e) {
                e.printStackTrace();
            }
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
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
            assert sections != null;
            return sections.getSections()
                    .get(position).getName();
        }
    }

    private class Sections {
        private List<BFragment> sections;

        Sections(boolean shuffle) {
            sections = new ArrayList<>();
            sections.add(DashBoard.newInstance());
            sections.add(Analysis.newInstance());
            sections.add(OverView.newInstance());
            sections.add(Savings.newInstance());
            if (shuffle) {
                Func.shuffle(sections);
            }
        }

        List<BFragment> getSections() {
            return sections;
        }
    }
}
