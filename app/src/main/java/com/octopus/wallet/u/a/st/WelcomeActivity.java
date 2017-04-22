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

package com.octopus.wallet.u.a.st;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Api;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import com.octopus.wallet.s.BService;

import me.yoctopus.cac.pref.InvalidPreference;
import me.yoctopus.cac.pref.Pref;
import me.yoctopus.cac.pref.Preferences;

import me.yoctopus.cac.util.LogUtil;

public class WelcomeActivity extends IntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hasMashMellow = Api.hasMashMellow();
        addSlide(new SimpleSlide.Builder()
                .title("Income/Expense Manager")
                .description("Manage your budgets in a modernized way")
                .image(R.drawable.budget)
                .background(R.color.colorPrimary)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Smart Budget Analysis")
                .description("Analyse your budgets in a smart way, with " +
                        "interactive charts add graphs")
                .image(R.drawable.graph_splash)
                .background(R.color.colorPrimary)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Shopping")
                .description("Monetize your shopping trends by keeping shopping lists" +
                        "for proper budget management")
                .image(R.drawable.shopping_splash)
                .background(R.color.colorPrimary)
                .build());
        String receipts_description = hasMashMellow ? "" +
                "Scan your receipts with the inbuilt camera, " +
                "\n\nKindly grant permissions before proceeding to next step" : "" +
                "Scan your receipts with the inbuilt camera";
        addSlide(new SimpleSlide.Builder()
                .title("Smart Receipts")
                .description(receipts_description)
                .image(R.drawable.receipt_splash)
                .background(R.color.colorPrimary)
                .permissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE})
                .build());
        String welcome_description = hasMashMellow ? "" +
                "You are update to go, please remember to read the " +
                "End User Licence Agreement (EULA) in the " +
                "about section,\n\n Kindly grant required permissions before" +
                "proceeding to the next step ":
                "You are update to go, please remember to read the " +
                       "End User Licence Agreement (EULA) in the " +
                        "about section";
        addSlide(new SimpleSlide.Builder()
                .title("Welcome")
                .description(welcome_description)
                .image(R.drawable.tick_circle)
                .background(R.color.colorPrimary)
                .permissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE})
                .build());
        setFullscreen(true);
        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int i) {
                return true;
            }

            @Override
            public boolean canGoBackward(int i) {
                return true;
            }
        });
    }


    @Override
    public void finish() {
        Preferences preferences = new Preferences(this.getApplicationContext());
        Pref<Boolean> launched = new Pref<>("slide_launched", true);
        try {
            preferences.savePreference(launched);
            Boolean welcome = preferences
                    .getPreference(new Pref<>("slide_launched", false));
            LogUtil.e("Wl", "saved launched "+welcome);
            Intent intent = new Intent(this, SplashActivity.class);
            BService.setAlarms(this);
            startActivity(intent);
            super.finish();
        } catch (InvalidPreference | InvalidPreferenceType invalidPreference) {
            invalidPreference.printStackTrace();
        }
    }
}
