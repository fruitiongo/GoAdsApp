package goads.app.com.goadsapp.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import goads.app.com.goadsapp.GoAdsApplication;
import goads.app.com.goadsapp.R;
import goads.app.com.goadsapp.RequestQueueProvider;
import goads.app.com.goadsapp.entity.Demand;
import goads.app.com.goadsapp.entity.DisplayDemandTag;
import goads.app.com.goadsapp.entity.Weather;
import goads.app.com.goadsapp.util.DistanceCalculator;
import goads.app.com.goadsapp.util.ImageAdapter;
import goads.app.com.goadsapp.util.TextAdAdapter;

/**
 * Created by Raj on 8/16/17.
 */

public class CanvasActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Location location;
    private final int LOCATION_REFRESH_TIME = 5, LOCATION_REFRESH_DISTANCE = 1;
    private ImageAdapter imageAdapter = null;
    private TextAdAdapter textAdAdapter = null;
    private GoogleMap googleMap;
    private AdView mAdView;
    private Weather weather;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //getLocationAds(location);
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
    };

    public void getWeather(final Location location){
        String baseURL = "http://query.yahooapis.com/v1/public/yql";
        String yqlQuery = "select * from weather.forecast where woeid in (select woeid from geo.places where text=\"("
                +location.getLatitude()+","+location.getLongitude()+")\")";
        try {
            String yqlQueryURL = baseURL + "?q=" + URLEncoder.encode(yqlQuery, "UTF-8") + "&format=json";
            Log.d("WEATHER QUERY ",yqlQueryURL);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, yqlQueryURL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                StringBuilder weatherHtmlBuilder = new StringBuilder();
                                Log.d("WEATHER ", response.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getString("description"));
                                JSONObject weatherItem = response.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item");
                                weatherHtmlBuilder.append(weatherItem.getString("title")).append("</BR>");
                                weather = new Weather();
                                weather.setConditions(weatherItem.getString("description").split("\n")[0].replace("<![CDATA[",""));
                                TextView textView = findViewById(R.id.weatherCondition);
                                textView.setText(weather.getConditions());
                                WebView weatherView = findViewById(R.id.WeatherView);
                                weatherView.setBackgroundColor(Color.DKGRAY);
                                weatherHtmlBuilder.append("<table><tr><td>");
                                weatherHtmlBuilder.append(weather.getConditions());
                                weatherHtmlBuilder.append("</td><td><table><tr><td>").append(weatherItem.getJSONObject("condition").getString("text"))
                                .append("</td></tr><tr><td>").append(weatherItem.getJSONObject("condition").getString("temp")).append("F</td></tr></table></tr></table>");
                                weatherView.loadData(weatherHtmlBuilder.toString(),"text/html",null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("WEATHER ERROR ", error.getLocalizedMessage());
                        }
                    }
            );
            RequestQueue requestQueue = RequestQueueProvider.getInstance(GoAdsApplication.getContext()).getRequestQueue();
            requestQueue.add(jsonObjectRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getLocationAds(final Location location) {
        try {
            String URL = "http://" +getString(R.string.api_host)+ ":8080/platform/api/ads";
            URL = URL + "?lat=" + location.getLatitude() + "&lng=" + location.getLongitude();
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(final JSONArray response) {
                            Log.d("RESPONSE", response.toString());
                            List<Demand> demandList = filterAndSort(response, location);
                            /*for(Demand demand : demandList){
                                Marker marker = addMarker(demand.getSelectedLocation().latitude, demand.getSelectedLocation().longitude);
                                demand.setMarker(marker);
                            }*/
                            imageAdapter.setImages(response);
                            textAdAdapter.setImages(demandList);
                            textAdAdapter.setGoogleMap(googleMap);
                            GridView gridView = (GridView)findViewById(R.id.gridview1);
                            //gridView.setAdapter(imageAdapter);
                            gridView.setAdapter(textAdAdapter);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    Intent i = new Intent(getApplicationContext(), ClickActivity.class);
                                    // Pass image index
                                    try {
                                        i.putExtra("cURL", (response.getJSONObject(position).getJSONObject("displayDemandTag").get("clickThroughURL")).toString());
                                        startActivity(i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.getLocalizedMessage());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = super.getHeaders();
                    if (params == null || params.size() == 0)
                        params = new HashMap<String, String>();
                    params.put("Authorization", "Token ZmRmMDcwMzBhMTQ2ZTQ3MDE1YTM0YzIzODJhYjM0ZDY=");
                    //..add other headers
                    return params;
                }
            };
            RequestQueue requestQueue = RequestQueueProvider.getInstance(GoAdsApplication.getContext()).getRequestQueue();
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_render);
        imageAdapter = new ImageAdapter(this);
        textAdAdapter = new TextAdAdapter(this);
        GridView gridView = (GridView)findViewById(R.id.gridview1);
        gridView.setAdapter(textAdAdapter);
        if(gridView != null){
            Location bestLocation = null;
            bestLocation = getLocation();
            if(bestLocation != null){
                getLocationAds(bestLocation);
                getWeather(bestLocation);
            }
            else {
                Log.d("LOCATION", "NO LOCATION DATA");
            }
        }
            /*LocationManager GoadsLocationManger = (LocationManager) getSystemService(LOCATION_SERVICE);
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck == PermissionChecker.PERMISSION_GRANTED){
                List<String> providers = GoadsLocationManger.getProviders(true);
                Location bestLocation = null;
                for (String provider : providers) {
                    Location l = GoadsLocationManger.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l;
                    }
                }
                //GoadsLocationManger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                //LOCATION_REFRESH_DISTANCE, mLocationListener);
                //Location currentLocation = GoadsLocationManger.getLastKnownLocation(GoadsLocationManger.NETWORK_PROVIDER);
                if(bestLocation != null)
                    getLocationAds(bestLocation);
                else {
                    Log.d("LOCATION", "NO LOCATION DATA");
                }
            }else {
                Log.d("LOCATION", "Location permission is NA");
            }
            //Log.d("IMAGES",""+imageAdapter.getCount());
        }else {
            Log.d("grid view ", "grid view NA");
        }*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        /*LocationManager GoadsLocationManger = (LocationManager) getSystemService(LOCATION_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PermissionChecker.PERMISSION_GRANTED)
            GoadsLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        Location currentLocation = GoadsLocationManger.getLastKnownLocation(GoadsLocationManger.NETWORK_PROVIDER);
        getLocationAds(currentLocation);*/
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    public static void setLocation(Location location){
        location = location;
    }

    private Location getLocation(){
        Location bestLocation = null;
        LocationManager GoadsLocationManger = (LocationManager) getSystemService(LOCATION_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PermissionChecker.PERMISSION_GRANTED){
            List<String> providers = GoadsLocationManger.getProviders(true);
            for (String provider : providers) {
                Location l = GoadsLocationManger.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            //GoadsLocationManger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
            //LOCATION_REFRESH_DISTANCE, mLocationListener);
            //Location currentLocation = GoadsLocationManger.getLastKnownLocation(GoadsLocationManger.NETWORK_PROVIDER);
        }else {
            Log.d("LOCATION", "Location permission is NA");
        }
        return bestLocation;
    }

    private List<Demand> filterAndSort(JSONArray jsonArray, Location currentLocation){
        Gson gson = new GsonBuilder().create();
        List<Demand> sortedDemand = new ArrayList<>(
        );
        try{
            for(int i=0; i<jsonArray.length(); i++){
                JSONArray targetArray = jsonArray.getJSONObject(i).getJSONObject("locationTarget").getJSONArray("target");
                Log.d("Json ",targetArray.toString());
                double minDistance = 500;
                LatLng location = null;
                for(int j = 0; j<targetArray.length(); j++){
                    JSONObject placeObject = targetArray.getJSONObject(j);
                    double lat = placeObject.getDouble("lat");
                    double lng = placeObject.getDouble("lng");
                    int radius = placeObject.getInt("radius");
                    double distance = DistanceCalculator.distance(currentLocation.getLatitude(), currentLocation.getLongitude(), lat, lng, "M");
                    if(distance <= radius){
                        if(minDistance > distance){
                            minDistance = distance;
                            location = new LatLng(lat, lng);
                        }
                    }
                }
                if(minDistance>0)
                {
                    Demand demand = new Demand();
                    demand.setDemandTagID(jsonArray.getJSONObject(i).getInt("demandTagID"));
                    demand.setDisplayDemandTag(gson.fromJson((jsonArray.getJSONObject(i).getString("displayDemandTag")), DisplayDemandTag.class));
                    demand.setMinDistance(minDistance);
                    demand.setSelectedLocation(location);
                    sortedDemand.add(demand);
                    Log.d("Demand", "adding demand");
                }
            }
            System.out.println("Persons by  Name: ");
            //sortedDemand.forEach(System.out::println);
        }catch (Exception e){
            e.printStackTrace();
        }
        Collections.sort(sortedDemand, new Comparator<Demand>()
        {
            @Override
            public int compare(Demand lhs, Demand rhs) {

                return Double.valueOf(lhs.getMinDistance()).compareTo(rhs.getMinDistance());
            }
        });
        return sortedDemand;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Location location = getLocation();
        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
    }

    public Marker addMarker(double lat, double lng){
        LatLng markerLocation = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
        Marker marker = googleMap.addMarker(markerOptions);
        return marker;
    }
}
