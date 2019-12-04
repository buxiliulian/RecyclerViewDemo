package com.umx.recyclerviewdemo.adapter;

import android.database.Cursor;

import androidx.annotation.NonNull;

public abstract class CursorAdapter extends BaseAdapter {

    protected Cursor mCursor;

    public CursorAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (mCursor.moveToPosition(position)) {
            bindCursorViewHolder(holder, mCursor);
        }
    }

    protected abstract void bindCursorViewHolder(VH holder, Cursor cursor);

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        // TODO: 这个到底怎么用？
        super.setHasStableIds(hasStableIds);
    }

    /**
     * Note: 请使用Loader来管理Cursor
     */
    public void swapCursor(Cursor cursor) {
        if (mCursor != cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
    }

}
