package com.umx.recyclerviewdemo.itemDecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = StickyHeaderDecoration.class.getSimpleName();

    /**
     * 注意: 这个回调的两个函数都是使用Adapter position, 而不是layout position.
     */
    public interface Callback {
        boolean hasHeader(int adapterPosition);

        String getHeaderText(int adapterPosition);
    }

    private Callback mCallback;
    private int mOrientation;
    // TODO:　这个可以转换为LinearLayoutManager的几个参数，使用注释代替
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;

    private int mHeaderHeight;

    private TextPaint mHeaderTextPaint;
    private Paint mHeaderBgPaint;

    private StickyHeaderDecoration(Builder builder) {
        Context context = builder.mContext;
        mOrientation = builder.mOrientation;
        if (mOrientation != VERTICAL && mOrientation != HORIZONTAL) {
            throw new IllegalArgumentException("StickyHeaderDecoration only support VERTICAL and HORIZONTAL orientation!");
        }
        mCallback = builder.mCallback;
        if (mCallback == null) {
            throw new IllegalArgumentException("StickyHeaderDecoration needs a non-null callback!");
        }

        mHeaderBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderBgPaint.setColor(builder.mBgColor);

        mHeaderTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mHeaderTextPaint.setColor(builder.mTextColor);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, builder.mTextSize,
                context.getResources().getDisplayMetrics());
        mHeaderTextPaint.setTextSize(textSize);
        Paint.FontMetrics fontMetrics = mHeaderTextPaint.getFontMetrics();
        mHeaderHeight = Math.round(fontMetrics.descent - fontMetrics.ascent);
    }


    /**
     * 测量子View时候调用，用于获取子View的ItemDecoration的Rect值，这些值放到第一个参数中。
     * <p>
     * 注意: 此时通过View获取ViewHolder的值为null，因为还没有完成layout过程。
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            if (mCallback.hasHeader(parent.getChildAdapterPosition(view))) {
                outRect.set(0, mHeaderHeight, 0, 0);
            }
        } else {
            // TODO: 实现水平的逻辑
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        c.save();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null) {
            Log.d(TAG, "StickyHeaderDecoration needs a LayoutManager from RecyclerView!");
            return;
        }
        if (mOrientation == VERTICAL) {
            Rect textBound = new Rect();
            // 遍历子View，绘制header
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                if (mCallback.hasHeader(position)) {
                    // 绘制背景
                    c.drawRect(layoutManager.getDecoratedLeft(child),
                            layoutManager.getDecoratedTop(child),
                            layoutManager.getDecoratedRight(child),
                            layoutManager.getDecoratedTop(child) + mHeaderHeight,
                            mHeaderBgPaint);

                    // 绘制header文本
                    String headerText = mCallback.getHeaderText(position);
                    mHeaderTextPaint.getTextBounds(headerText, 0, headerText.length(), textBound);
                    c.drawText(headerText,
                            child.getLeft() + child.getPaddingLeft(),
                            layoutManager.getDecoratedTop(child) + mHeaderHeight / 2.f + textBound.height() / 2.f,
                            mHeaderTextPaint);
                }
            }

        } else {
            // TODO: 实现水平逻辑
        }
        c.restore();
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        c.save();
        if (mOrientation == VERTICAL) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (!(layoutManager instanceof LinearLayoutManager)) {
                Log.e(TAG, "StickyHeaderDecoration only support LinearLayoutManager!");
                return;
            }
            LinearLayoutManager llm = (LinearLayoutManager) layoutManager;

            // 获取第一个可见的子View的位置
            int firstVisibleItemPosition = llm.findFirstVisibleItemPosition();
            if (firstVisibleItemPosition == RecyclerView.NO_POSITION) {
                Log.e(TAG, "No visible child!");
                return;
            }

            // 获取第一个可见的子View
            View firstView = llm.findViewByPosition(firstVisibleItemPosition);
            if (firstView == null) {
                // This should not happen.
                Log.d(TAG, "No visible child found!");
                return;
            }

            // 获取子View的Adapter position
            int childAdapterPosition = parent.getChildAdapterPosition(firstView);
            if (childAdapterPosition == RecyclerView.NO_POSITION) {
                Log.d(TAG, "First view has no adapter position");
                return;
            }

            int decoratedBottom = llm.getDecoratedBottom(firstView);
            if (!mCallback.getHeaderText(childAdapterPosition).equals(mCallback.getHeaderText(childAdapterPosition + 1))) {
                if (decoratedBottom < mHeaderHeight) {
                    c.translate(0, decoratedBottom - mHeaderHeight);
                }
            }

            // 绘制header的背景
            c.drawRect(llm.getDecoratedLeft(firstView),
                    parent.getPaddingTop(),
                    llm.getDecoratedRight(firstView),
                    parent.getPaddingTop() + mHeaderHeight, mHeaderBgPaint);

            // 绘制header的文本
            String headerText = mCallback.getHeaderText(childAdapterPosition);
            Rect textBound = new Rect();
            mHeaderTextPaint.getTextBounds(headerText, 0, headerText.length(), textBound);
            c.drawText(headerText, firstView.getLeft() + firstView.getPaddingLeft(),
                    parent.getPaddingTop() + mHeaderHeight / 2.f + textBound.height() / 2.f, mHeaderTextPaint);
        } else {
            // TODO: 实现垂直逻辑
        }
        c.restore();
    }

    public static class Builder {
        private Context mContext;
        private int mOrientation;
        private Callback mCallback;
        private int mBgColor = Color.LTGRAY;
        private int mTextColor = Color.BLACK;
        // small: 14sp, medium: 18sp, Large:22sp
        private int mTextSize = 14; // sp

        public Builder(Context context, int orientation, Callback callback) {
            mContext = context;
            mOrientation = orientation;
            mCallback = callback;
        }

        public Builder setHeaderBgColor(int color) {
            mBgColor = color;
            return this;
        }

        public Builder setHeaderTextColor(int color) {
            mTextColor = color;
            return this;
        }

        public Builder setHeaderTextSize(int spSize) {
            mTextSize = spSize;
            return this;
        }

        public StickyHeaderDecoration create() {
            return new StickyHeaderDecoration(this);
        }
    }
}
