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

package com.octopus.wallet.m.a;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Currency;

/**
 * Created by yoctopus on 3/23/17.
 */

public class CurrencyAdapter extends VBinder<Currency> {
    private ImageView icon;
    private TextView name;
    private TextView rate;
    private ImageView drag;
    private Listener listener;
    public CurrencyAdapter(SList<Currency> list,
                           Listener listener) {
        super(list, R.layout.currency_item);
        this.listener = listener;
    }

    @Override
    public void onInit(View parent) {
        icon = (ImageView) parent.findViewById(R.id.currency_icon);
        name = (TextView) parent.findViewById(R.id.currency_name);
        rate = (TextView) parent.findViewById(R.id.currency_rate);
        drag = (ImageView) parent.findViewById(R.id.drag_image_view);
    }

    @Override
    public void onBind(final Currency currency) {
        String text = currency.toString();
        name.setText(text);
        icon.setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(String.valueOf(text.charAt(0)),
                        ColorGenerator.MATERIAL.getRandomColor()));
        rate.setText(String.valueOf(currency.getRate()));
        drag.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClicked(currency);
                }
                drag.setImageTintList(ColorStateList.valueOf(
                        getParent().getContext().getResources()
                        .getColor(R.color.colorAccent)
                ));
            }
        });
        if (isSameSet(currency)) {
            drag.setImageDrawable(getParent().getContext().getResources()
            .getDrawable(R.drawable.ic_done));
        }
    }
    public interface Listener {
        void onClicked(Currency currency);
    }
    public boolean isSameSet(Currency currency) {
        Currency current = Func.currentCurrency;
        return current.getCode().equals(currency.getCode()) &&
                current.getCountry().equals(currency.getCountry());
    }
}
