package com.zeropercenthappy.decorationlibrary;

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

public class NormalVerGLRVDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;
    private int dividerSize;

    /**
     * @param context
     * @param dividerSize               分割线的宽或高，单位：px
     * @param dividerDrawableResourceId 分割线的资源文件id
     */
    public NormalVerGLRVDecoration(@NonNull Context context,
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
     * @param dividerSize     分割线的宽或高，单位：px
     * @param dividerDrawable 分割线drawable
     */
    public NormalVerGLRVDecoration(int dividerSize,
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
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            //draw bottom
            int bottomLeft = child.getLeft() - params.leftMargin;
            int bottomRight = child.getRight() + params.rightMargin;
            int bottomRightWithDivider = child.getRight() + params.rightMargin + dividerSize;
            int bottomTop = child.getBottom() + params.bottomMargin;
            int bottomBottom = bottomTop + dividerSize;

            if (!DecorationUtils.isLastRow(i + 1, spanCount, childCount)) {
                if (DecorationUtils.isLastColumn(i + 1, spanCount, childCount)) {
                    divider.setBounds(bottomLeft, bottomTop, bottomRight, bottomBottom);
                    divider.draw(canvas);
                } else {
                    divider.setBounds(bottomLeft, bottomTop, bottomRightWithDivider, bottomBottom);
                    divider.draw(canvas);
                }
            }
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //draw right
            int rightTop = child.getTop() - params.topMargin;
            int rightBottom = child.getBottom() + params.bottomMargin;
            int rightLeft = child.getRight() + params.rightMargin;
            int rightRight = rightLeft + dividerSize;

            if (!DecorationUtils.isLastColumn(i + 1, spanCount, childCount)) {
                divider.setBounds(rightLeft, rightTop, rightRight, rightBottom);
                divider.draw(canvas);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        int childCount = parent.getAdapter().getItemCount();
        int spanCount = getSpanCount(parent);
        int itemPosition = parent.getChildAdapterPosition(view);

        int leftOffset = 0;
        int rightOffset = 0;
        int bottomOffset = dividerSize;

        if (childCount < spanCount) {
            spanCount = childCount;
        }

        //竖线
        if (childCount == 1) {
            //只有一个
            leftOffset = 0;
            rightOffset = 0;
        } else {
            //多于一个
            if ((itemPosition) % spanCount == 0) {
                //第一个item
                leftOffset = 0;
                rightOffset = (int) (dividerSize * ((spanCount - 1d) / spanCount));
            } else if ((itemPosition + 1) % spanCount == 0) {
                //最后一个item
                leftOffset = (int) (dividerSize * ((spanCount - 1d) / spanCount));
                rightOffset = 0;
            } else {
                leftOffset = (int) (dividerSize * (((itemPosition + 1) % spanCount - 1d) / spanCount));
                rightOffset = (int) (dividerSize * ((spanCount - (double) (itemPosition + 1) % spanCount) / spanCount));
            }
        }

        //横线
        if (childCount == 1 || childCount <= spanCount) {
            //只有一个或一行
            bottomOffset = 0;
        } else {
            //多于一行
            if ((childCount % spanCount == 0) && (itemPosition + 1) > childCount - spanCount) {
                bottomOffset = 0;
            } else if ((childCount % spanCount != 0) && ((itemPosition + 1) > childCount - childCount % spanCount)) {
                bottomOffset = 0;
            }
        }

        outRect.set(leftOffset, 0, rightOffset, bottomOffset);
//        Log.i("decoration", "position = " + itemPosition +
//                "\nleft = " + leftOffset +
//                "\nright = " + rightOffset +
//                "\nbottom = " + bottomOffset);
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }
}
