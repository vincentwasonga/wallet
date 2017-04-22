/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wb.android.google.camera.app;

import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wb.android.google.camera.data.DataManager;
import wb.android.google.camera.data.DownloadCache;
import wb.android.google.camera.data.ImageCacheService;
import wb.android.google.camera.util.GalleryUtils;
import wb.android.google.camera.util.ThreadPool;

public class GalleryAppImpl extends MultiDexApplication implements GalleryApp {

    private static final String DOWNLOAD_FOLDER = "download";
    private static final long DOWNLOAD_CAPACITY = 64 * 1024 * 1024; // 64M

    private ImageCacheService mImageCacheService;
    private Object mLock = new Object();
    private DataManager mDataManager;
    private ThreadPool mThreadPool;
    private DownloadCache mDownloadCache;
    private ArrayList<String> mErrorList;

    @Override
    public void onCreate() {
        super.onCreate();
        wb.android.google.camera.Util.initialize(this);
        initializeAsyncTask();
        GalleryUtils.initialize(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public Context getAndroidContext() {
        return this;
    }

    @Override
    public synchronized DataManager getDataManager() {
        if (mDataManager == null) {
            mDataManager = new DataManager(this);
            mDataManager.initializeSourceMap();
        }
        return mDataManager;
    }
    
    @Override
    public synchronized List<String> getErrorList() {
    	if (mErrorList == null) {
    		mErrorList = new ArrayList<String>();
    	}
    	return mErrorList;
    }
    
    @Override
    public synchronized void uploadError(String error) {
    	getErrorList().add(error);
    }

    @Override
    public ImageCacheService getImageCacheService() {
        // This method may block on file I/O so a dedicated lock is needed here.
        synchronized (mLock) {
            if (mImageCacheService == null) {
                mImageCacheService = new ImageCacheService(getAndroidContext());
            }
            return mImageCacheService;
        }
    }

    @Override
    public synchronized ThreadPool getThreadPool() {
        if (mThreadPool == null) {
            mThreadPool = new ThreadPool();
        }
        return mThreadPool;
    }

    @Override
    public synchronized DownloadCache getDownloadCache() {
        if (mDownloadCache == null) {
            File cacheDir = new File(getExternalCacheDir(), DOWNLOAD_FOLDER);

            if (!cacheDir.isDirectory()) cacheDir.mkdirs();

            if (!cacheDir.isDirectory()) {
                throw new RuntimeException(
                        "fail to create: " + cacheDir.getAbsolutePath());
            }
            mDownloadCache = new DownloadCache(this, cacheDir, DOWNLOAD_CAPACITY);
        }
        return mDownloadCache;
    }

    private void initializeAsyncTask() {
        // AsyncTask class needs to be loaded in UI thread.
        // So we load it here to comply the rule.
        try {
            Class.forName(AsyncTask.class.getName());
        } catch (ClassNotFoundException e) {
        }
    }
}
