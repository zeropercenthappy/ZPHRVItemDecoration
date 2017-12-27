package com.zeropercenthappy.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ybq on 2017/4/21.
 */

public class FullVerGLRVDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;
    private int dividerSize;

    /**
     * @param context
     * @param dividerSize               分割线的宽或高，单位：px
     * @param dividerDrawableResourceId 分割线的资源文件id
     */
    public FullVerGLRVDecoration(@NonNull Context context,
                                 int dividerSize,
                                 int dividerDrawableResourceId) {
        this.dividerSize = dividerSize;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
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
    public FullVerGLRVDecoration(int dividerSize,
                                 @NonNull Drawable dividerDrawable) {
        this.dividerSize = dividerSize;
        this.divider = dividerDrawable;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            drawHorizontal(canvas, parent);
            drawVertical(canvas, parent);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //draw top
            final int topLeft = child.getLeft() - params.leftMargin - dividerSize;
            final int topRight = child.getRight() + params.rightMargin + dividerSize;
            final int topBottom = child.getTop() + params.topMargin;
            final int topTop = topBottom - dividerSize;
            divider.setBounds(topLeft, topTop, topRight, topBottom);
            divider.draw(canvas);
            //draw bottom
            final int bottomLeft = child.getLeft() - params.leftMargin - dividerSize;
            final int bottomRight = child.getRight() + params.rightMargin + dividerSize;
            final int bottomTop = child.getBottom() + params.bottomMargin;
            final int bottomBottom = bottomTop + dividerSize;
            divider.setBounds(bottomLeft, bottomTop, bottomRight, bottomBottom);
            divider.draw(canvas);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //draw right
            final int rightTop = child.getTop() - params.topMargin;
            final int rightBottom = child.getBottom() + params.bottomMargin + dividerSize;
            final int rightLeft = child.getRight() + params.rightMargin;
            final int rightRight = rightLeft + dividerSize;
            divider.setBounds(rightLeft, rightTop, rightRight, rightBottom);
            divider.draw(canvas);
            //draw left
            final int leftTop = child.getTop() - params.topMargin;
            final int leftBottom = child.getBottom() + params.bottomMargin + dividerSize;
            final int leftLeft = child.getLeft() - params.leftMargin - dividerSize;
            final int leftRight = leftLeft + dividerSize;
            divider.setBounds(leftLeft, leftTop, leftRight, leftBottom);
            divider.draw(canvas);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        int childCount = parent.getAdapter().getItemCount();
        int spanCount = getSpanCount(parent);
        int rowCount = getRowCount(parent);

        int itemPosition = parent.getChildAdapterPosition(view);
        int leftOffset = 0;
        int topOffset = 0;
        int rightOffset = 0;
        int bottomOffset = 0;

        if (childCount <= spanCount) {
            spanCount = childCount;
        }

        //竖线
        if (childCount == 1) {
            //只有一个
            leftOffset = dividerSize;
            rightOffset = dividerSize;
        } else {
            //多于一个
            if ((itemPosition) % spanCount == 0) {
                //第一列
                leftOffset = dividerSize;
                rightOffset = (int) (dividerSize * (1d / spanCount));
            } else if ((itemPosition + 1) % spanCount == 0) {
                //最后一列
                leftOffset = (int) (dividerSize * (1d / spanCount));
                rightOffset = dividerSize;
            } else {
                leftOffset = (int) (dividerSize * ((spanCount - ((itemPosition + 1d) % spanCount - 1))) / spanCount);
                rightOffset = (int) (dividerSize * ((itemPosition + 1d) % spanCount / spanCount));
            }
        }

        //横线
        if (childCount == 1) {
            //只有一个
            topOffset = dividerSize;
            bottomOffset = dividerSize;
        } else {
            //多于一个
            if ((itemPosition + 1) <= spanCount) {
                //第一行
                topOffset = dividerSize;
                bottomOffset = (int) (dividerSize * (1d / rowCount));
            } else if ((itemPosition + 1) > (rowCount - 1) * spanCount) {
                //最后一行
                topOffset = (int) (dividerSize * (1d / rowCount));
                bottomOffset = dividerSize;
            } else {
                int row = ((itemPosition + 1) / spanCount) + ((itemPosition + 1) % spanCount == 0 ? 0 : 1);
                topOffset = (int) (dividerSize * ((rowCount - row + 1d) / rowCount));
                bottomOffset = (int) (dividerSize * (((double) row) / rowCount));
            }
        }

        outRect.set(leftOffset, topOffset, rightOffset, bottomOffset);
//        Log.i("decoration", "position = " + itemPosition +
//                "\nleft = " + leftOffset +
//                "\ntop = " + topOffset +
//                "\nright = " + rightOffset +
//                "\nbottom = " + bottomOffset);
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        return spanCount;
    }

    private int getRowCount(RecyclerView parent) {
        int rowCount;
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        rowCount = (childCount / spanCount) + (childCount % spanCount == 0 ? 0 : 1);
        return rowCount;
    }
}
