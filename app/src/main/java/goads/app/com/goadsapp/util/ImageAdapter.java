package goads.app.com.goadsapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import goads.app.com.goadsapp.R;

/**
 * Created by Raj on 8/28/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private JSONArray mThumbIds = new JSONArray();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        WebView webView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            webView = new WebView(mContext);
            webView.setLayoutParams(new ViewGroup.LayoutParams(300,50));
            webView.setInitialScale(100);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            try {
                webView.loadUrl("http://192.168.3.54:8080/platform/api/tag?dt=1&dist=0.3", getHeaders());
            } catch (AuthFailureError authFailureError) {
                authFailureError.printStackTrace();
            }
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(400, 300));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //imageView.setPadding(8, 8, 8, 8);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300,250));
            //imageView.setMaxHeight(200);
            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200,200);
            //imageView.setLayoutParams(layoutParams);
        } else {
            webView = (WebView) convertView;
        }
        /*try{
            new DownloadImageTask(imageView)
                    .execute(mThumbIds.getJSONObject(position).getJSONObject("displayDemandTag").get("imageFile").toString());
            //imageView.setImageURI(Uri.parse());
            //imageView.setImageDrawable(mThumbIds[position]);
            //imageView.setImageResource(mThumbIds[position]);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        return webView;
    }

    public void setImages(JSONArray jsonArray){
        mThumbIds = jsonArray;

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = null;
        if (params == null || params.size() == 0)
            params = new HashMap<String, String>();
        params.put("Authorization", "Token ZmRmMDcwMzBhMTQ2ZTQ3MDE1YTM0YzIzODJhYjM0ZDY=");
        //..add other headers
        return params;
    }

    // references to our images
}
