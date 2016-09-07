package com.beltonzhong.ilovenougat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import static android.widget.Toast.LENGTH_SHORT;
import static com.beltonzhong.ilovenougat.MainActivity.getRequest;

/**
 * Created by beltonzhong on 9/4/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private JSONArray dataset;

    public MyAdapter(JSONArray data) {
        dataset = data;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        try {
            JSONObject item = dataset.getJSONObject(position);
            final String productName = item.getString("productName");
            final String productId = item.getString("productId");
            final String price = item.getString("price");
            final TextView note = (TextView) holder.itemView.findViewById(R.id.price_notification);
            holder.brandName.setText(item.getString("brandName"));
            holder.productName.setText(productName);
            holder.price.setText(price);
            if(!price.equals(item.getString("originalPrice"))) {
                holder.oldPrice.setText(item.getString("originalPrice"));
                holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.percentOff.setText("-" + item.getString("percentOff"));
            }
            new LoadImageTask(holder.thumbnail).execute(item.getString("thumbnailImageUrl"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SixPMRequestTask(productId, price, note).execute(productName);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("MyAdapter", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return dataset.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout itemView;
        private ImageView thumbnail;
        private TextView brandName;
        private TextView productName;
        private TextView oldPrice;
        private TextView price;
        private TextView percentOff;

        public ViewHolder(View view) {
            super(view);
            itemView = (RelativeLayout) view;
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            brandName = (TextView) itemView.findViewById(R.id.brand_name);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            oldPrice = (TextView) itemView.findViewById(R.id.old_price);
            price = (TextView) itemView.findViewById(R.id.price);
            percentOff = (TextView) itemView.findViewById(R.id.percent_off);
        }
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap image = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("MyAdapter", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public class SixPMRequestTask extends AsyncTask<String, Void, String> {
        private TextView notification;
        private String productId;
        private String price;

        public SixPMRequestTask(String productId, String price, TextView note) {
            this.productId = productId;
            this.price = price;
            this.notification = note;
        }

        @Override
        protected String doInBackground(String... params) {
            return getRequest("https://api.6pm.com/Search?term=%3C" + params[0] + "%3E&key=524f01b7e2906210f7bb61dcbe1bfea26eb722eb");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject resultObject = new JSONObject(result);
                JSONArray resultsArray = resultObject.getJSONArray("results");
                for(int i = 0; i < resultsArray.length(); i++) {
                    JSONObject sixPMResult = (JSONObject) resultsArray.get(i);
                    String id = sixPMResult.getString("productId");
                    if(id.equals(productId)) {
                        double sixPrice = Double.parseDouble(sixPMResult.getString("price").substring(1));
                        double zapposPrice = Double.parseDouble(price.substring(1));
                        if(sixPrice < zapposPrice)
                            notification.setText("This item is available for $" + sixPrice + " at 6pm.com");
                        else
                            break;
                    }
                }
            } catch (JSONException e) {
                Log.e("MainActivity", e.getMessage());
                e.printStackTrace();
            }

        }
    }
}

