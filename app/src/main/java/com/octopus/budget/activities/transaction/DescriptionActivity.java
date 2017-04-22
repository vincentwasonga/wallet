/*
 * ﻿Copyright [2016] [Peter Vincent]
 * Licensed under the Apache License, Version 2.0 (Personal Budget);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.octopus.budget.activities.transaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.octopus.budget.BudgetApp;
import com.octopus.budget.R;
import com.octopus.budget.models.DescriptionSet;
import com.octopus.budget.models.math.HelperFunc;
import com.octopus.budget.models.notification.Notification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by octopus on 8/28/16.
 */
public class DescriptionActivity extends AppCompatActivity {

    private final int REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    private final int GALLERY_REQUEST_CODE = 3;
    private BudgetApp app;
    private Location location;
    private Uri uri;
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener;
    private EditText descriptionEdit;
    private EditText locationEdit;
    private TextView describeIt;
    private ImageButton locateBtn;
    private ImageView receiptImageView;
    private TextView receiptQue;
    private ImageButton takePicBtn;
    private ImageButton pickGalleryBtn;
    private ImageButton descriptionOk;
    private String TAG = "DescriptionActivity";
    private String imagePath;
    private Notification notification;
    private DescriptionSet descriptionSet;
    private Runnable locateRunnable = new Runnable() {

        @Override
        public void run() {
            Log.i(TAG, "run: locating ...");
            try {
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (lastLocation != null) {
                    DescriptionActivity.this.location = lastLocation;
                    locationEdit.setText(getLocationName(lastLocation));
                } else {

                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }
    };
    private boolean mResolvingError;
    private Runnable openGalleryRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: opening gallery");
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(imageIntent, "Select Receipt"), GALLERY_REQUEST_CODE);

        }
    };
    private Runnable openCameraRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "run: opening camera");
            notification.setCurrentActivity(DescriptionActivity.this);
            notification.setNotificationBundle("Coming soon");
            notification.notify(Notification.TOAST);
            /*
            Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(photoIntent, CAMERA_REQUEST_CODE);
            */
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        app = (BudgetApp) getApplication();
        location = null;
        setContentView(R.layout.activity_description);
        locationEdit = (EditText) findViewById(R.id.currentLocName);
        locateBtn = (ImageButton) findViewById(R.id.imageButton);
        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps();
            }
        });
        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation blink = AnimationUtils.loadAnimation(DescriptionActivity.this, R.anim.locbtn_animator);
                view.startAnimation(blink);
                locateCurrent();
            }
        });

        descriptionEdit = (EditText) findViewById(R.id.descriptionEdit);

        describeIt = (TextView) findViewById(R.id.describe);
        receiptImageView = (ImageView) findViewById(R.id.receiptImageView);
        receiptQue = (TextView) findViewById(R.id.receiptQue);
        receiptQue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onQueryReceiptAsked();
            }
        });
        takePicBtn = (ImageButton) findViewById(R.id.takePictureBtn);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        pickGalleryBtn = (ImageButton) findViewById(R.id.pickGalleryBtn);
        pickGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        descriptionOk = (ImageButton) findViewById(R.id.okDescriptionBtn);
        descriptionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDescriptionsDone();
            }
        });
        Intent intent = getIntent();
        handler = new Handler();
        uri = Uri.parse(app.getDirectory());
        notification = new Notification(this);
        if (intent.hasExtra("Amount")) {
            int amount = intent.getIntExtra("Amount", 0);
            String string = "Describe " + HelperFunc.getMoney(amount);
            describeIt.setText(string);
        }
        if (intent.hasExtra("description")) {
            descriptionSet = intent.getParcelableExtra("description");
            descriptionEdit.setText(!descriptionSet.getDescription().equals(DescriptionSet.DEFAULT)
                    ? descriptionSet.getDescription() : " ");
            locationEdit.setText(!descriptionSet.getLocationName().equals(DescriptionSet.DEFAULT)
                    ? descriptionSet.getLocationName() : " ");
            imagePath = descriptionSet.getReceiptName();
            if (!imagePath.equals(DescriptionSet.DEFAULT)) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                receiptImageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i(TAG, "onPostCreate: ");
        imagePath = "";
        mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(TAG, "onConnected: ");
                locateBtn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.i(TAG, "onConnectionSuspended: ");
            }
        };

        mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.i(TAG, "onConnectionFailed: ");
                if (connectionResult.hasResolution()) {
                    mResolvingError = true;
                    try {
                        connectionResult.startResolutionForResult(DescriptionActivity.this,
                                REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR);
                    } catch (IntentSender.SendIntentException e) {
                        mGoogleApiClient.connect();
                    }
                } else {
                    showGoogleAPIErrorDialog(connectionResult.getErrorCode());
                }

            }
        };
        setupGoogleApiClient();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: ");
        ResultsRunnable resultsRunnable = new ResultsRunnable(data, requestCode, resultCode);
        handler.post(resultsRunnable);
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String s = cursor.getString(column_index);
        cursor.close();
        String newfileName = saveReceipt(s);
        Log.i(TAG, "getPath: " + newfileName);
        return newfileName;
    }

    private void openGallery() {
        Log.i(TAG, "openGallery: ");
        handler.post(openGalleryRunnable);
    }

    private void takePicture() {
        Log.i(TAG, "takePicture: ");
        handler.post(openCameraRunnable);
    }

    private void onQueryReceiptAsked() {
        Log.i(TAG, "onQueryReceiptAsked: ");
        takePicBtn.setVisibility(View.VISIBLE);
        pickGalleryBtn.setVisibility(View.VISIBLE);
    }

    private void openMaps() {
        Log.i(TAG, "openMaps: ");
    }

    private void locateCurrent() {
        Log.i(TAG, "locateCurrent: ");
        handler.post(locateRunnable);
    }

    private void onDescriptionsDone() {
        Log.i(TAG, "onDescriptionsDone: ");
        finish();

    }

    @Override
    public void finish() {
        Log.i(TAG, "finish: ");
        Intent data = new Intent();
        String description = descriptionEdit.getText() != null ?
                descriptionEdit.getText().toString() : DescriptionSet.DEFAULT;
        String locationName = locationEdit.getText() != null ?
                locationEdit.getText().toString() : DescriptionSet.DEFAULT;
        String receiptName = !imagePath.isEmpty() ? imagePath : DescriptionSet.DEFAULT;
        descriptionSet = new DescriptionSet(description, locationName, receiptName);
        data.putExtra("Description", descriptionSet);
        setResult(RESULT_OK, data);

        super.finish();
    }

    protected synchronized void setupGoogleApiClient() {
        Log.i(TAG, "setupGoogleApiClient: ");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(
                        mOnConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private String getLocationName(Location location) {
        Log.i(TAG, "getLocationName: ");
        String name = "place unknown";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (list != null && !list.isEmpty()) {
                Address address = list.get(0);
                name = address.getAddressLine(0);
            }
        } catch (IOException | NullPointerException e) {
            Log.e(TAG, "getLocationName: ", e);
        }
        Log.i(TAG, "getLocationName: name " + name);
        return name;
    }

    private void showGoogleAPIErrorDialog(int errorCode) {
        Log.i(TAG, "showGoogleAPIErrorDialog: " + errorCode);
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Dialog errorDialog = googleApiAvailability.getErrorDialog(this, errorCode, REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR);
        errorDialog.show();
    }

    private String saveReceipt(String sourceName) {
        Log.i(TAG, "saveReceipt: ");
        File destination = new File(app.getDirectory("receipt" + System.currentTimeMillis()));
        File source = new File(sourceName);
        if (destination.exists() && source.exists()) {
            try {
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(destination);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                in.close();
                out.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return destination.getAbsolutePath();
    }

    private class ResultsRunnable implements Runnable {
        private Intent data;
        private int requestCode;
        private int resultCode;

        public ResultsRunnable(Intent data, int requestcode, int resultCode) {
            Log.i(TAG, "ResultsRunnable: ");
            this.data = data;
            this.requestCode = requestcode;
            this.resultCode = resultCode;
        }

        @Override
        public void run() {
            switch (requestCode) {
                case REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR: {
                    Log.i(TAG, "run: " + requestCode);
                    mResolvingError = false;
                    if (resultCode == RESULT_OK && !mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                    break;
                }
                case CAMERA_REQUEST_CODE: {
                    Log.i(TAG, "run: " + requestCode);
                    if (data == null) {
                        return;
                    }
                    if (resultCode == RESULT_OK) {
                        uri = data.getData();
                        imagePath = uri.toString();
                        Log.i(TAG, "run: imagepath "+imagePath);
                        receiptImageView.setImageURI(Uri.parse(imagePath));
                    }
                    break;
                }
                case GALLERY_REQUEST_CODE: {
                    Log.i(TAG, "run: " + requestCode);
                    if (resultCode == RESULT_OK) {
                        uri = data.getData();
                        imagePath = getPath(uri);
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        receiptImageView.setImageBitmap(bitmap);
                    }
                    break;
                }
            }
        }
    }

}
