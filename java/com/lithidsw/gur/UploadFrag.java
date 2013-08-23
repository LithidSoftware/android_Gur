package com.lithidsw.gur;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lithidsw.gur.adapter.UploadAdapter;
import com.lithidsw.gur.database.QueTable;
import com.lithidsw.gur.database.SavedTable;
import com.lithidsw.gur.service.UploadService;
import com.lithidsw.gur.utils.NotificationUpload;
import com.lithidsw.gur.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadFrag extends Fragment {

    private FragmentActivity fa;
    private UploadAdapter adapter;
    private BroadcastReceiver mReceiver = null;

    private QueTable qt;

    private TextView addImageTxt;
    private Button addImage;
    private Button takePic;
    private GridView mainGrid;
    private LinearLayout footer;
    String mCurrentPhotoPath;
    Bitmap quick_camera_bit = null;

    ArrayList<String[]> imageItems = new ArrayList<String[]>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(
                R.layout.frag_upload, container, false);

        fa = getActivity();

        adapter = new UploadAdapter(fa, imageItems);
        mainGrid = (GridView) ll.findViewById(R.id.grid_view);
        mainGrid.setAdapter(adapter);
        addImageTxt = (TextView) ll.findViewById(R.id.add_img_txt);
        addImage = (Button) ll.findViewById(R.id.button_add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setType("image/*");
                in.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(in, "Select Image"), 9999);
            }
        });
        takePic = (Button) ll.findViewById(R.id.button_take_pic);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = null;
                try {
                    f = createImageFile();
                    in.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(in, 9998);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(fa, "Couldn't get file path, try again", Toast.LENGTH_LONG).show();
                }
            }
        });

        updateLoader();
        return ll;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerUpdateItemsListener();
        if (!isMyServiceRunning()) {
            checkQue();
        }
        updateLoader();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            fa.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                "Gur"
        );

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        String JPEG_FILE_PREFIX = "Gur";
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp;
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        System.out.println(f.getAbsolutePath());
        quick_camera_bit = BitmapFactory.decodeFile(f.getAbsolutePath());
        mediaScanIntent.setData(contentUri);
        fa.sendBroadcast(mediaScanIntent);
        qt = new QueTable(fa);
        qt.updatedItem("Image", mCurrentPhotoPath, Utils.calculateMD5(mCurrentPhotoPath));
        updateLoader();
    }

    private boolean addGalleryImage(Uri uri) {
        String image_path = null;
        Cursor cursor = fa.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
        cursor.moveToFirst();
        image_path = cursor.getString(0);
        cursor.close();

        String md5 = Utils.calculateMD5(image_path);

        if (new QueTable(fa).isMd5(md5) || new SavedTable(fa).isMd5(md5)) {
            return false;
        }
        qt = new QueTable(fa);
        qt.updatedItem("Image", image_path, md5);
        updateLoader();
        return true;
    }

    private void checkQue() {
        if (new QueTable(fa).getQueCount() > 0) {
            if (!isMyServiceRunning()) {
                fa.startService(new Intent(fa, UploadService.class));
            }
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) fa.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (UploadService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateLoader() {
        new QueLoader().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == fa.RESULT_OK) {
            switch (requestCode) {
                case 9999:
                    if (!addGalleryImage(data.getData())) {
                        Toast.makeText(fa, "Picture already exists!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 9998:
                    galleryAddPic();
                    break;
            }
            checkQue();
        }
    }

    private void updateview(int count) {
        if (count > 0) {
            mainGrid.setVisibility(View.VISIBLE);
            addImageTxt.setVisibility(View.GONE);
        } else {
            mainGrid.setVisibility(View.GONE);
            addImageTxt.setVisibility(View.VISIBLE);
        }
    }

    private void registerUpdateItemsListener() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String mAction = intent.getAction();
                    if (mAction != null && mAction.equals("com.lithidsw.gur.UPLOAD_COMPLETE")) {
                        System.out.println("Gur, getting broadcast now!");
                        updateLoader();
                    }
                }
            };

            IntentFilter mFilter = new IntentFilter("com.lithidsw.gur.UPLOAD_COMPLETE");
            getActivity().registerReceiver(mReceiver, mFilter);
        }
    }

    class QueLoader extends AsyncTask<String, String, ArrayList<String[]>> {
        @Override
        protected void onPreExecute() {
            mainGrid.invalidateViews();
        }

        @Override
        protected ArrayList<String[]> doInBackground(String... strings) {
            return new QueTable(fa).getAllQue();
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> list) {
            imageItems.clear();
            imageItems.addAll(list);
            adapter.notifyDataSetChanged();
            updateview(imageItems.size());
        }
    }
}
