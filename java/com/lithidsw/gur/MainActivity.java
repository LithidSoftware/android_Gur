package com.lithidsw.gur;

import java.util.Locale;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lithidsw.gur.database.QueTable;
import com.lithidsw.gur.database.SavedTable;
import com.lithidsw.gur.service.UploadService;
import com.lithidsw.gur.utils.NotificationUpload;
import com.lithidsw.gur.utils.Utils;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    private int opentab = 0;
    NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        Intent in = getIntent();
        if (in.getExtras() != null) {
            opentab = in.getExtras().getInt("opentab", 0);
            System.out.println("Gur, opentab: "+opentab);
            boolean clear = in.getExtras().getBoolean("clear", false);
            if (clear) {
                nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(0);
            }

            if (in.getAction().equals("android.intent.action.SEND")) {
                Uri imageUri = (Uri) in.getExtras().get(Intent.EXTRA_STREAM);
                Toast.makeText(this, "Setting image: "+ imageUri.toString(), Toast.LENGTH_LONG).show();
                addGalleryImage(imageUri);
                finish();
            }
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        actionBar.setSelectedNavigationItem(opentab);
    }

    private boolean addGalleryImage(Uri uri) {
        String image_path = null;
        Cursor cursor = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
        cursor.moveToFirst();
        image_path = cursor.getString(0);
        cursor.close();

        String md5 = Utils.calculateMD5(image_path);

        if (new QueTable(this).isMd5(md5) || new SavedTable(this).isMd5(md5)) {
            return false;
        }
        QueTable qt = new QueTable(this);
        qt.updatedItem("Image", image_path, md5);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Toast.makeText(MainActivity.this, "Holder for about dialog!", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent("com.lithidsw.gur.UPLOAD_COMPLETE"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new UploadFrag();
                    break;
                case 1:
                    fragment = new SavedFrag();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }
}
