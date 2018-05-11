package goads.app.com.goadsapp.goads.app.com.login;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import goads.app.com.goadsapp.GoAdsApplication;
import goads.app.com.goadsapp.R;
import goads.app.com.goadsapp.RequestQueueProvider;

/**
 * Created by Raj on 8/3/17.
 */

public class DriverLogin {

    final private Activity activity;
    private String apiKey;

    public DriverLogin(Activity activity)
    {
        this.activity = activity;
    }

    private boolean isValid = false;
    public boolean validateLogin(String email, String password){
        setLogin(true);
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject queryJson = new JSONObject();
            queryJson.put("email",email);
            queryJson.put("password",password);
            queryJson.put("authType","dooh_pub");
            jsonObject.put("query",queryJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //activity.setContentView(R.layout.canvas_render);
        String URL = "http://10.0.2.2:8080/platform/ui/login";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LOGIN", response.toString());
                        try {
                            setApiKey(response.getJSONObject("data").getString("apiToken"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        isValid = true;
                        //activity.setContentView(R.layout.canvas_render);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("Error.Response", error.getLocalizedMessage());
                    }
                }
        );
        RequestQueue requestQueue = RequestQueueProvider.getInstance(GoAdsApplication.getContext()).getRequestQueue();
        requestQueue.add(jsonObjectRequest);
        return isValid;
    }

    public void setLogin(boolean isValid)
    {
        this.isValid = isValid;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
