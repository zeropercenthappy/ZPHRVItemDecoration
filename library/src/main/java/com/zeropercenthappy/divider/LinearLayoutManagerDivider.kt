package com.zeropercenthappy.divider

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class LinearLayoutManagerDivider(
    @ColorInt dividerColor: Int,
    private val dividerWidth: Int
) : RecyclerView.ItemDecoration() {

    constructor(
        @ColorInt dividerColor: Int,
        dividerWidth: Int,
        fullWrap: Boolean
    ) : this(dividerColor, dividerWidth) {
        this.fullWrap = fullWrap
    }

    private val paint: Paint = Paint()
    private val headerViewList = arrayListOf<View>()
    private val footerViewList = arrayListOf<View>()
    private var fullWrap = true
    private var orientation: Int = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = dividerColor
    }

    override fun onDraw(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (fullWrap) {
            drawFullWrap(canvas, parent)
        } else {
            drawNotFullWrap(canvas, parent, state)
        }
    }

    /**
     * 每个item都画满宽度的线
     * 以item的offset来决定最终显示出来的分割线
     */
    private fun drawFullWrap(canvas: Canvas, parent: RecyclerView) {
        for (i in 0 until parent.childCount) {
            val childView = parent.getChildAt(i)
            // HeaderView和FooterView不处理
            if (isHeader(childView) || isFooter(childView)) {
                continue
            }
            // 绘制分割线
            if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                // 上边
                canvas.drawRect(
                    childView.left.toFloat(),
                    childView.top.toFloat() - dividerWidth,
                    childView.right.toFloat(),
                    childView.top.toFloat(), paint
                )
                // 下边
                canvas.drawRect(
                    childView.left.toFloat(),
                    childView.bottom.toFloat(),
                    childView.right.toFloat(),
                    childView.bottom.toFloat() + dividerWidth,
                    paint
                )
            } else {
                // 左边
                canvas.drawRect(
                    childView.left.toFloat() - dividerWidth,
                    childView.top.toFloat(),
                    childView.left.toFloat(),
                    childView.bottom.toFloat(),
                    paint
                )
                // 右边
                canvas.drawRect(
                    childView.right.toFloat(),
                    childView.top.toFloat(),
                    childView.right.toFloat() + dividerWidth,
                    childView.bottom.toFloat(),
                    paint
                )
            }
        }
    }

    /**
     * 根据item的偏移量绘制分割线
     */
    private fun drawNotFullWrap(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        for (i in 0 until parent.childCount) {
            val childView = parent.getChildAt(i)
            // HeaderView和FooterView不处理
            if (isHeader(childView) || isFooter(childView)) {
                continue
            }
            // 排除HeaderView和FooterView后，计算真实列表中的ChildView数量
            val realItemCount = state.itemCount - headerViewList.size - footerViewList.size
            // 找到当前ChildView在真实列表中的position（从1数起）
            val positionInLinear =
                parent.getChildLayoutPosition(childView) + 1 - headerViewList.size
            // 根据偏移量绘制分割线
            val offsetRect = getNotFullWrapOffsets(positionInLinear, realItemCount)
            if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                // 上边
                canvas.drawRect(
                    childView.left.toFloat(),
                    (childView.top - offsetRect.top).toFloat(),
                    childView.right.toFloat(),
                    childView.top.toFloat(),
                    paint
                )
                // 下边
                canvas.drawRect(
                    childView.left.toFloat(),
                    childView.bottom.toFloat(),
                    childView.right.toFloat(),
                    (childView.bottom + offsetRect.bottom).toFloat(),
                    paint
                )
            } else {
                // 左边
                canvas.drawRect(
                    (childView.left - offsetRect.left).toFloat(),
                    childView.top.toFloat(),
                    childView.left.toFloat(),
                    childView.bottom.toFloat(),
                    paint
                )
                // 右边
                canvas.drawRect(
                    childView.right.toFloat(),
                    childView.top.toFloat(),
                    (childView.right + offsetRect.right).toFloat(),
                    childView.bottom.toFloat(),
                    paint
                )
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager
        require(layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
            "LinearLayoutManagerDivider can only use with LinearLayoutManager"
        }
        orientation = layoutManager.orientation
        // HeaderView和FooterView不设置偏移量
        if (isHeader(view) || isFooter(view)) {
            outRect.set(0, 0, 0, 0)
            return
        }
        // 排除HeaderView和FooterView后，计算真实列表中的ChildView数量
        val realItemCount = state.itemCount - headerViewList.size - footerViewList.size
        // 找到当前ChildView在真实列表中的position（从1数起）
        val positionInLinear = parent.getChildLayoutPosition(view) + 1 - headerViewList.size
        // 计算偏移量
        if (fullWrap) {
            outRect.set(getFullWrapOffsets(positionInLinear, realItemCount))
        } else {
            outRect.set(getNotFullWrapOffsets(positionInLinear, realItemCount))
        }
        // 第一次第二行item的偏移量后，重新计算第一行的item的偏移量
        // 因为第一行的item在第一次计算时，是当作同时是第一行和最后一行计算的
        // 所以开始出现第二行时，要重新计算一次第一行
        if (realItemCount == 2) {
            parent.postDelayed({
                for (i in headerViewList.size + 0 until headerViewList.size + 1) {
                    parent.adapter?.notifyItemChanged(i)
                }
            }, 50)
        }
    }

    /**
     * 在真实列表中，全包裹的分割线，计算指定position（从1开始）在列表中应该设置的偏移量
     */
    private fun getFullWrapOffsets(position: Int, total: Int): Rect {
        val rect = Rect()
        var leftOffset = 0
        var topOffset = 0
        var rightOffset = 0
        var bottomOffset = 0
        // 上下偏移量
        if (total == 1) {
            if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                // 同时是第一行和最后一行，即只有一行
                topOffset = dividerWidth
                bottomOffset = dividerWidth
            } else {
                // 同时是第一列和最后一列，即只有一列
                leftOffset = dividerWidth
                rightOffset = dividerWidth
            }
        } else if (position == 1) {
            if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                // 第一行
                topOffset = dividerWidth
                bottomOffset = (1f / total * dividerWidth).roundToInt()
            } else {
                // 第一列
                leftOffset = dividerWidth
                rightOffset = (1f / total * dividerWidth).roundToInt()
            }

        } else if (position == total) {
            if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                // 最后一行
                topOffset = (1f / total * dividerWidth).roundToInt()
                bottomOffset = dividerWidth
            } else {
                // 最后一列
                leftOffset = (1f / total * dividerWidth).roundToInt()
                rightOffset = dividerWidth
            }
        } else {
            if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                // 中间行
                topOffset = ((total + 1f - position) / total * dividerWidth).roundToInt()
                bottomOffset = (position.toFloat() / total * dividerWidth).roundToInt()
            } else {
                // 中间列
                leftOffset = ((total + 1f - position) / total * dividerWidth).roundToInt()
                rightOffset = (position.toFloat() / total * dividerWidth).roundToInt()
            }
        }
        // 计算完毕
        rect.set(leftOffset, topOffset, rightOffset, bottomOffset)
        return rect
    }

    /**
     * 在真实列表中，非全包裹的分割线，计算指定position（从1开始）在列表中应该设置的偏移量
     */
    private fun getNotFullWrapOffsets(position: Int, total: Int): Rect {
        val rect = Rect()
        var leftOffset = 0
        var topOffset = 0
        var rightOffset = 0
        var bottomOffset = 0
        if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
            // 上下偏移量
            when {
                total == 1 -> {
                    // 同时是第一行和最后一行，即只有一行
                    topOffset = 0
                    bottomOffset = 0
                }
                position == 1 -> {
                    // 第一行
                    topOffset = 0
                    bottomOffset = ((total - 1f) / total * dividerWidth).roundToInt()
                }
                position == total -> {
                    // 最后一行
                    topOffset = ((total - 1f) / total * dividerWidth).roundToInt()
                    bottomOffset = 0
                }
                else -> {
                    // 中间行
                    topOffset = ((position - 1f) / total * dividerWidth).roundToInt()
                    bottomOffset =
                        (((total - 1f) - (position - 1f)) / total * dividerWidth).roundToInt()
                }
            }
        } else {
            // 左右偏移量
            when {
                total == 1 -> {
                    // 同时是第一列和最后一列，即只有一列
                    leftOffset = 0
                    rightOffset = 0
                }
                position == 1 -> {
                    // 第一列
                    leftOffset = 0
                    rightOffset = ((total - 1f) / total * dividerWidth).roundToInt()
                }
                position == total -> {
                    // 最后一列
                    leftOffset = ((total - 1f) / total * dividerWidth).roundToInt()
                    rightOffset = 0
                }
                else -> {
                    // 中间列
                    leftOffset = ((position - 1f) / total * dividerWidth).roundToInt()
                    rightOffset =
                        (((total - 1f) - (position - 1f)) / total * dividerWidth).roundToInt()
                }
            }
        }
        // 计算完毕
        rect.set(leftOffset, topOffset, rightOffset, bottomOffset)
        return rect
    }

    /**
     * 不会对布局造成影响，仅用于分割线计算
     */
    fun addHeaderView(headerView: View) {
        headerViewList.add(headerView)
    }

    /**
     * 不会对布局造成影响，仅用于分割线计算
     */
    fun removeHeaderView(headerView: View) {
        headerViewList.remove(headerView)
    }

    /**
     * 不会对布局造成影响，仅用于分割线计算
     */
    fun addFooterView(footerView: View) {
        footerViewList.add(footerView)
    }

    /**
     * 不会对布局造成影响，仅用于分割线计算
     */
    fun removeFooterView(footerView: View) {
        footerViewList.remove(footerView)
    }

    private fun isHeader(view: View): Boolean {
        if (headerViewList.contains(view)) {
            return true
        }
        // 兼容Brvah的addHeaderView方法设置的Header
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val chideView = view.getChildAt(0)
                if (headerViewList.contains(chideView)) {
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }

    private fun isFooter(view: View): Boolean {
        if (footerViewList.contains(view)) {
            return true
        }
        // 兼容Brvah的addFooterView方法设置的Footer
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val chideView = view.getChildAt(0)
                if (footerViewList.contains(chideView)) {
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }
}