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

package com.octopus.wallet.u.a.t;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.octopus.wallet.BuildConfig;
import com.octopus.wallet.R;
import com.octopus.wallet.m.h.rcpt.CustomOpenCVLoader;
import com.octopus.wallet.m.h.rcpt.PreviewFrame;
import com.octopus.wallet.m.h.rcpt.ReceiptMessage;
import com.octopus.wallet.m.h.rcpt.ScannedReceipt;
import com.octopus.wallet.m.h.utl.Api;
import com.octopus.wallet.m.h.utl.BitmapUtil;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.p.fl.Dir;
import com.octopus.wallet.m.p.fl.Ext;
import com.octopus.wallet.m.p.fl.FUri;
import me.yoctopus.cac.pref.InvalidPreference;
import me.yoctopus.cac.pref.InvalidPreferenceType;
import me.yoctopus.cac.pref.Pref;
import me.yoctopus.cac.pref.Preferences;
import com.octopus.wallet.m.v.w.CanvasView;
import com.octopus.wallet.u.a.BActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.yoctopus.cac.anim.Anim;
import me.yoctopus.cac.anim.Animator;
import me.yoctopus.cac.notif.Duration;
import me.yoctopus.cac.util.LogUtil;

;

public class ReceiptActivity extends BActivity
        implements SurfaceHolder.Callback,
        Camera.PictureCallback,
        Camera.PreviewCallback {
    private static final int CREATE_PERMISSIONS_REQUEST_CAMERA =
            1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE =
            3;
    private static final int RESUME_PERMISSIONS_REQUEST_CAMERA =
            11;
    private static final String TAG =
            LogUtil.makeTag(ReceiptActivity.class);
    private boolean safeToTakePicture;
    private String path;
    private ProgressBar progressBar;
    private ImageView scanPictureButton;
    private HandlerThread handlerThread;
    private BitmapUtil.ReceiptProcessor receiptProcessor;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private boolean focused;
    private CanvasView canvasView;
    private FABToolbarLayout fabToolbarLayout;
    private SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy:" +
                    "MM:" +
                    "dd " +
                    "HH:" +
                    "mm:" +
                    "ss",
                    Locale.ENGLISH);
    private boolean imageProcessorBusy =
            true;
    private SurfaceView surfaceView;
    private BaseLoaderCallback baseLoaderCallback =
            new BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS: {
                            checkResumePermissions();
                            break;
                        }
                        default: {
                            LogUtil.d(TAG,
                                    "status: " +
                                            status);
                            super.onManagerConnected(
                                    status);
                        }
                    }
                }
            };
    private boolean scanClicked =
            false;
    private boolean colorMode =
            false;
    private boolean filterMode =
            colorMode;
    private boolean autoMode =
            false;
    private boolean flashMode =
            false;
    private int YELLOW =
            0xFFFFEB3B;
    private int WHITE =
            0xFFFFFFFF;

    private ResetShutterColor resetShutterColor =
            new ResetShutterColor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        canvasView = (CanvasView) getView(
                                R.id.hud);
                        Display display =
                                getWindowManager()
                                        .getDefaultDisplay();
                        android.graphics.Point size =
                                new android.graphics.Point();
                        display.getRealSize(
                                size);
                        scanPictureButton = (ImageView)
                                getView(
                                        R.id.scanDocButton);
                        scanPictureButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (scanClicked) {
                                            requestPicture();
                                            scanPictureButton
                                                    .setBackgroundTintList(
                                                            null);
                                            seeProgress();
                                        } else {
                                            scanClicked =
                                                    true;
                                            notifyToast(" Tap again " +
                                                            "to scan image",
                                                    Duration.LONG);
                                            ((ImageView) v)
                                                    .setColorFilter(
                                                            YELLOW);
                                        }
                                    }
                                });

                        final ImageView colorModeButton =
                                (ImageView) getView(
                                        R.id.colorModeButton);

                        colorModeButton.setColorFilter(
                                colorMode ?
                                        YELLOW :
                                        WHITE);
                        colorModeButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        colorMode = !colorMode;
                                        ((ImageView) v)
                                                .setColorFilter(
                                                        colorMode ?
                                                                YELLOW :
                                                                WHITE
                                                );
                                        sendImageProcessorMessage("colorMode",
                                                colorMode);
                                        notifyToast(colorMode ?
                                                        "Color mode" :
                                                        "B&W mode");
                                        Pref<Boolean> preference =
                                                new Pref<>("color_mode", colorMode);
                                        try {
                                            getPreferences()
                                                    .savePreference(preference);
                                        } catch (InvalidPreference invalidPreference) {
                                            invalidPreference.printStackTrace();
                                        }
                                    }
                                });
                        final ImageView filterModeButton =
                                (ImageView) getView(
                                        R.id.filterModeButton);

                        filterModeButton.setColorFilter(
                                filterMode ?
                                        YELLOW :
                                        WHITE);
                        filterModeButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        filterMode = !filterMode;
                                        ((ImageView) v)
                                                .setColorFilter(filterMode ?
                                                        YELLOW :
                                                        WHITE);
                                        sendImageProcessorMessage(
                                                "filterMode",
                                                filterMode);
                                        notifyToast(filterMode ?
                                                        "Filter on" :
                                                        "Filter off");
                                        Pref<Boolean> preference =
                                                new Pref<>(
                                                        "filter_mode",
                                                        filterMode);
                                        try {
                                            getPreferences()
                                                    .savePreference(preference);
                                        } catch (InvalidPreference invalidPreference) {
                                            invalidPreference.printStackTrace();
                                        }
                                    }
                                });
                        final ImageView flashModeButton =
                                (ImageView) getView(
                                        R.id.flashModeButton);

                        flashModeButton.setColorFilter(
                                flashMode ?
                                        YELLOW :
                                        WHITE);
                        flashModeButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        flashMode = setFlash(
                                                !flashMode);
                                        ((ImageView) v)
                                                .setColorFilter(
                                                        flashMode ?
                                                                YELLOW :
                                                                WHITE);
                                        Pref<Boolean> preference =
                                                new Pref<>(
                                                        "flash_mode",
                                                        flashMode);
                                        try {
                                            getPreferences()
                                                    .savePreference(preference);
                                        } catch (InvalidPreference invalidPreference) {
                                            invalidPreference.printStackTrace();
                                        }
                                    }
                                });
                        final ImageView autoModeButton =
                                (ImageView) getView(
                                        R.id.autoModeButton);


                        autoModeButton.setColorFilter(
                                autoMode ?
                                        YELLOW :
                                        WHITE);
                        autoModeButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        autoMode = !autoMode;
                                        ((ImageView) v)
                                                .setColorFilter(
                                                        autoMode ?
                                                                YELLOW :
                                                                WHITE);
                                        notifyToast(autoMode ?
                                                        "Automatic mode" :
                                                        "Manual mode");
                                        Pref<Boolean> preference =
                                                new Pref<>("auto_mode",
                                                        autoMode);
                                        try {
                                            getPreferences()
                                                    .savePreference(preference);
                                        } catch (InvalidPreference invalidPreference) {
                                            invalidPreference.printStackTrace();
                                        }
                                    }
                                });
                        progressBar =
                                (ProgressBar) getView(R.id.receipt_progress);
                        fabToolbarLayout =
                                (FABToolbarLayout) getView(
                                        R.id.fabtoolbar);
                        FloatingActionButton fabToolbarButton =
                                (FloatingActionButton) getView(
                                        R.id.fabtoolbar_fab);
                        fabToolbarButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        fabToolbarLayout.show();
                                    }
                                });
                        getView(R.id.hideToolbarButton).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        fabToolbarLayout.hide();
                                    }
                                });
                        getView(R.id.backButton).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                }
                        );
                        LogUtil.d(TAG,
                                "resuming");
                        for (String build :
                                Build.SUPPORTED_ABIS) {
                            LogUtil.d(TAG,
                                    "myBuild " +
                                            build);
                        }
                        checkCreatePermissions();
                        CustomOpenCVLoader.initAsync(
                                OpenCVLoader.OPENCV_VERSION_3_1_0,
                                ReceiptActivity.this,
                                baseLoaderCallback);
                        if (handlerThread == null) {
                            handlerThread =
                                    new HandlerThread(
                                            "Worker Thread");
                            handlerThread.start();
                        }
                        ReceiptProcessorListener processorListener =
                                new ReceiptProcessorListener() {
                                    @Override
                                    public void waitProgressVisible() {
                                        runOnUiThread(
                                                new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(
                                                        View.VISIBLE);

                                            }
                                        });
                                    }

                                    @Override
                                    public void waitProgressInvisible() {
                                        runOnUiThread(
                                                new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(
                                                        View.GONE);
                                            }
                                        });
                                    }

                                    @Override
                                    public boolean requestPicture() {
                                        if (safeToTakePicture) {
                                            runOnUiThread(
                                                    resetShutterColor);
                                            safeToTakePicture =
                                                    false;
                                            camera.takePicture(
                                                    null,
                                                    null,
                                                    ReceiptActivity.this);
                                            return true;
                                        }
                                        return false;
                                    }

                                    @Override
                                    public void setImageProcessorBusy(
                                            boolean busy) {
                                        ReceiptActivity.this.imageProcessorBusy =
                                                busy;
                                    }

                                    @Override
                                    public void saveDocument(
                                            ScannedReceipt document) {
                                        Mat doc = (document.processed !=
                                                null) ?
                                                document.processed :
                                                document.original;
                                        String fileName;
                                        FUri fUri =
                                                new FUri(
                                                        Dir.Camera);
                                        fUri.setFileName(
                                                "Receipt_" +
                                                        Func.getSimpleDateFormat()
                                                                .format(
                                                                        new Date()));
                                        fUri.setExt(
                                                Ext.JPEG);
                                        fileName = fUri.getUri().toString();
                                        Mat endDoc = new Mat(Double.valueOf(
                                                doc.size().width).intValue(),
                                                Double.valueOf(
                                                        doc.size().height)
                                                        .intValue(),
                                                CvType.CV_8UC4);
                                        Core.flip(doc.t(),
                                                endDoc,
                                                1);
                                        Imgcodecs.imwrite(
                                                fileName,
                                                endDoc);
                                        endDoc.release();
                                        try {
                                            ExifInterface exif =
                                                    new ExifInterface(
                                                            fileName);
                                            if (Api.hasNougat()) {
                                                exif.setAttribute(
                                                        ExifInterface
                                                                .TAG_USER_COMMENT,
                                                        "Generated using" +
                                                                " Personal Budget");
                                            }
                                            String nowFormatted =
                                                    simpleDateFormat.format(
                                                            new Date().getTime());
                                            exif.setAttribute(
                                                    ExifInterface
                                                            .TAG_DATETIME,
                                                    nowFormatted);
                                            if (Api.hasMashMellow()) {
                                                exif.setAttribute(
                                                        ExifInterface
                                                                .TAG_DATETIME_DIGITIZED,
                                                        nowFormatted);
                                            }
                                            if (Api.hasNougat()) {
                                                exif.setAttribute(
                                                        ExifInterface
                                                                .TAG_SOFTWARE,
                                                        "Personal Budget " +
                                                                BuildConfig
                                                                        .VERSION_NAME);
                                            }
                                            exif.saveAttributes();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        animateDocument(
                                                fileName,
                                                document);
                                        LogUtil.d(TAG,
                                                "wrote: " +
                                                        fileName);
                                        BitmapUtil.addImageToGallery(
                                                fileName,
                                                ReceiptActivity.this);
                                        refreshCamera();
                                    }

                                    @Override
                                    public void invalidateHUD() {
                                        runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        canvasView.invalidate();
                                                    }
                                                });
                                    }

                                    @Override
                                    public CanvasView getCanvasView() {
                                        return canvasView;
                                    }

                                    @Override
                                    public Preferences getPreferences() {
                                        return ReceiptActivity.this
                                                .getPreferences();
                                    }
                                };
                        if (receiptProcessor == null) {
                            receiptProcessor =
                                    new BitmapUtil.ReceiptProcessor(
                                            handlerThread.getLooper(),
                                            processorListener);
                        }
                        processorListener.setImageProcessorBusy(false);
                    }
                };
        executeRunnable(runnable);
    }

    public void setImageProcessorBusy(
            boolean imageProcessorBusy) {
        this.imageProcessorBusy =
                imageProcessorBusy;
    }

    public boolean setFlash(boolean stateFlash) {
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            Camera.Parameters par =
                    camera.getParameters();
            par.setFlashMode(stateFlash ?
                    Camera.Parameters.FLASH_MODE_TORCH :
                    Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(par);
            notifyToast(stateFlash ?
                            "Flash on" :
                            "Flash off");
            return stateFlash;
        }
        return false;
    }

    private void checkResumePermissions() {
        Api.isCameraAllowed(
                this,
                new Api.CallBack() {
                    @Override
                    public void onSuccess() {
                        enableCameraView();
                    }

                    @Override
                    public void onFailure() {
                        ActivityCompat.requestPermissions(
                                ReceiptActivity.this,
                                new String[]{
                                        Manifest.permission
                                                .CAMERA},
                                RESUME_PERMISSIONS_REQUEST_CAMERA);
                    }
                });

    }

    private void checkCreatePermissions() {
        Api.isWriteAllowed(
                this,
                new Api.CallBack() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure() {
                        ActivityCompat.requestPermissions(
                                ReceiptActivity.this,
                                new String[]{
                                        Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE);
                    }
                });

    }

    public void turnCameraOn() {
        surfaceView = (SurfaceView) getView(
                R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(
                SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.setVisibility(
                SurfaceView.VISIBLE);
    }

    public void enableCameraView() {
        if (surfaceView == null) {
            turnCameraOn();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CREATE_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled,
                // the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED) {
                    turnCameraOn();
                }
                break;
            }
            case RESUME_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED) {
                    enableCameraView();
                }
                break;
            }

        }
    }

    public void seeProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyProgress(null);
            }
        });
    }

    public List<Camera.Size> getResolutionList() {
        return camera.getParameters()
                .getSupportedPreviewSizes();
    }

    public Camera.Size getMaxPreviewResolution() {
        int maxWidth = 0;
        Camera.Size curRes = null;
        camera.lock();
        for (Camera.Size r : getResolutionList()) {
            if (r.width > maxWidth) {
                LogUtil.d(TAG,
                        "supported preview " +
                                "resolution: " +
                                r.width +
                                "x" +
                                r.height);
                maxWidth = r.width;
                curRes = r;
            }
        }
        return curRes;
    }

    public List<Camera.Size> getPictureResolutionList() {
        return camera.getParameters()
                .getSupportedPictureSizes();
    }

    public Camera.Size getMaxPictureResolution(
            float previewRatio) {
        int maxPixels =
                0;
        int ratioMaxPixels =
                0;
        Camera.Size currentMaxRes =
                null;
        Camera.Size ratioCurrentMaxRes =
                null;
        for (Camera.Size r : getPictureResolutionList()) {
            float pictureRatio = (float) r.width /
                    r.height;
            LogUtil.d(TAG,
                    "supported picture" +
                            " resolution: " +
                            r.width +
                            "x" +
                            r.height +
                            " ratio: " +
                            pictureRatio);
            int resolutionPixels = r.width *
                    r.height;
            if (resolutionPixels > ratioMaxPixels &&
                    pictureRatio == previewRatio) {
                ratioMaxPixels = resolutionPixels;
                ratioCurrentMaxRes = r;
            }
            if (resolutionPixels >
                    maxPixels) {
                maxPixels = resolutionPixels;
                currentMaxRes = r;
            }
        }
        boolean matchAspect;
        try {
            matchAspect = getPreferences().getPreference(
                    new Pref<>("math_aspect", true));
        } catch (InvalidPreferenceType e) {
            e.printStackTrace();
            matchAspect = false;
        }
        if (ratioCurrentMaxRes !=
                null &&
                matchAspect) {
            LogUtil.d(TAG,
                    "Max supported " +
                            "picture resolution" +
                            " with preview aspect ratio: "
                            + ratioCurrentMaxRes.width +
                            "x" +
                            ratioCurrentMaxRes.height);
            return ratioCurrentMaxRes;
        }
        return currentMaxRes;
    }

    private int findBestCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //load the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info =
                    new Camera.CameraInfo();
            Camera.getCameraInfo(i,
                    info);
            if (info.facing == Camera.CameraInfo
                    .CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
            cameraId = i;
        }
        return cameraId;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            int cameraId = findBestCamera();
            camera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();
        Camera.Size pSize =
                getMaxPreviewResolution();
        param.setPreviewSize(pSize.width,
                pSize.height);
        float previewRatio =
                (float) pSize.width /
                        pSize.height;
        Display display =
                getWindowManager().getDefaultDisplay();
        android.graphics.Point size =
                new android.graphics.Point();
        display.getRealSize(size);
        int displayWidth = Math.min(size.y,
                size.x);
        int displayHeight = Math.max(size.y,
                size.x);
        float displayRatio =
                (float) displayHeight / displayWidth;
        int previewHeight = displayHeight;
        if (displayRatio > previewRatio) {
            ViewGroup.LayoutParams surfaceParams =
                    surfaceView.getLayoutParams();
            previewHeight =
                    (int) ((float) size.y /
                            displayRatio *
                            previewRatio);
            surfaceParams.height = previewHeight;
            surfaceView.setLayoutParams(surfaceParams);
            canvasView.getLayoutParams().height =
                    previewHeight;
        }
        int hotAreaWidth = displayWidth /
                4;
        int hotAreaHeight =
                previewHeight / 2 -
                        hotAreaWidth;
        ImageView angleNorthWest =
                (ImageView) getView(
                        R.id.nw_angle);
        RelativeLayout.LayoutParams paramsNW =
                (RelativeLayout.LayoutParams)
                        angleNorthWest.getLayoutParams();
        paramsNW.leftMargin = hotAreaWidth -
                paramsNW.width;
        paramsNW.topMargin = hotAreaHeight -
                paramsNW.height;
        angleNorthWest.setLayoutParams(paramsNW);
        ImageView angleNorthEast = (ImageView) getView(
                R.id.ne_angle);
        RelativeLayout.LayoutParams paramsNE =
                (RelativeLayout.LayoutParams)
                        angleNorthEast.getLayoutParams();
        paramsNE.leftMargin = displayWidth -
                hotAreaWidth;
        paramsNE.topMargin = hotAreaHeight -
                paramsNE.height;
        angleNorthEast.setLayoutParams(paramsNE);
        ImageView angleSouthEast = (ImageView) getView(
                R.id.se_angle);
        RelativeLayout.LayoutParams paramsSE =
                (RelativeLayout.LayoutParams)
                        angleSouthEast.getLayoutParams();
        paramsSE.leftMargin = displayWidth -
                hotAreaWidth;
        paramsSE.topMargin = previewHeight -
                hotAreaHeight;
        angleSouthEast.setLayoutParams(paramsSE);
        ImageView angleSouthWest = (ImageView) getView(
                R.id.sw_angle);
        RelativeLayout.LayoutParams paramsSW =
                (RelativeLayout.LayoutParams)
                        angleSouthWest.getLayoutParams();
        paramsSW.leftMargin = hotAreaWidth -
                paramsSW.width;
        paramsSW.topMargin = previewHeight -
                hotAreaHeight;
        angleSouthWest.setLayoutParams(paramsSW);
        Camera.Size maxRes =
                getMaxPictureResolution(previewRatio);
        if (maxRes != null) {
            param.setPictureSize(maxRes.width,
                    maxRes.height);
            LogUtil.d(TAG,
                    "max supported picture resolution: " +
                            maxRes.width +
                            "x" +
                            maxRes.height);
        }
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(
                PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            param.setFocusMode(
                    Camera.Parameters
                            .FOCUS_MODE_CONTINUOUS_VIDEO);
            LogUtil.d(TAG,
                    "enabling auto-focus");
        } else {
            focused = true;
            LogUtil.d(TAG,
                    "auto-focus not available");
        }
        if (pm.hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            param.setFlashMode(flashMode ?
                    Camera.Parameters.FLASH_MODE_TORCH :
                    Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(param);
        rotateCamera();
        try {
            camera.setAutoFocusMoveCallback(
                    new Camera.AutoFocusMoveCallback() {
                        @Override
                        public void onAutoFocusMoving(boolean start,
                                                      Camera camera) {
                            focused = !start;
                            LogUtil.d(TAG,
                                    "focusMoving: " +
                                            focused);
                        }
                    });
        } catch (Exception e) {
            LogUtil.d(TAG,
                    "failed setting" +
                            " AutoFocusMoveCallback");
        }

        // some devices doesn't call the
        // AutoFocusMoveCallback - fake the
        // focus to true at the start
        focused = true;
        safeToTakePicture = true;
    }

    private void rotateCamera() {

        int NINETY = 90;
        camera.setDisplayOrientation(
                NINETY);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format,
                               int width,
                               int height) {
        refreshCamera();
    }

    private void refreshCamera() {
        try {
            camera.stopPreview();
        } catch (Exception e) {
        }

        try {
            camera.setPreviewDisplay(
                    surfaceHolder);

            camera.startPreview();
            camera.setPreviewCallback(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(
                    null);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data,
                               Camera camera) {
        android.hardware.Camera.Size pictureSize =
                camera.getParameters().getPreviewSize();
        LogUtil.d(TAG,
                "onPreviewFrame - received image " +
                        pictureSize.width +
                        "x" +
                        pictureSize.height
                        + " focused: " +
                        focused +
                        " imageprocessor: " +
                        (imageProcessorBusy ?
                                "busy" :
                                "available"));

        if (focused &&
                !imageProcessorBusy) {
            setImageProcessorBusy(
                    true);
            Mat yuv = new Mat(
                    new Size(
                            pictureSize.width,
                            pictureSize.height * 1.5),
                    CvType.CV_8UC1);
            yuv.put(0,
                    0,
                    data);
            Mat mat = new Mat(
                    new Size(
                            pictureSize.width,
                            pictureSize.height),
                    CvType.CV_8UC4);
            Imgproc.cvtColor(yuv,
                    mat,
                    Imgproc.COLOR_YUV2RGBA_NV21,
                    4);
            yuv.release();
            sendImageProcessorMessage(
                    "previewFrame",
                    new PreviewFrame(
                            mat,
                            autoMode,
                            !(autoMode ||
                                    scanClicked)));
        }
    }

    public boolean requestPicture() {
        if (safeToTakePicture) {
            runOnUiThread(resetShutterColor);
            safeToTakePicture = false;
            camera.takePicture(null,
                    null,
                    this);
            return true;
        }
        return false;
    }

    @Override
    public void onPictureTaken(byte[] data,
                               Camera camera) {
        shootSound();
        android.hardware.Camera.Size pictureSize =
                camera.getParameters().getPictureSize();
        LogUtil.d(TAG,
                "onPictureTaken - received image " +
                        pictureSize.width +
                        "x" +
                        pictureSize.height);
        Mat mat = new Mat(
                new Size(
                        pictureSize.width,
                        pictureSize.height),
                CvType.CV_8U);
        mat.put(0,
                0,
                data);
        setImageProcessorBusy(
                true);
        sendImageProcessorMessage(
                "pictureTaken",
                mat);
        scanClicked = false;
        safeToTakePicture = true;
    }

    public void sendImageProcessorMessage(String messageText,
                                          Object obj) {
        LogUtil.d(TAG,
                "sending message to ReceiptProcessor: " +
                        messageText +
                        " - " +
                        obj.toString());
        Message msg = receiptProcessor.obtainMessage();
        msg.obj = new ReceiptMessage(messageText,
                obj);
        receiptProcessor.sendMessage(msg);
    }

    private void animateDocument(String filename,
                                 ScannedReceipt quadrilateral) {
        AnimationRunnable runnable =
                new AnimationRunnable(filename,
                        quadrilateral);
        runOnUiThread(runnable);
    }

    private void shootSound() {
        notifySound("file:///" +
                "system/media/audio/ui/" +
                "camera_click.ogg");
    }

    private class ResetShutterColor implements Runnable {
        @Override
        public void run() {
            scanPictureButton.setBackgroundTintList(null);
        }
    }

    @Override
    public void finish() {
        if (path != null &&
                !TextUtils.isEmpty(path)) {
            Intent intent = new Intent();
            intent.putExtra("receiptName",
                    path);
            setResult(RESULT_OK,
                    intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }

    class AnimationRunnable implements Runnable {
        Size previewSize = null;
        public String fileName = null;
        public int width;
        public int height;
        private Size imageSize;
        private Point[] previewPoints = null;
        private Bitmap bitmap;

        AnimationRunnable(String filename,
                          ScannedReceipt document) {
            this.fileName = filename;
            path = filename;
            this.imageSize =
                    document.processed.size();

            if (document.quadrilateral != null) {
                this.previewPoints =
                        document.previewPoints;
                this.previewSize =
                        document.previewSize;
            }
        }

        double hypotenuse(Point a,
                          Point b) {
            return Math.sqrt(
                    Math.pow(
                            a.x -
                                    b.x,
                            2) +
                            Math.pow(
                                    a.y -
                                            b.y,
                                    2));
        }

        @Override
        public void run() {
            final ImageView imageView =
                    (ImageView) getView(
                            R.id.scannedAnimation);
            Display display = getWindowManager().getDefaultDisplay();
            android.graphics.Point size =
                    new android.graphics.Point();
            display.getRealSize(
                    size);
            int width = Math.min(
                    size.x,
                    size.y);
            int height = Math.max(
                    size.x,
                    size.y);
            // ATENTION: captured images are always in landscape,
            // values should be swapped
            double imageWidth = imageSize.height;
            double imageHeight = imageSize.width;
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            if (previewPoints != null) {
                double documentLeftHeight =
                        hypotenuse(
                                previewPoints[0],
                                previewPoints[1]);
                double documentBottomWidth =
                        hypotenuse(
                                previewPoints[1],
                                previewPoints[2]);
                double documentRightHeight =
                        hypotenuse(
                                previewPoints[2],
                                previewPoints[3]);
                double documentTopWidth =
                        hypotenuse(
                                previewPoints[3],
                                previewPoints[0]);
                double documentWidth = Math.max(
                        documentTopWidth,
                        documentBottomWidth);
                double documentHeight = Math.max(
                        documentLeftHeight,
                        documentRightHeight);
                LogUtil.d(TAG,
                        "device: " +
                                width +
                                "x" +
                                height +
                                " image: " +
                                imageWidth +
                                "x" +
                                imageHeight +
                                " document: "
                                + documentWidth +
                                "x" +
                                documentHeight);
                LogUtil.d(TAG,
                        "previewPoints[0] x=" +
                                previewPoints[0].x +
                                " y=" +
                                previewPoints[0].y);
                LogUtil.d(TAG,
                        "previewPoints[1] x=" +
                                previewPoints[1].x +
                                " y=" +
                                previewPoints[1].y);
                LogUtil.d(TAG,
                        "previewPoints[2] x=" +
                                previewPoints[2].x +
                                " y=" +
                                previewPoints[2].y);
                LogUtil.d(TAG,
                        "previewPoints[3] x=" +
                                previewPoints[3].x +
                                " y=" +
                                previewPoints[3].y);
                // ATENTION: again, swap width add height
                double xRatio = width /
                        previewSize.height;
                double yRatio = height /
                        previewSize.width;
                params.topMargin = (int) (previewPoints[3].x *
                        yRatio);
                params.leftMargin =
                        (int) ((previewSize.height -
                                previewPoints[3].y) *
                                xRatio);
                params.width = (int) (documentWidth *
                        xRatio);
                params.height = (int) (documentHeight *
                        yRatio);
            } else {
                params.topMargin = height /
                        4;
                params.leftMargin = width /
                        4;
                params.width = width /
                        2;
                params.height = height /
                        2;
            }
            bitmap = BitmapUtil.decodeSampledBitmapFromUri(
                    fileName,
                    params.width,
                    params.height);
            imageView.setVisibility(
                    View.VISIBLE);
            imageView.setImageBitmap(bitmap);
            final int leftMargin =
                    -params.leftMargin;
            final int myHeight =
                    height;
            final int topMargin =
                    params.topMargin;
            new Animator()
                    .addAnim(Anim.load(
                            new Anim.CustomAnimation() {
                        @Override
                        public void doCustom() {
                            TranslateAnimation translateAnimation =
                                    new TranslateAnimation(
                                            Animation.ABSOLUTE,
                                            0,
                                            Animation.ABSOLUTE,
                                            leftMargin,
                                            Animation.ABSOLUTE,
                                            0,
                                            Animation.ABSOLUTE,
                                            myHeight -
                                                    topMargin);
                            ScaleAnimation scaleAnimation =
                                    new ScaleAnimation(1,
                                            0,
                                            1,
                                            0);
                            AnimationSet animationSet =
                                    new AnimationSet(true);
                            animationSet.addAnimation(
                                    scaleAnimation);
                            animationSet.addAnimation(
                                    translateAnimation);
                            animationSet.setDuration(
                                    600);
                            animationSet.setInterpolator(
                                    new AccelerateInterpolator());
                            animationSet.setAnimationListener(
                                    new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart
                                                (Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(
                                                Animation animation) {
                                            imageView.setVisibility(
                                                    View.INVISIBLE);
                                            imageView.setImageBitmap(
                                                    null);
                                            AnimationRunnable.this
                                                    .bitmap.recycle();
                                            finish();
                                        }

                                        @Override
                                        public void onAnimationRepeat(
                                                Animation animation) {

                                        }
                                    });
                            imageView.startAnimation(animationSet);
                        }
                    }))
                    .animate();
        }
    }


    public interface ReceiptProcessorListener {
        void waitProgressVisible();

        void waitProgressInvisible();

        boolean requestPicture();

        void setImageProcessorBusy(boolean busy);

        void saveDocument(ScannedReceipt document);

        void invalidateHUD();

        CanvasView getCanvasView();

        Preferences getPreferences();
    }
}
