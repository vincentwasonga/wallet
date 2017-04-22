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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.CurrencyManager;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.m.MainActivity;

import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;

import me.yoctopus.cac.util.LogUtil;

public class SplashActivity extends BActivity {
    private static final int START_DELAY_MILLIS = 1500;
    private final Runnable runnable =
            new Runnable() {
                @SuppressLint("InlinedApi")
                @Override
                public void run() {
                    CurrencyManager manager =
                            new CurrencyManager(SplashActivity.this);
                    Func.init(manager.getCurrency());
                    try {
                        Boolean login =
                                getPreferences().getPreference(
                                        new Pref<>("login", false));
                        Intent intent;
                        if (login) {
                            intent = getIntent(LoginActivity.class);
                            intent.putExtra("pin_action",
                                    LoginActivity.LOGIN);
                        } else {
                            intent = getIntent(MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } catch (InvalidPreferenceType e) {
                        e.printStackTrace();
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Boolean welcome = getPreferences()
                    .getPreference(new Pref<>("slide_launched", false));

            LogUtil.e("Sl", "launched "+welcome);
            if (welcome) {
                executeRunnable(runnable, START_DELAY_MILLIS);
            }
            else {
                startActivity(getIntent(WelcomeActivity.class));
                finish();
            }
        } catch (InvalidPreferenceType invalidPreferenceType) {
            invalidPreferenceType.printStackTrace();
        }
    }
}
