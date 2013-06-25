package com.lithidsw.gur.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lithidsw.gur.R;
import com.lithidsw.gur.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SavedAdapter extends BaseAdapter {

    private Activity activity;
    private List<String[]> savedItems = new ArrayList<String[]>();
    private static LayoutInflater inflater = null;
    View vi;

    ImageLoader imageLoader;

    public SavedAdapter(Activity a, List<String[]> b) {
        activity = a;
        savedItems = b;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(activity, 200);

        System.out.println("Gur, Started the adapter for saved items");
    }

    @Override
    public int getCount() {
        try {
            return savedItems.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.saved_items, null);
        }

        final String imagePath = savedItems.get(position)[4];
        final int pos = position;

        System.out.println("Gur, IMAGE: "+imagePath);

        ImageView image = (ImageView) vi.findViewById(R.id.picture);
        imageLoader.DisplayImage(imagePath, image);

        return vi;
    }
}
