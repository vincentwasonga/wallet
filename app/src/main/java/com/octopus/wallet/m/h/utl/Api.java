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

package com.octopus.wallet.m.h.utl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

/**
 * Created by yoctopus on 11/22/16.
 */

public class Api {
    public static boolean isGooglePlayInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            PackageInfo info = pm.getPackageInfo(
                    "com.android.vending",
                    PackageManager.GET_ACTIVITIES);
            String label = (String)
                    info.applicationInfo.loadLabel(pm);
            app_installed = (label != null
                    && label.equals(
                    "Google Play Store"));
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static boolean isNonPlayStoreAppsAllowed(Context context) {
        boolean isNonPlayAppAllowed = false;
        try {
            isNonPlayAppAllowed =
                    Settings.Secure.getInt(
                            context.getContentResolver(),
                            Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isNonPlayAppAllowed;
    }
    public static void isCameraAllowed(Context context,
                                       CallBack callBack) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            callBack.onFailure();

        } else {
            callBack.onSuccess();
        }

    }
    public static void isWriteAllowed(Context context,
                                      CallBack callBack) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            callBack.onFailure();

        }
        else {
            callBack.onSuccess();
        }
    }




    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }



    public static boolean hasMashMellow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }





    public static boolean hasMethod(Class<?> klass,
                                    String methodName,
                                    Class<?>... paramTypes) {
        try {
            klass.getDeclaredMethod(methodName, paramTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }





    public static final boolean HAS_FACE_DETECTION;

    static {
        boolean hasFaceDetection = false;
        try {
            Class<?> listenerClass = Class.forName(
                    "android.hardware.Camera$FaceDetectionListener");
            hasFaceDetection =
                    hasMethod(Camera.class, "setFaceDetectionListener",
                            listenerClass) &&
                            hasMethod(Camera.class, "startFaceDetection") &&
                            hasMethod(Camera.class, "stopFaceDetection") &&
                            hasMethod(Camera.Parameters.class, "getMaxNumDetectedFaces");
        } catch (Throwable t) {
        }
        HAS_FACE_DETECTION = hasFaceDetection;
    }



    public interface CallBack {
        void onSuccess();
        void onFailure();
    }
}
