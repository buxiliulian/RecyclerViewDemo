package com.umx.recyclerviewdemo.adapter;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleCursorAdapter extends CursorAdapter {
    private int mLayoutId;
    private String[] mFrom;
    private int[] mTo;

    public SimpleCursorAdapter(Cursor cursor, int layoutId, String[] from, int[] to) {
        super(cursor);
        mLayoutId = layoutId;
        mFrom = from;
        mTo = to;
    }

    @Override
    protected void bindCursorViewHolder(BaseViewHolder holder, Cursor cursor) {
        for (int i = 0; i < mTo.length; i++) {
            View view = holder.getView(mTo[i]);
            String str = cursor.getString(cursor.getColumnIndexOrThrow(mFrom[i]));
            if (TextUtils.isEmpty(str)) {
                return;
            }
            if (view instanceof TextView) {
                ((TextView) view).setText(str);
            } else if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(Integer.parseInt(str));
            }
        }
    }

    @Override
    protected int getLayoutId(int viewType) {
        return mLayoutId;
    }

}
