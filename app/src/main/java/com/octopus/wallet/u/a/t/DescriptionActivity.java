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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.octopus.wallet.R;
import com.octopus.wallet.m.h.utl.Func;
import com.octopus.wallet.m.p.fl.Dir;
import com.octopus.wallet.m.p.fl.Ext;
import com.octopus.wallet.m.p.fl.FManager;
import com.octopus.wallet.m.p.fl.FUri;
import com.octopus.wallet.m.p.nt.models.LocationData;
import com.octopus.wallet.m.pb.Record;
import com.octopus.wallet.m.tx.NTTrans;
import com.octopus.wallet.m.v.w.ZoomImageView;
import com.octopus.wallet.u.a.BActivity;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yoctopus.cac.anim.Anim;
import me.yoctopus.cac.anim.Animator;
import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.tx.Tx;
import me.yoctopus.cac.util.LogUtil;

/**
 * Created by octopus on 8/28/16.
 */
public class DescriptionActivity extends BActivity {
    @InjectView(R.id.titleText)
    TextView titleText;
    @InjectView(R.id.descriptionEdit)
    EditText descriptionEdit;
    @InjectView(R.id.currentLocName)
    EditText currentLocName;
    @InjectView(R.id.receiptImageView)
    ZoomImageView receiptImageView;

    @OnClick({R.id.backButton,
            R.id.okDescriptionBtn,
            R.id.receiptQue,
            R.id.takePictureBtn,
            R.id.pickGalleryBtn,
            R.id.imageButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                finish();
                break;
            case R.id.okDescriptionBtn:
                onDescriptionsDone();
                break;
            case R.id.receiptQue:
                break;
            case R.id.takePictureBtn:
                takePicture();
                break;
            case R.id.pickGalleryBtn:
                openGallery();
                break;
            case R.id.imageButton:
                new Animator()
                        .addAnim(Anim.load(getActivity(),
                                R.anim.locbtn_animator))
                        .addView(view)
                        .animate();
                locateCurrent();
                break;
        }
    }

    private String TAG = LogUtil.makeTag(DescriptionActivity.class);
    private final int CAMERA_REQUEST_CODE = 2;
    private final int GALLERY_REQUEST_CODE = 3;
    private final int GOOGLE_REQUEST_CLIENT_ERROR = 1;
    private Uri uri;
    private String imagePath;
    private Record.DescriptionSet descriptionSet;

    private Runnable openGalleryRunnable =
            new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG,
                            "run: opening gallery");
                    Intent imageIntent = new Intent();
                    imageIntent.setType("image/*");
                    imageIntent.setAction(
                            Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(imageIntent,
                                    "Select Receipt"),
                            GALLERY_REQUEST_CODE);
                }
            };
    private Runnable openCameraRunnable =
            new Runnable() {
                @Override
                public void run() {
                    LogUtil.i(TAG,
                            "run: opening camera");
                    Intent photoIntent = getIntent(
                            ReceiptActivity.class);
                    startActivityForResult(
                            photoIntent,
                            CAMERA_REQUEST_CODE);
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG,
                "onCreate: ");
        setContentView(R.layout.activity_description);
        ButterKnife.inject(this);
        animateActivity();
        setHasBackButton(true);

        titleText.setText("Description");

        Intent intent = getIntent();
        uri = Uri.parse(getDirectory());
        if (intent.hasExtra("description")) {
            descriptionSet = intent.getParcelableExtra(
                    "description");
            descriptionEdit.setText(
                    !descriptionSet.getDescription().equals(
                            Record.DescriptionSet.DEFAULT)
                            ? descriptionSet.getDescription() :
                            null);
            currentLocName.setText(
                    !descriptionSet.getLocationName().equals(
                            Record.DescriptionSet.DEFAULT)
                            ? descriptionSet.getLocationName() :
                            null);
            imagePath = descriptionSet.getReceiptName();
            loadImage(imagePath);
        }
        if (intent.hasExtra("receipt")) {
            imagePath = intent.getStringExtra("receipt");
            loadImage(imagePath);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LogUtil.i(TAG,
                "onPostCreate: ");
        imagePath = "";
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        LogUtil.i(TAG,
                "onActivityResult: ");
        ResultsRunnable resultsRunnable = new ResultsRunnable(
                data,
                requestCode,
                resultCode);
        executeRunnable(resultsRunnable);
    }

    private void loadImage(String path) {
        LogUtil.d(TAG,
                path);
        ImageLoader loader = ImageLoader.getInstance();
        ImageLoaderConfiguration configuration =
                ImageLoaderConfiguration.createDefault(this);
        loader.init(configuration);
        path = "file://" + path;
        LogUtil.d(TAG,
                path);
        loader.displayImage(path,
                receiptImageView,
                new ImageSize(receiptImageView.getWidth(),
                        receiptImageView.getHeight()));
    }

    private String getPath(Uri uri) {
        String[] projection = {
                MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri,
                projection,
                null,
                null,
                null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(
                MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(
                column_index);
        cursor.close();
        String newName = saveReceipt(s);
        LogUtil.i(TAG,
                "getPath: " +
                        newName);
        return newName;
    }

    private void openGallery() {
        LogUtil.i(TAG,
                "openGallery: ");
        getHandler().post(
                openGalleryRunnable);
    }

    private void takePicture() {
        LogUtil.i(TAG,
                "takePicture: ");
        getHandler().post(
                openCameraRunnable);
    }

    private void locateCurrent() {
        LogUtil.i(TAG,
                "locateCurrent: ");
        transact(
                new FindLocation(DescriptionActivity.this,
                        100),
                new Tx.OnComplete<LocationData>() {
                    @Override
                    public void onComplete(int id,
                                           LocationData data) {
                        addLocationName(data);
                    }
                }
        );
    }

    private void onDescriptionsDone() {
        LogUtil.i(TAG,
                "onDescriptionsDone: ");
        finish();
    }

    @Override
    public void finish() {
        LogUtil.i(TAG,
                "finish: ");
        Intent data = new Intent();
        String description = descriptionEdit.getText() != null ?
                descriptionEdit.getText().toString() :
                Record.DescriptionSet.DEFAULT;
        String locationName = currentLocName.getText() != null ?
                currentLocName.getText().toString() :
                Record.DescriptionSet.DEFAULT;
        String receiptName = !imagePath.isEmpty() ?
                imagePath :
                Record.DescriptionSet.DEFAULT;
        descriptionSet = new Record.DescriptionSet(description,
                locationName,
                receiptName);
        data.putExtra("Description",
                descriptionSet);
        setResult(RESULT_OK, data);
        super.finish();
    }

    public String getDirectory() {
        return new File(Dir.Receipts.getName()).getAbsolutePath();
    }

    private String saveReceipt(String sourceName) {
        LogUtil.i(TAG,
                "saveReceipt: ");
        FUri fUri = new FUri(Dir.Receipts);
        fUri.setFileName("Receipt_" +
                Func.getSimpleDateFormat()
                        .format(
                                new Date()));
        fUri.setExt(Ext.PNG);
        File destination = new File(fUri.getUri()
                .toString());
        File source = new File(sourceName);
        FManager manager = new FManager(this);
        if (manager.copy(source,
                destination)) {
            LogUtil.d(TAG,
                    "saveReceipt() returned: copied");
            return manager.getDestination().getAbsolutePath();
        } else {
            LogUtil.i(TAG,
                    "saveReceipt: did not copy");
            return sourceName;
        }
    }

    private void addLocationName(LocationData data) {
        LogUtil.i(TAG,
                "addLocationName: ");
        if (data == null) {
            notifyDialog("Unable to determine location" +
                            "",
                    "Either your network is off or " +
                            "location services are disabled\n " +
                            "Enable network or GPS\n" +
                            "Alternatively," +
                            " you can enter the location name " +
                            "manually");
        } else {
            String locationName = data.getLocationName();
            Location location = data.getLocation();
            if (TextUtils.isEmpty(locationName)) {
                notifyDialog("Unable to determine name of location",
                        "Enable network add try again\n\n" +
                                "Location details" +
                                "\nLat : " + location.getLatitude() +
                                "\nLon : " + location.getLongitude(),
                        new NDialog.DButton(
                                "Enable Network",
                                new NDialog.DButton.BListener() {
                                    @Override
                                    public void onClick(View v) {
                                        enableNetWork();
                                    }
                                }),
                        null);
            } else {
                currentLocName.setText(locationName);
            }
        }
    }

    private void enableNetWork() {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(
                                Settings.ACTION_DATA_ROAMING_SETTINGS);
                        startActivity(intent);
                    }
                };
        getHandler().postDelayed(runnable,
                500);
    }


    private class ResultsRunnable implements Runnable {
        private Intent data;
        private int requestCode;
        private int resultCode;

        ResultsRunnable(Intent data,
                        int requestCode,
                        int resultCode) {
            LogUtil.i(TAG,
                    "ResultsRunnable: ");
            this.data = data;
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }

        @Override
        public void run() {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE: {
                    LogUtil.i(TAG,
                            "run: " +
                                    requestCode);
                    if (data == null) {
                        return;
                    }
                    if (resultCode == RESULT_OK) {
                        if (data.hasExtra("receiptName")) {
                            imagePath = data.getStringExtra(
                                    "receiptName");
                            loadImage(imagePath);
                        }
                    }
                    break;
                }
                case GALLERY_REQUEST_CODE: {
                    LogUtil.i(TAG,
                            "run: " +
                                    requestCode);
                    if (resultCode == RESULT_OK) {
                        uri = data.getData();
                        imagePath = getPath(uri);
                        loadImage(imagePath);
                    }
                    break;
                }
            }
        }
    }

    public class FindLocation extends NTTrans<LocationData> {
        private GoogleApiClient googleApiClient;
        private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
        private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
        private boolean resolving = false, canFindLocation = false;
        public FindLocation(Activity context, int id) {
            super(context, id);
        }


        @Override
        public Progress<LocationData, Integer> getProgress() {
            return null;
        }

        @Override
        public CallBacks<LocationData, Integer> getCallBacks() {
            return new CallBacks<LocationData, Integer>() {
                @Override
                public void onStart() {

                }

                @Override
                public LocationData onExecute() {
                    final LocationData data = new LocationData("location");
                    connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            LogUtil.i(TAG,
                                    "onConnected: ");
                            canFindLocation = true;
                            googleApiClient.connect();
                            try {
                                Location loc =
                                        LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                if (loc != null) {
                                    data.setLocationName(getLocationName(loc));
                                    data.setLocation(loc);
                                    LogUtil.i(TAG,
                                            "onConnected: loc " +
                                                    "Lat: " + loc.getLatitude() +
                                                    "Lon: " + loc.getLongitude() +
                                                    "provider " + loc.getProvider());
                                    LogUtil.i(TAG,
                                            "onConnected: locationName " +
                                                    data.getLocationName());

                                }
                                onComplete(data);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            LogUtil.i(TAG,
                                    "onConnectionSuspended: ");
                        }
                    };
                    connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(
                                @NonNull ConnectionResult connectionResult) {
                            LogUtil.i(TAG,
                                    "onConnectionFailed: ");
                            if (connectionResult.hasResolution()) {
                                resolving = true;
                                try {
                                    connectionResult
                                            .startResolutionForResult(getActivity(),
                                                    GOOGLE_REQUEST_CLIENT_ERROR);

                                } catch (IntentSender.SendIntentException e) {
                                    googleApiClient.connect();
                                }
                            } else {
                                showGoogleAPIErrorDialog(
                                        connectionResult.getErrorCode());
                            }
                        }
                    };
                    LogUtil.i(TAG,
                            "setupGoogleApiClient: ");
                    googleApiClient = new GoogleApiClient.Builder(getContext())
                            .addConnectionCallbacks(connectionCallbacks)
                            .addOnConnectionFailedListener(
                                    connectionFailedListener)
                            .addApi(LocationServices.API)
                            .build();
                    googleApiClient.connect();
                    return null;
                }

                @Override
                public void onProgress(Integer... x) {

                }

                @Override
                public void onEnd(LocationData data) {

                }
            };
        }
    }

    private void showGoogleAPIErrorDialog(int errorCode) {
        LogUtil.i(TAG,
                "showGoogleAPIErrorDialog: " +
                        errorCode);
        GoogleApiAvailability googleApiAvailability =
                GoogleApiAvailability.getInstance();
        Dialog errorDialog = googleApiAvailability
                .getErrorDialog(getActivity(),
                        errorCode,
                        GOOGLE_REQUEST_CLIENT_ERROR);
        errorDialog.show();
    }

    private String getLocationName(Location location) {
        String name = "";
        Geocoder geocoder = new Geocoder(this,
                Locale.getDefault());
        try {
            List<Address> list
                    = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(),
                    1);
            if (list != null && !list.isEmpty()) {
                Address address = list.get(0);
                name = address.getAddressLine(0);
            }
        } catch (IOException | NullPointerException e) {
            LogUtil.e(TAG,
                    "getLocationName: ",
                    e);
        }
        LogUtil.i(TAG,
                "getLocationName: " +
                        name);
        return name;
    }

}
