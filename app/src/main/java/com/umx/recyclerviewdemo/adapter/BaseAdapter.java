package com.umx.recyclerviewdemo.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.VH> {

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return VH.newVH(parent, getLayoutId(viewType));
    }

    protected abstract int getLayoutId(int viewType);


    static class VH extends RecyclerView.ViewHolder {
        //　为了减少从缓存中获取ViewHolder后再绑定的压力
        private SparseArray<View> mViews;

        static VH newVH(ViewGroup parent, int layoutId) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new VH(itemView);
        }

        VH(@NonNull View itemView) {
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
}
