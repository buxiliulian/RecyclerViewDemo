package com.umx.recyclerviewdemo.itemDecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration
{

    /**
     * 注意: 这个回调的两个函数都是使用Adapter position, 而不是layout position.
     */
    public interface Callback
    {
        boolean hasHeader(int adapterPosition);

        String getHeaderText(int adapterPosition);
    }

    private Callback mCallback;
    private Context mContext;
    private int mOrientation;
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;

    private int mHeaderHeight;

    // 用于绘制header文本
    private TextPaint mHeaderTextPaint;
    // 用于绘制header背景
    private Paint mHeaderBgPaint;

    private StickyHeaderDecoration(Builder builder)
    {
        mContext = builder.mContext;
        mOrientation = builder.mOrientation;
        if (mOrientation != VERTICAL && mOrientation != HORIZONTAL)
        {
            throw new IllegalArgumentException("StickyHeaderDecoration only support VERTICAL and HORIZONTAL orientation!");
        }
        mCallback = builder.mCallback;
        if (mCallback == null)
        {
            throw new IllegalArgumentException("StickyHeaderDecoration needs a non-null callback!");
        }

        mHeaderBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderBgPaint.setColor(builder.mBgColor);

        mHeaderTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mHeaderTextPaint.setColor(builder.mTextColor);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, builder.mTextSize,
                mContext.getResources().getDisplayMetrics());
        mHeaderTextPaint.setTextSize(textSize);
        Paint.FontMetrics fontMetrics = mHeaderTextPaint.getFontMetrics();
        mHeaderHeight = Math.round(fontMetrics.descent - fontMetrics.ascent);
    }


    /**
     * 测量子View时候调用，用于获取子View的ItemDecoration的Rect值，这些值放到第一个参数中。
     * <p>
     * 注意: 此时通过View获取ViewHolder是为null，以为还没有完成layout过程
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        if (mOrientation == VERTICAL)
        {
            int position = parent.getChildAdapterPosition(view);
            if (mCallback != null && mCallback.hasHeader(position))
            {
                outRect.set(0, mHeaderHeight, 0, 0);
            }
        } else
        {
            // TODO: 实现水平的逻辑
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        c.save();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null)
        {
            return;
        }
        if (mOrientation == VERTICAL)
        {
            Rect textBound = new Rect();
            // 遍历子View，绘制header
            for (int i = 0; i < parent.getChildCount(); i++)
            {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                // 如果有header就开始绘制
                if (mCallback.hasHeader(position))
                {
                    // 1. 绘制背景
                    c.drawRect(layoutManager.getDecoratedLeft(child),
                            layoutManager.getDecoratedTop(child),
                            layoutManager.getDecoratedRight(child),
                            layoutManager.getDecoratedTop(child) + mHeaderHeight,
                            mHeaderBgPaint);

                    // 获取header文本
                    String headerText = mCallback.getHeaderText(position);
                    // 获取header文本的Rect
                    mHeaderTextPaint.getTextBounds(headerText, 0, headerText.length(), textBound);
                    // 2. 绘制header文本
                    c.drawText(headerText,
                            child.getLeft() + child.getPaddingLeft(),
                            layoutManager.getDecoratedTop(child) + mHeaderHeight / 2.f + textBound.height() / 2.f,
                            mHeaderTextPaint);
                }
            }

        } else
        {
            // TODO: 实现水平逻辑
        }
        c.restore();
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        if (mOrientation == VERTICAL)
        {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager)
            {
                LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
                int firstVisibleItemPosition = llm.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition != RecyclerView.NO_POSITION)
                {
                    View firstView = llm.findViewByPosition(firstVisibleItemPosition);
                    int childAdapterPosition = parent.getChildAdapterPosition(firstView);
                    c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getWidth() - parent.getPaddingRight(),
                            parent.getPaddingTop() + mHeaderHeight, mHeaderBgPaint);
                    String headerText = mCallback.getHeaderText(childAdapterPosition);
                    Rect textBound = new Rect();
                    mHeaderTextPaint.getTextBounds(headerText, 0, headerText.length(), textBound);
                    c.drawText(headerText, firstView.getLeft() + firstView.getPaddingLeft(), parent.getPaddingTop() + mHeaderHeight / 2.f + textBound.height() / 2.f, mHeaderTextPaint);
                }
            }
        } else
        {
           // TODO: 实现垂直逻辑
        }
    }

    public static class Builder
    {
        private Context mContext;
        private int mOrientation;
        private Callback mCallback;
        private int mBgColor = Color.LTGRAY;
        private int mTextColor = Color.BLACK;
        // small: 14sp, medium: 18sp, Large:22sp
        private int mTextSize = 14; // sp

        public Builder(Context context, int orientation, Callback callback)
        {
            mContext = context;
            mOrientation = orientation;
            mCallback = callback;
        }

        public Builder setHeaderBgColor(int color)
        {
            mBgColor = color;
            return this;
        }

        public Builder setHeaderTextColor(int color)
        {
            mTextColor = color;
            return this;
        }

        public Builder setHeaderTextSize(int spSize)
        {
            mTextSize = spSize;
            return this;
        }

        public StickyHeaderDecoration create()
        {
            return new StickyHeaderDecoration(this);
        }
    }
}
