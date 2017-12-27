package com.zeropercenthappy.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ybq on 2017/4/21.
 * 仅适用于使用LinearLayoutManager的RecycleView绘制分割线
 */

public class NormalLLRVDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;
    private int dividerSize;
    private int dividerMargin;

    /**
     * @param context
     * @param dividerSize               分割线的宽或高，单位：px
     * @param dividerDrawableResourceId 分割线的资源文件id
     */
    public NormalLLRVDecoration(@NonNull Context context,
                                int dividerSize,
                                int dividerDrawableResourceId) {
        this.dividerSize = dividerSize;
        if (Build.VERSION.SDK_INT < 21) {
            this.divider = context.getResources().getDrawable(dividerDrawableResourceId);
        } else {
            this.divider = context.getResources()
                    .getDrawable(dividerDrawableResourceId, context.getTheme());
        }
    }

    /**
     * @param context
     * @param dividerSize               分割线的宽或高，单位：px
     * @param dividerMargin             分割线的margin
     * @param dividerDrawableResourceId 分割线的资源文件id
     */
    public NormalLLRVDecoration(@NonNull Context context,
                                int dividerSize,
                                int dividerMargin,
                                int dividerDrawableResourceId) {
        this.dividerSize = dividerSize;
        this.dividerMargin = dividerMargin;
        if (Build.VERSION.SDK_INT < 21) {
            this.divider = context.getResources().getDrawable(dividerDrawableResourceId);
        } else {
            this.divider = context.getResources()
                    .getDrawable(dividerDrawableResourceId, context.getTheme());
        }
    }

    /**
     * @param dividerSize     分割线的宽或高，单位：px
     * @param dividerDrawable 分割线drawable
     */
    public NormalLLRVDecoration(int dividerSize,
                                @NonNull Drawable dividerDrawable) {
        this.dividerSize = dividerSize;
        this.divider = dividerDrawable;
    }

    /**
     * @param dividerSize     分割线的宽或高，单位：px
     * @param dividerMargin   分割线的margin
     * @param dividerDrawable 分割线drawable
     */
    public NormalLLRVDecoration(int dividerSize,
                                int dividerMargin,
                                @NonNull Drawable dividerDrawable) {
        this.dividerSize = dividerSize;
        this.dividerMargin = dividerMargin;
        this.divider = dividerDrawable;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                drawHorizontalLine(canvas, parent, state);
            } else {
                drawVerticalLine(canvas, parent, state);
            }
        }
    }

    private void drawVerticalLine(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int top = parent.getPaddingTop() + dividerMargin;
        int bottom = parent.getHeight() - parent.getPaddingBottom() - dividerMargin;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + dividerSize;
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }

    private void drawHorizontalLine(Canvas canvas,
                                    RecyclerView parent,
                                    RecyclerView.State state) {
        int left = parent.getPaddingLeft() + dividerMargin;
        int right = parent.getWidth() - parent.getPaddingRight() - dividerMargin;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + dividerSize;
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildAdapterPosition(view);
        itemPosition += 1;

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (itemPosition == childCount) {
            return;
        }
        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                //draw hor line
                outRect.set(0, 0, 0, dividerSize);
            } else {
                //draw ver line
                outRect.set(0, 0, dividerSize, 0);
            }
        }
    }
}
