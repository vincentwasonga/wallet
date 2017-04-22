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
import android.webkit.WebView;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.u.a.BActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class EulaActivity extends BActivity {

    @InjectView(R.id.titleText)
    TextView title;
    @InjectView(R.id.fullscreen_content)
    WebView wv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eula);
        ButterKnife.inject(this);
        animateActivity();
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setAllowContentAccess(true);

    }
    @OnClick(R.id.backButton)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Intent intent = getIntent();
        int html = intent.getIntExtra("html",
                0);
        if (html == 0) {
            showTermsAndConditions();
        } else {
            showAbout();
        }

    }

    private void showAbout() {
        title.setText("Credits");
        String url = "file:///android_asset/" +
                "html/credits.html";
        wv.loadUrl(url);
    }

    private void showTermsAndConditions() {
        title.setText("EULA");
        String url = "file:///android_asset/" +
                "html/terms.html";
        wv.loadUrl(url);
    }


}
