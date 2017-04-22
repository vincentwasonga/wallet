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

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.octopus.wallet.R;
import com.octopus.wallet.m.a.AdapterDivider;
import com.octopus.wallet.m.a.NotificationAdapter;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.pb.Notification;
import com.octopus.wallet.u.a.BActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NotificationActivity extends BActivity {
    @InjectView(R.id.notification_list)
    RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.inject(this);
        setHasBackButton(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Notifications");
    }
    @OnClick(R.id.clear_button)
    public void onViewClicked() {
        getDatabase().deleteAllNotifications();
        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        listNotifications(getDatabase().getNotifications());
    }

    private void listNotifications(ArrayList<Notification> notifications) {
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new AdapterDivider(this,
                LinearLayout.VERTICAL));
        list.setAdapter(new NotificationAdapter(new SList<>(notifications),
                new NotificationAdapter.OnInteraction() {
                    @Override
                    public void onDelete(Notification notification) {
                        delete(notification);
                    }
                }));
    }

    private void delete(Notification notification) {
        getDatabase().delete(notification);
    }
}
