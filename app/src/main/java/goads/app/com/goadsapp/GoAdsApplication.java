package goads.app.com.goadsapp;

import android.app.Application;
import android.content.Context;
import android.location.Location;

/**
 * Created by Raj on 8/8/17.
 */

public class GoAdsApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        GoAdsApplication.context = getApplicationContext();
    }

    public static Context getContext(){
        return GoAdsApplication.context;
    }
}
