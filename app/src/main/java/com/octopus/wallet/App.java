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

package com.octopus.wallet;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.octopus.wallet.s.BService;

import me.yoctopus.cac.util.LogUtil;
import wb.android.google.camera.app.GalleryAppImpl;

public class App extends GalleryAppImpl {
    private Context budgetContext;
    private String TAG = LogUtil.makeTag(App.class);
    private BService service;
    private ServiceConnection connection =
            new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder binder) {
            try {
                BService.PBBinder pbBinder =
                        (BService.PBBinder) binder;
                service = pbBinder.getService();
            }
            catch (ClassCastException e) {
                LogUtil.e(TAG,
                        e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG,
                "onCreate: ");
        setBudgetContext(
                getApplicationContext());
        bindService();
    }
    private void bindService() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBudgetContext(),
                        BService.class);
                bindService(intent,
                        connection,
                        BIND_AUTO_CREATE);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onTerminate() {
        LogUtil.i(TAG,
                "onTerminate: ");
        unbindService(connection);
        service = null;
        super.onTerminate();
    }
    public Context getBudgetContext() {
        return budgetContext;
    }

    public void setBudgetContext(Context budgetContext) {
        this.budgetContext = budgetContext;
    }


    public BService getService() {
        return service;
    }

}
