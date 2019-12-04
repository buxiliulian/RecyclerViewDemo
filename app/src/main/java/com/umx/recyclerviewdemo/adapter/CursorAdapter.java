package com.umx.recyclerviewdemo.adapter;

import android.database.Cursor;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class CursorAdapter extends RecyclerView.Adapter<BaseViewHolder>
{

    protected Cursor mCursor;

    public CursorAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return BaseViewHolder.newVH(parent, getLayoutId(viewType));
    }

    protected abstract int getLayoutId(int viewType);

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (mCursor.moveToPosition(position)) {
            bindCursorViewHolder(holder, mCursor);
        }
    }

    protected abstract void bindCursorViewHolder(BaseViewHolder holder, Cursor cursor);

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
