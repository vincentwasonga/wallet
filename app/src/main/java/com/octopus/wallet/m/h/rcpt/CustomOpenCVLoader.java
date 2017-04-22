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

package com.octopus.wallet.m.h.rcpt;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.view.View;

import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Api;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.notif.Notification;
import me.yoctopus.cac.util.LogUtil;

/**
 * Created by allgood on 22/02/16.
 */
public class CustomOpenCVLoader extends OpenCVLoader {
    private static String TAG = LogUtil.makeTag(CustomOpenCVLoader.class);

    private static Notification notification;

    private static ServiceConnection serviceConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name,
                                               IBinder service) {

                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };

    private static boolean isGooglePlayInstalled(Context context) {
        return Api.isGooglePlayInstalled(context);
    }

    private static boolean isOpenCVInstalled(String Version,
                                             Context context) {
        LogUtil.d(TAG,
                Version);
        Intent intent =
                new Intent("org.opencv" +
                        ".engine.BIND");
        intent.setPackage("org.opencv" +
                ".engine");
        boolean result = context
                .bindService(intent,
                        serviceConnection,
                        Context.BIND_AUTO_CREATE);
        context.unbindService(serviceConnection);
        return result;
    }

    public static boolean initAsync(String version,
                                    final Context context,
                                    LoaderCallbackInterface callbackInterface) {
        String title, message;
        NDialog.DButton positive = null, negative = null;
        if (!isOpenCVInstalled(version,
                context)) {
            notification = new Notification(context);
            notification.dismiss();
            boolean isNonPlayAppAllowed =
                    Api.isNonPlayStoreAppsAllowed(context);
            title = context.getResources().getString(R.string.install_opencv);
            message = context.getResources().getString(R.string.ask_install_opencv);

            if (isGooglePlayInstalled(context)) {
                positive =
                        new NDialog.DButton(
                                context.getResources().getString(
                                        R.string.googleplay),
                                new NDialog.DButton.BListener() {
                                    @Override
                                    public void onClick(View v) {
                                        downloadFromPlayStore(context);
                                    }
                                });
            }

            if (!isNonPlayAppAllowed &&
                    !isGooglePlayInstalled(context)) {
                message = context.getString(
                        R.string.ask_install_opencv) +
                        "\n\n" +
                        context.getString(R.string.messageactivateunknown);
                negative = new NDialog.DButton(
                        context.getString(R.string.activateunknown),
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                enableNonGooglePlayApps(context);
                            }
                        });
            }
            notification.showDialog(title, message, positive, negative, null);
        } else {
            return OpenCVLoader.initAsync(version,
                    context,
                    callbackInterface);
        }
        return false;
    }

    private static void enableNonGooglePlayApps(Context context) {
        context.startActivity(
                new Intent(
                        android.provider
                                .Settings
                                .ACTION_SECURITY_SETTINGS));
    }

    private static void downloadFromPlayStore(Context context) {
        context.startActivity(
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://" +
                                "play.google.com/" +
                                "store/apps/" +
                                "details?id=" +
                                "org.opencv.engine")));
    }


}
