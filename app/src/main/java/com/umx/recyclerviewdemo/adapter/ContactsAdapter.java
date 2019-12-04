package com.umx.recyclerviewdemo.adapter;

import android.database.Cursor;

import com.github.promeg.pinyinhelper.Pinyin;
import com.umx.recyclerviewdemo.itemDecoration.StickyHeaderDecoration;

public class ContactsAdapter extends SimpleCursorAdapter implements StickyHeaderDecoration.Callback {
    private String mSortColumnName;

    public ContactsAdapter(Cursor cursor, int layoutId, String[] from, int[] to) {
        super(cursor, layoutId, from, to);
    }

    public void setSortColumnName(String name) {
        mSortColumnName = name;
    }

    @Override
    public boolean hasHeader(int adapterPosition) {
        boolean hasHeader = false;
        if (adapterPosition == 0) {
            hasHeader = true;
        } else if (adapterPosition > 0 && adapterPosition < getItemCount()) {
            if (!getFirstLetter(adapterPosition).equals(getFirstLetter(adapterPosition - 1))) {
                hasHeader = true;
            }
        }
        return hasHeader;
    }

    private String getFirstLetter(int adapterPosition) {
        mCursor.moveToPosition(adapterPosition);
        String name = mCursor.getString(mCursor.getColumnIndexOrThrow(mSortColumnName));
        char firstChar = name.charAt(0);
        String firstLetter;
        if (Pinyin.isChinese(firstChar)) {
            firstLetter = Pinyin.toPinyin(firstChar).substring(0, 1);
        } else {
            firstLetter = String.valueOf(firstChar);
        }
        return firstLetter;
    }

    @Override
    public String getHeaderText(int adapterPosition) {
        return getFirstLetter(adapterPosition);
    }
}
