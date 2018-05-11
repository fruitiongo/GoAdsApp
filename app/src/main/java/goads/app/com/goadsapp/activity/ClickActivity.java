package goads.app.com.goadsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import goads.app.com.goadsapp.R;

/**
 * Created by Raj on 8/29/17.
 */

public class ClickActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clicklayout);
        Intent i = getIntent();
        String clickURL = i.getExtras().get("cURL").toString();
        WebView webView = (WebView)findViewById(R.id.lpview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(clickURL);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
    }
}

