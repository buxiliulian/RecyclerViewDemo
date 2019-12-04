package com.umx.recyclerviewdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.umx.recyclerviewdemo.adapter.ContactsAdapter;
import com.umx.recyclerviewdemo.itemDecoration.StickyHeaderDecoration;

/**
 * TODO:
 * 1. 可以考虑把自定义的Adapter生成库
 * 2. 利用一个空Fragment实现权限请求的样板代码？甚至使用编译期代码生成来完成？
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String READ_CONTACTS_PERMISSION = Manifest.permission.READ_CONTACTS;
    private static final int LOADER_ID_READ_CONTACTS = 0x01;
    private static final int REQUEST_CODE_READ_CONTACTS = 0x02;
    private ContactsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();
        if (ContextCompat.checkSelfPermission(this, READ_CONTACTS_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS_PERMISSION)) {
                new AlertDialog.Builder(this)
                        .setMessage("需要权限读取联系人")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestReadContactPermission();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS_PERMISSION}, REQUEST_CODE_READ_CONTACTS);
            }
        } else {
            initContactLoader();
        }
    }

    private void requestReadContactPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS_PERMISSION}, REQUEST_CODE_READ_CONTACTS);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        mAdapter = new ContactsAdapter(null, android.R.layout.simple_list_item_1,
                new String[]{ContactsQuery.NAME}, new int[]{android.R.id.text1});
        mAdapter.setSortColumnName(ContactsQuery.SORT_KEY);
        recyclerView.setAdapter(mAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        StickyHeaderDecoration stickyHeaderDecoration = new StickyHeaderDecoration.Builder(this, llm.getOrientation(), mAdapter).create();
        recyclerView.addItemDecoration(stickyHeaderDecoration);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, ContactsQuery.URI, ContactsQuery.PROJECTION, null, null, ContactsQuery.SORT_KEY);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initContactLoader();
            }
        }
    }

    private void initContactLoader() {
        LoaderManager.getInstance(this).initLoader(LOADER_ID_READ_CONTACTS, null, this);
    }
}
