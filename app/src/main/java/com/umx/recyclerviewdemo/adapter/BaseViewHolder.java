package com.umx.recyclerviewdemo.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by David Chow on 2019-12-04.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder
{
    //　为了减少从缓存中获取ViewHolder后再绑定的压力
    private SparseArray<View> mViews;

    public static BaseViewHolder newVH(ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new BaseViewHolder(itemView);
    }

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    View getView(int id) {
        if (mViews == null) {
            mViews = new SparseArray<>();
        }
        View cachedView = mViews.get(id);
        if (cachedView == null) {
            cachedView = itemView.findViewById(id);
            mViews.put(id, cachedView);
        }
        return cachedView;
    }
}
