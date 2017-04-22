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

package com.octopus.wallet.m.p.nt;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.octopus.wallet.m.p.fl.FUri;
import com.octopus.wallet.m.p.nt.models.LocationData;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.yoctopus.cac.util.LogUtil;

/**
 * Created by octopus on 10/17/16.
 */
public class NBridge {
    private static String TAG = LogUtil.makeTag(NBridge.class);
    private String url;
    private Context context;
    private Activity activity;
    private LocationData locationData = null;
    private boolean canFindLocation;
    private boolean resolvingError;
    private Listener listener;
    private final int REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR = 1;
    private GoogleApiClient googleApiClient;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;

    public NBridge(String url) {
        LogUtil.i(TAG,
                "NBridge: ");
        this.setUrl(url);
        init();
    }

    public NBridge() {
        init();
    }



    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null &&
                networkInfo.isConnected());
    }

    private void init() {
        LogUtil.i(TAG,
                "init: ");

    }

    private void initLocationVariables() {
        setLocationData(
                new LocationData("location"));
        setConnectionCallbacks(
                new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                LogUtil.i(TAG,
                        "onConnected: ");
                setCanFindLocation(true);
                getGoogleApiClient().connect();
                try {
                    Location loc =
                            LocationServices.FusedLocationApi
                                    .getLastLocation(getGoogleApiClient());
                    if (loc != null) {
                        getLocationData()
                                .setLocationName(getLocationName(loc));
                        getLocationData()
                                .setLocation(loc);
                        LogUtil.i(TAG,
                                "onConnected: loc " +
                                "Lat: " + loc.getLatitude() +
                                "Lon: " + loc.getLongitude() +
                                "provider " + loc.getProvider());
                        LogUtil.i(TAG,
                                "onConnected: locationName " +
                                getLocationData().getLocationName());

                    } else {
                        setLocationData(null);
                    }
                    returnData(getLocationData());
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                LogUtil.i(TAG,
                        "onConnectionSuspended: ");
            }
        });
        setConnectionFailedListener(
                new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(
                    @NonNull ConnectionResult connectionResult) {
                LogUtil.i(TAG,
                        "onConnectionFailed: ");
                if (connectionResult.hasResolution()) {
                    setResolvingError(true);
                    try {
                        connectionResult
                                .startResolutionForResult(getActivity(),
                                REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR);

                    } catch (IntentSender.SendIntentException e) {
                        getGoogleApiClient().connect();
                    }
                } else {
                    showGoogleAPIErrorDialog(
                            connectionResult.getErrorCode());
                }
            }
        });
    }

    public NData getCurrentLocation() {
        initLocationVariables();
        setupGoogleApiClient();
        LogUtil.i(TAG, "getCurrentLocation: return");
        return getLocationData();
    }

    private synchronized void setupGoogleApiClient() {
        LogUtil.i(TAG,
                "setupGoogleApiClient: ");
        setGoogleApiClient(new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(getConnectionCallbacks())
                .addOnConnectionFailedListener(
                        getConnectionFailedListener())
                .addApi(LocationServices.API)
                .build());
        getGoogleApiClient().connect();
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
                        REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR);
        errorDialog.show();
    }

    private String getLocationName(Location location) {
        String name = "";
        Geocoder geocoder = new Geocoder(getContext(),
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



    public boolean downLoad(FUri fUri,
                         String url) {
        final DownloadManager manager =
                (DownloadManager)
                        context.getSystemService(
                                Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request =
                new DownloadManager.Request(
                        Uri.parse(url));
        String sDest = fUri.toString();
        request.setDestinationUri(Uri.parse(sDest));
        returnData(new NData("reference",
                manager.enqueue(request)));
        return true;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }


    public void setCanFindLocation(boolean canFindLocation) {
        this.canFindLocation = canFindLocation;
    }


    public void setResolvingError(boolean resolvingError) {
        this.resolvingError = resolvingError;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public GoogleApiClient.ConnectionCallbacks getConnectionCallbacks() {
        return connectionCallbacks;
    }

    public void setConnectionCallbacks(
            GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        this.connectionCallbacks = connectionCallbacks;
    }

    public GoogleApiClient
            .OnConnectionFailedListener
    getConnectionFailedListener() {
        return connectionFailedListener;
    }

    public void setConnectionFailedListener(
            GoogleApiClient.OnConnectionFailedListener
                    connectionFailedListener) {
        this.connectionFailedListener = connectionFailedListener;
    }

    public interface Listener<T extends NData> {
        void onComplete(T t);

    }

    private <T extends NData> void returnData(final T t) {
        if (getListener() != null) {
            getListener().onComplete(t);
        }

    }

    public <T extends NData> Listener<T> getListener() {
        return listener;
    }

    public <T extends NData> void setListener(Listener<T> listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
