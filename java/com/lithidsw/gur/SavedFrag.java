package com.lithidsw.gur;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lithidsw.gur.adapter.SavedAdapter;
import com.lithidsw.gur.database.SavedTable;

import java.util.ArrayList;

public class SavedFrag extends Fragment {

    private FragmentActivity fa;
    private SavedAdapter adapter;

    private TextView addImageTxt;
    private GridView mainGrid;

    ArrayList<String[]> imageItems = new ArrayList<String[]>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(
                R.layout.frag_saved, container, false);

        fa = getActivity();

        adapter = new SavedAdapter(fa, imageItems);
        mainGrid = (GridView) ll.findViewById(R.id.grid_view);
        mainGrid.setAdapter(adapter);
        mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClipboardManager clipboard = (ClipboardManager) fa.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(imageItems.get(i)[1], imageItems.get(i)[4]);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(fa, imageItems.get(i)[4]+" has been copied!", Toast.LENGTH_LONG).show();
            }
        });

        mainGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mainGrid.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.setTitle("Choose threads");
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.selection_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                final SparseBooleanArray checked = mainGrid.getCheckedItemPositions();
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        int size = checked.size();
                        for (int i = 0; i < size; i++) {
                            int key = checked.keyAt(i);
                            boolean value = checked.get(key);
                            if (value) {
                                new SavedTable(fa).deleteItem(imageItems.get(key)[0]);
                                imageItems.remove(key);
                                adapter.notifyDataSetChanged();
                                updateview(imageItems.size());
                            }
                        }
                        actionMode.finish();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        addImageTxt = (TextView) ll.findViewById(R.id.no_saved);

        new SavedLoader().execute();
        return ll;
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

    class SavedLoader extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {
            imageItems.clear();
            mainGrid.invalidateViews();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            ArrayList<String[]> list = new SavedTable(fa).getAllSaved();
            if (list.size() > 0) {
                for (int i=0; i < list.size(); i++) {
                    String[] item = new String[6];
                    item[0] = list.get(i)[0];
                    item[1] = list.get(i)[1];
                    item[2] = list.get(i)[2];
                    item[3] = list.get(i)[3];
                    item[4] = list.get(i)[4];
                    item[5] = list.get(i)[5];
                    imageItems.add(item);
                }
                return imageItems.size();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer count) {
            adapter.notifyDataSetChanged();
            updateview(count);
        }
    }
}