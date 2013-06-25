package com.lithidsw.gur.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.lithidsw.gur.database.SavedTable;
import com.lithidsw.gur.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ImageUploader {

    Context context;

    public ImageUploader(Context context) {
        this.context = context;
    }

    public boolean uploadImage(String filename, Integer pos) {
        Bitmap bitmap = BitmapFactory.decodeFile(filename);
        String link = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        URL url = null;
        String data = null;
        URLConnection conn = null;
        OutputStreamWriter wr = null;
        try {
            url = new URL("https://api.imgur.com/3/image");
            data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT), "UTF-8");
            System.out.println(data);
            conn = url.openConnection();
            conn.addRequestProperty("Authorization", "Client-ID 0ef1f24ce10a22c");
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            InputStream is;
            if (((HttpURLConnection) conn).getResponseCode() == 400) {
                is = ((HttpURLConnection) conn).getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String inputLine;
            inputLine = in.readLine();
            JSONObject json = null;
            try {
                json = new JSONObject(inputLine);
                if (json.getBoolean("success")) {
                    JSONObject c = json.getJSONObject("data");
                    link = c.getString("link");
                    String md5 = Utils.calculateMD5(filename);
                    new SavedTable(context).updatedItem("Image_"+pos, "Desc_"+pos, link, md5, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
