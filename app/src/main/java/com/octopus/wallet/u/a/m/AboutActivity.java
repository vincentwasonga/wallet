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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.u.a.BActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yoctopus.cac.util.LogUtil;

public class AboutActivity extends BActivity {
    @InjectView(R.id.userAgent)
    TextView userAgent;
    @InjectView(R.id.termsAndConditionsButton)
    Button termsAndConditionsButton;
    @InjectView(R.id.creditsBtn)
    Button creditsBtn;
    private String TAG =
            LogUtil.makeTag(AboutActivity.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LogUtil.i(TAG,
                "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        userAgent.setText(
                Func.getUserAgent(this));
        final Intent intent =
                getIntent(EulaActivity.class);
        termsAndConditionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("html",
                                0);
                        startActivity(intent);
                    }
                });
        creditsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("html",
                        1);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
