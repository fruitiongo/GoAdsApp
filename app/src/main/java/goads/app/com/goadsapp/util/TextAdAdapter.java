package goads.app.com.goadsapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import goads.app.com.goadsapp.R;
import goads.app.com.goadsapp.entity.Demand;

/**
 * Created by Raj on 9/14/17.
 */

public class TextAdAdapter extends BaseAdapter {
    private Context mContext;
    private List<Demand> mThumbIds = new ArrayList<Demand>();
    private GoogleMap googleMap;

    public TextAdAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.textadview, null);
        }
            Demand demand = mThumbIds.get(position);
            String logoURL = demand.getDisplayDemandTag().getFaviconURL();
            //String logoURL = mThumbIds.getJSONObject(position).getJSONObject("displayDemandTag").get("faviconURL").toString();
            /*if(logoURL!=null && !logoURL.equals("null"))
                logoURL = "http://"+logoURL+"/favicon.ico";
            logoURL = "http://www.target.com/favicon.ico";*/
            //logoURL = "http://4.bp.blogspot.com/-A4Gd9dkfpDk/UEjcHGktS2I/AAAAAAAAG0g/XAwIHhkHuT4/s1600/macys.jpg";
            final String description = demand.getDisplayDemandTag().getDescription();
            //final String description = mThumbIds.getJSONObject(position).getJSONObject("displayDemandTag").get("description").toString();
            final String title = demand.getDisplayDemandTag().getTitle();
            //final String title = mThumbIds.getJSONObject(position).getJSONObject("displayDemandTag").get("title").toString();

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            //Log.d("LOGO URL ",logoURL);
            if(logoURL!=null && !logoURL.equals("null")) {
                try {
                    new DownloadImageTask(imageView, demand.getSelectedLocation())
                            .execute(logoURL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            TextView distanceView = (TextView)view.findViewById(R.id.textView2);
        distanceView.setText(Math.round(demand.getMinDistance())+"m");
            TextView titleView = (TextView)view.findViewById(R.id.textView4);
            titleView.setText(title);
            TextView descriptionView = (TextView)view.findViewById(R.id.textView3);
            descriptionView.setText(description);
        return view;
    }

    public void setImages(List<Demand> demandList){
        mThumbIds = demandList;

    }

    public void setGoogleMap(GoogleMap googleMap){
        this.googleMap = googleMap;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        LatLng location;

        public DownloadImageTask(ImageView bmImage, LatLng location) {
            this.bmImage = bmImage;
            this.location = location;
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            HttpsURLConnection secureConnection = null;
            InputStream is = null;
            ByteArrayOutputStream out = null;
            boolean displayProgress = false;
            try {
                if(urldisplay.startsWith("https")){
                    secureConnection = (HttpsURLConnection) new URL(urldisplay).openConnection();
                    secureConnection.setInstanceFollowRedirects(true);
                    is = secureConnection.getInputStream();
                    Log.d("FINAL URL ",secureConnection.getURL().toURI().toString());
                    bitmap = BitmapFactory.decodeStream(is);
                }else {
                    connection = (HttpURLConnection) new URL(urldisplay).openConnection();
                    connection.setInstanceFollowRedirects(true);
                    is = connection.getInputStream();
                    Log.d("FINAL URL ",connection.getURL().toURI().toString());
                    bitmap = BitmapFactory.decodeStream(is);
                }
                /*if (displayProgress) {
                    connection.connect();
                    final int length = connection.getContentLength();
                    if (length <= 0) {
                        //error = new ImageError("Invalid content length. The URL is probably not pointing to a file")
                                //.setErrorCode(ImageError.ERROR_INVALID_FILE);
                        this.cancel(true);
                    }
                    is = new BufferedInputStream(connection.getInputStream(), 8192);
                    out = new ByteArrayOutputStream();
                    byte bytes[] = new byte[8192];
                    int count;
                    long read = 0;
                    while ((count = is.read(bytes)) != -1) {
                        read += count;
                        out.write(bytes, 0, count);
                        //publishProgress((int) ((read * 100) / length));
                    }
                    bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
                } else {*/

                //}
            } catch (Throwable e) {
                if (!this.isCancelled()) {
                    //error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
                    this.cancel(true);
                }
            } finally {
                try {
                    if (connection != null)
                        connection.disconnect();
                    if(secureConnection != null)
                        secureConnection.disconnect();
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    if (is != null)
                        is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
            /*Bitmap mIcon11 = null;
            try {
                //InputStream in = new java.net.URL(urldisplay).openStream();
                java.net.URL url = new java.net.URL(urldisplay);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream in = connection.getInputStream();
                if(in != null){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = calculateInSampleSize(options,32,32);
                    options.inJustDecodeBounds = true;
                    //mIcon11 = BitmapFactory.decodeStream(in, null, options);
                    /*try {
                        in.close();
                        if(in.markSupported())
                        in.mark(0);
                        //in.reset();
                    } catch (IOException e) {
                        return null;
                    }*/
                    /*mIcon11 = BitmapFactory.decodeStream(in, null, options);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read=0;
                    while((read = in.read()) != -1){
                        bos.write(read);
                    }
                    byte[] ba = bos.toByteArray();
                    //BitmapFactory.decodeByteArray(ba,0,ba.length);
                    Log.d("BYTE ARRAY ", ""+ba.length);
                    //mIcon11 = BitmapFactory.decodeByteArray(ba, 0, ba.length, options);//new FlushedInputStream(inputStream));
                    options.inJustDecodeBounds = false;
                }else {
                    Log.d("BITMAP", "NULL");
                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }*/
            //return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            //addMarker(location.latitude, location.longitude, result);
        }

        public void addMarker(double lat, double lng, Bitmap bitmap){
            LatLng markerLocation = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions().position(markerLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            Marker marker = googleMap.addMarker(markerOptions);
        }
    }
}
