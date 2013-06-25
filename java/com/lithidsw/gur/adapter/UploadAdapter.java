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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.lithidsw.gur.R;
import com.lithidsw.gur.loader.ImageLoader;

public class UploadAdapter extends BaseAdapter {

    private Activity activity;
    private List<String[]> uploadItems = new ArrayList<String[]>();
    private static LayoutInflater inflater = null;
    View vi;

    ImageLoader imageLoader;

    public UploadAdapter(Activity a, List<String[]> b) {
        activity = a;
        uploadItems = b;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(activity, 200);
    }

    @Override
    public int getCount() {
        try {
            return uploadItems.size();
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
            vi = inflater.inflate(R.layout.upload_items, null);
        }

        final String imagePath = uploadItems.get(position)[2];
        final int pos = position;

        ImageView image = (ImageView) vi.findViewById(R.id.picture);
        imageLoader.DisplayImage(imagePath, image);

        TextView text = (TextView) vi.findViewById(R.id.text);
        text.setText("Image "+(position+1));

        ProgressBar pbar = (ProgressBar) vi.findViewById(R.id.progress);
        if (position == 0) {
            pbar.setIndeterminate(true);
        } else {
            pbar.setIndeterminate(false);
        }

        ImageButton btnRemove = (ImageButton) vi.findViewById(R.id.btn_remove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadItems.remove(pos);
                notifyDataSetChanged();
            }
        });

        return vi;
    }
}
