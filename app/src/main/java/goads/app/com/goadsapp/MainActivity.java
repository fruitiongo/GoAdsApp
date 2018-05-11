package goads.app.com.goadsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import goads.app.com.goadsapp.activity.CanvasActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int LOCATION_REFRESH_TIME = 5, LOCATION_REFRESH_DISTANCE = 1;
    private JSONArray ads;
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER = "checked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);
        boolean remember = pref.getBoolean(PREF_REMEMBER, false);
        if(username == null || password == null || !remember){
            setContentView(R.layout.activity_main);
        }else {
            authenticate(username, password, remember);
        }
    }

    @Override
    public void onClick(View view) {
        Log.d("Clicked", "Login");
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();
        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        String email = emailEditText.getText().toString();
        boolean isLoginValid = true;
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        authenticate(email, password, checkBox.isChecked());
    }

    private void authenticate(String email, String password, boolean remember){
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
            emailEditText.setError("Invalid Email");
            return;
        }
        if (TextUtils.isEmpty(password) || !(password.length() >= 8) || TextUtils.isDigitsOnly(password)) {
            EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
            passwordEditText.setError("Invalid Password");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject queryJson = new JSONObject();
            queryJson.put("email", email);
            queryJson.put("password", password);
            queryJson.put("authType", "dooh_pub");
            jsonObject.put("query", queryJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("API HOST",getString(R.string.api_host));
        String URL = "http://"+getString(R.string.api_host)+":8080/platform/ui/login";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LOGIN", response.toString());
                        try {
                            Intent i = new Intent(getApplicationContext(), CanvasActivity.class);
                            i.putExtra("API_KEY", response.getJSONObject("data").getString("apiToken"));

                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            pref.edit().putString(PREF_USERNAME, email)
                            .putString(PREF_PASSWORD,password)
                                    .putBoolean(PREF_REMEMBER,remember)
                                    .commit();
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //activity.setContentView(R.layout.canvas_render);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("Error.Response", error.getLocalizedMessage());
                        //Toast toast = new Toast(MainActivity.this);
                        Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                        //toast.show();
                    }
                }
        );
        RequestQueue requestQueue = RequestQueueProvider.getInstance(GoAdsApplication.getContext()).getRequestQueue();
        requestQueue.add(jsonObjectRequest);
    }

    private void validateLogin(String email, String password){


        //activity.setContentView(R.layout.canvas_render);

    }
}
