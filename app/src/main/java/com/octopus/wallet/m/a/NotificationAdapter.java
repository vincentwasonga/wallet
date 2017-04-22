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

import android.view.View;
import android.widget.TextView;

import com.octopus.wallet.R;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.pb.Notification;

/**
 * Created by yoctopus on 3/1/17.
 */

public class NotificationAdapter extends VBinder<Notification> {
    private TextView title;
    private TextView message;
    private TextView date;
    private OnInteraction onInteraction;

    public NotificationAdapter(SList<Notification> notifications,
                               OnInteraction onInteraction) {
        super(notifications.reverse(), R.layout.notification_item);
        this.onInteraction = onInteraction;
    }

    @Override
    public void onInit(View parent) {
        title = (TextView) parent.findViewById(R.id.title_textview);
        message = (TextView) parent.findViewById(R.id.message_textview);
        date = (TextView) parent.findViewById(R.id.date_textview);
    }

    @Override
    public void onBind(final Notification notification) {
        title.setText(notification.getTitle());
        message.setText(notification.getDescription());
        date.setText(
                Func.getDateMPassed(
                        notification.getDate()));
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInteraction.onDelete(notification);
            }
        });
    }

    public interface OnInteraction {
        void onDelete(Notification notification);
    }
}
