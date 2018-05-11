package goads.app.com.goadsapp.entity;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Raj on 8/30/17.
 */

public class Demand {
    private int demandTagID;
    private DisplayDemandTag displayDemandTag;
    private double minDistance;
    private LatLng selectedLocation;
    private Bitmap icon;
    private Marker marker;

    public int getDemandTagID() {
        return demandTagID;
    }

    public void setDemandTagID(int demandTagID) {
        this.demandTagID = demandTagID;
    }

    public DisplayDemandTag getDisplayDemandTag() {
        return displayDemandTag;
    }

    public void setDisplayDemandTag(DisplayDemandTag displayDemandTag) {
        this.displayDemandTag = displayDemandTag;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public LatLng getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(LatLng selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
