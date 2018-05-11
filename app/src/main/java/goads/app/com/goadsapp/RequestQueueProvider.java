package goads.app.com.goadsapp;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Raj on 8/8/17.
 */

public class RequestQueueProvider {
    private static RequestQueueProvider ourInstance;
    private static Context mCtx;

    public static synchronized RequestQueueProvider getInstance(Context context) {
        mCtx = context;
        if(ourInstance == null)
        {
            ourInstance = new RequestQueueProvider(context);
        }
        return ourInstance;
    }

    private RequestQueue mRequestQueue;

    private RequestQueueProvider(Context context) {
        getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }
}
