package com.beltonzhong.ilovenougat;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

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
            holder.brandName.setText(item.getString("brandName"));
            holder.productName.setText(item.getString("productName"));
            holder.price.setText(item.getString("price"));
            if(!item.getString("price").equals(item.getString("originalPrice"))) {
                holder.oldPrice.setText(item.getString("originalPrice"));
                holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.percentOff.setText("-" + item.getString("percentOff"));
            }
            new LoadImageTask(holder.thumbnail).execute(item.getString("thumbnailImageUrl"));
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
}
