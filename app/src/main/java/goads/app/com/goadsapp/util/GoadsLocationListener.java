package goads.app.com.goadsapp.util;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import goads.app.com.goadsapp.GoAdsApplication;

/**
 * Created by Raj on 8/16/17.
 */

public class GoadsLocationListener implements LocationListener {
    private Location location;
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
