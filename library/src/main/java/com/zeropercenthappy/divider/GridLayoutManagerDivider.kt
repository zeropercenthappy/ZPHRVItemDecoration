package com.zeropercenthappy.divider

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.annotation.ColorInt
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlin.math.roundToInt

class GridLayoutManagerDivider(
    @ColorInt dividerColor: Int,
    private val horizontalDividerHeight: Int,
    private val verticalDividerWidth: Int,
    private val fullWrap: Boolean
) : RecyclerView.ItemDecoration() {


    constructor(
        @ColorInt dividerColor: Int,
        dividerSize: Int,
        fullWrap: Boolean
    ) : this(dividerColor, dividerSize, dividerSize, fullWrap)

    private val paint: Paint = Paint()
    private val headerViewList = arrayListOf<View>()
    private val footerViewList = arrayListOf<View>()

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = dividerColor
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
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
            // 左边
            canvas.drawRect(
                childView.left.toFloat() - verticalDividerWidth,
                childView.top.toFloat() - horizontalDividerHeight,
                childView.left.toFloat(),
                childView.bottom.toFloat() + horizontalDividerHeight,
                paint
            )
            // 上边
            canvas.drawRect(
                childView.left.toFloat() - verticalDividerWidth,
                childView.top.toFloat() - horizontalDividerHeight,
                childView.right.toFloat() + verticalDividerWidth,
                childView.top.toFloat(), paint
            )
            // 右边
            canvas.drawRect(
                childView.right.toFloat(),
                childView.top.toFloat() - horizontalDividerHeight,
                childView.right.toFloat() + verticalDividerWidth,
                childView.bottom.toFloat() + horizontalDividerHeight,
                paint
            )
            // 下边
            canvas.drawRect(
                childView.left.toFloat() - verticalDividerWidth,
                childView.bottom.toFloat(),
                childView.right.toFloat() + verticalDividerWidth,
                childView.bottom.toFloat() + horizontalDividerHeight,
                paint
            )
        }
    }

    private fun drawNotFullWrap(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val childView = parent.getChildAt(i)
            // HeaderView和FooterView不处理
            if (isHeader(childView) || isFooter(childView)) {
                continue
            }
            // 排除HeaderView和FooterView后，计算真实表格中的ChildView数量
            val realItemCount = state.itemCount - headerViewList.size - footerViewList.size
            val spanCount = getSpanCount(parent)
            // 找到当前ChildView在真实表格中的position（从1数起）
            val positionInGrid = parent.getChildLayoutPosition(childView) + 1 - headerViewList.size
            // 根据偏移量绘制分割线
            val offsetRect = getNotFullWrapOffSets(positionInGrid, spanCount, realItemCount)
            // 左边
            canvas.drawRect(
                (childView.left - offsetRect.left).toFloat(),
                (childView.top - offsetRect.top).toFloat(),
                childView.left.toFloat(),
                (childView.bottom + offsetRect.bottom).toFloat(),
                paint
            )
            // 右边
            // 分割线的右端绘制到从item的右侧+分割线宽度为止，以解决未排满spanCount时的分割线绘制不准确问题
            // 右边的分割线只在非最后一个item才绘制
            canvas.drawRect(
                childView.right.toFloat(),
                (childView.top - offsetRect.top).toFloat(),
                (childView.right + verticalDividerWidth).toFloat(),
                (childView.bottom + offsetRect.bottom).toFloat(),
                paint
            )
            // 上边
            // 分割线的右端绘制到从item的右侧+分割线宽度为止，以解决未排满spanCount时的分割线绘制不准确问题
            canvas.drawRect(
                (childView.left - offsetRect.left).toFloat(),
                (childView.top - offsetRect.top).toFloat(),
                (childView.right + horizontalDividerHeight).toFloat(),
                childView.top.toFloat(),
                paint
            )
            // 下边
            if (!isLastRow(positionInGrid, spanCount, realItemCount)) {
                // 下边的分割线只在非最后一行才绘制
                // 并且绘制满宽度分割线，以解决未排满spanCount时的分割线绘制不准确问题
                canvas.drawRect(
                    (childView.left - offsetRect.left).toFloat(),
                    childView.bottom.toFloat(),
                    (childView.right + offsetRect.right).toFloat(),
                    (childView.bottom + horizontalDividerHeight).toFloat(),
                    paint
                )
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager
        require(layoutManager is GridLayoutManager && layoutManager.orientation == GridLayoutManager.VERTICAL) {
            "GridLayoutManagerDivider can only use with vertical GridLayoutManager"
        }
        // HeaderView和FooterView不设置偏移量
        if (isHeader(view) || isFooter(view)) {
            outRect.set(0, 0, 0, 0)
            return
        }
        // 排除HeaderView和FooterView后，计算真实表格中的ChildView数量
        val realItemCount = state.itemCount - headerViewList.size - footerViewList.size
        val spanCount = getSpanCount(parent)

        // 找到当前ChildView在真实表格中的position（从1数起）
        val positionInGrid = parent.getChildLayoutPosition(view) + 1 - headerViewList.size
        // 计算偏移量
        if (fullWrap) {
            outRect.set(getFullWrapOffsets(positionInGrid, spanCount, realItemCount))
        } else {
            outRect.set(getNotFullWrapOffSets(positionInGrid, spanCount, realItemCount))
        }
        // 第一次计算真实表格第二行第一个item的偏移量后，重新计算第一行的item的偏移量
        // 因为第一行的item在第一次计算时，是当作同时是第一行和最后一行计算的
        // 所以开始出现第二行时，要重新计算一次第一行
        if (realItemCount == spanCount + 1) {
            parent.postDelayed({
                for (i in headerViewList.size + 0 until headerViewList.size + spanCount) {
                    parent.adapter?.notifyItemChanged(i)
                }
            }, 50)
        }
    }

    /**
     * 在真实表格中，全包裹的分割线，计算指定position（从1开始）在表格中应该设置的偏移量
     */
    private fun getFullWrapOffsets(position: Int, spanCount: Int, total: Int): Rect {
        val rect = Rect()
        val rowCount = getRowCount(spanCount, total)
        var leftOffset = 0
        var topOffset = 0
        var rightOffset = 0
        var bottomOffset = 0
        // 上下偏移量
        if (isFirstRow(position, spanCount) && isLastRow(position, spanCount, total)) {
            // 同时是第一行和最后一行，即只有一行
            topOffset = horizontalDividerHeight
            bottomOffset = horizontalDividerHeight
        } else if (isFirstRow(position, spanCount)) {
            // 第一行
            topOffset = horizontalDividerHeight
            bottomOffset = (1f / rowCount * horizontalDividerHeight).roundToInt()
        } else if (isLastRow(position, spanCount, total)) {
            // 最后一行
            topOffset = (1f / rowCount * horizontalDividerHeight).roundToInt()
            bottomOffset = horizontalDividerHeight
        } else {
            // 中间行
            val atRow = atRow(position, spanCount)
            topOffset = ((rowCount + 1f - atRow) / rowCount * horizontalDividerHeight).roundToInt()
            bottomOffset = (atRow.toFloat() / rowCount * horizontalDividerHeight).roundToInt()
        }
        // 左右偏移量
        if (isFirstColumn(position, spanCount) && isLastColumn(position, spanCount, total)) {
            // 同时是第一列和最后一列，即只有一列
            leftOffset = verticalDividerWidth
            rightOffset = verticalDividerWidth
        } else if (isFirstColumn(position, spanCount)) {
            // 第一列
            leftOffset = verticalDividerWidth
            rightOffset = (1f / spanCount * verticalDividerWidth).roundToInt()
        } else if (isLastColumn(position, spanCount, total, false)) {
            // 最后一列
            leftOffset = (1f / spanCount * verticalDividerWidth).roundToInt()
            rightOffset = verticalDividerWidth
        } else {
            // 中间列
            val atColumn = atColumn(position, spanCount)
            leftOffset = ((spanCount + 1f - atColumn) / spanCount * verticalDividerWidth).roundToInt()
            rightOffset = (atColumn.toFloat() / spanCount * verticalDividerWidth).roundToInt()
        }
        // 计算完毕
        rect.set(leftOffset, topOffset, rightOffset, bottomOffset)
        return rect
    }

    /**
     * 在真实表格中，非全包裹的分割线，计算指定position（从1开始）在表格中应该设置的偏移量
     */
    private fun getNotFullWrapOffSets(position: Int, spanCount: Int, total: Int): Rect {
        val rect = Rect()
        val rowCount = getRowCount(spanCount, total)

        var leftOffset = 0
        var topOffset = 0
        var rightOffset = 0
        var bottomOffset = 0

        // 上下偏移量
        if (isFirstRow(position, spanCount) && isLastRow(position, spanCount, total)) {
            // 同时是第一行和最后一行，即只有一行
            topOffset = 0
            bottomOffset = 0
        } else if (isFirstRow(position, spanCount)) {
            // 第一行
            topOffset = 0
            bottomOffset = ((rowCount - 1f) / rowCount * horizontalDividerHeight).roundToInt()
        } else if (isLastRow(position, spanCount, total)) {
            // 最后一行
            topOffset = ((rowCount - 1f) / rowCount * horizontalDividerHeight).roundToInt()
            bottomOffset = 0
        } else {
            // 中间行
            val atRow = atRow(position, spanCount)
            topOffset = ((atRow - 1f) / rowCount * horizontalDividerHeight).roundToInt()
            bottomOffset = (((rowCount - 1f) - (atRow - 1f)) / rowCount * horizontalDividerHeight).roundToInt()
        }
        // 左右偏移量
        if (isFirstColumn(position, spanCount) && isLastColumn(position, spanCount, total)) {
            // 同时是第一列和最后一列，即只有一列
            leftOffset = 0
            rightOffset = 0
        } else if (isFirstColumn(position, spanCount)) {
            // 第一列
            leftOffset = 0
            rightOffset = ((spanCount - 1f) / spanCount * verticalDividerWidth).roundToInt()
        } else if (isLastColumn(position, spanCount, total, false)) {
            // 最后一列
            leftOffset = ((spanCount - 1f) / spanCount * verticalDividerWidth).roundToInt()
            rightOffset = 0
        } else {
            // 中间列
            val atColumn = atColumn(position, spanCount)
            leftOffset = ((atColumn - 1f) / spanCount * verticalDividerWidth).roundToInt()
            rightOffset = (((spanCount - 1f) - (atColumn - 1f)) / spanCount * verticalDividerWidth).roundToInt()
        }

        // 计算完毕
        rect.set(leftOffset, topOffset, rightOffset, bottomOffset)
        return rect
    }

    /**
     * 计算真实表格的行数
     */
    private fun getRowCount(spanCount: Int, total: Int): Int {
        return when {
            total <= spanCount -> {
                // 小于一行
                1
            }
            total % spanCount != 0 -> {
                // 多于一行且未排满定义的spanCount
                total / spanCount + 1
            }
            else -> {
                // 多于一行且刚好排满定义的spanCount
                total / spanCount
            }

        }
    }

    /**
     * 在真实表格中，判断position（从1开始）是否在第一行
     */
    private fun isFirstRow(position: Int, spanCount: Int): Boolean {
        return position <= spanCount
    }

    /**
     * 在真实表格中，判断position（从1开始）是否在最后一行
     */
    private fun isLastRow(position: Int, spanCount: Int, total: Int): Boolean {
        return when {
            total <= spanCount -> {
                // 只有一行
                true
            }
            total % spanCount == 0 -> {
                // 多于一行
                // 总数刚好将列数排满
                position > (total / spanCount - 1) * spanCount
            }
            else -> {
                // 多于一行
                // 总数不够将列数排满
                // 利用int/int向下取整，得到排满的行数，再乘以spanCount，算出最后一行的条件
                position > (total / spanCount) * spanCount
            }

        }
    }

    /**
     * 在真实表格中，计算指定position（从1开始）在第几行
     */
    private fun atRow(position: Int, spanCount: Int): Int {
        return if (position % spanCount == 0) {
            position / spanCount
        } else {
            position / spanCount + 1
        }
    }

    /**
     * 在真实表格中，判断position（从1开始）是否在第一列
     */
    private fun isFirstColumn(position: Int, spanCount: Int): Boolean {
        return when (spanCount) {
            1 -> {
                // 只有一列
                true
            }
            else -> {
                position % spanCount == 1
            }
        }
    }

    /**
     * 在真实表格中，判断position（从1开始）是否在最后一列
     * @param isCountLastItem 是否将最后一个item不是最后一列来当作最后一列处理
     */
    private fun isLastColumn(position: Int, spanCount: Int, total: Int, isCountLastItem: Boolean = false): Boolean {
        return when {
            spanCount == 1 -> {
                // 只有一列
                true
            }
            position % spanCount == 0 -> {
                // 大于一列
                // position刚好处于定义最后一列上
                true
            }
            (position == total && isCountLastItem) -> {
                // 大于一列
                // position是最后一个item
                true
            }
            else -> {
                false
            }
        }
    }

    /**
     * 在真实表格中，判断position（从1开始）在第几列
     */
    private fun atColumn(position: Int, spanCount: Int): Int {
        return when {
            position % spanCount != 0 -> {
                // 非最后一列
                position % spanCount
            }
            else -> {
                // 最后一列
                spanCount
            }
        }
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        val spanCount: Int
        val layoutManager = parent.layoutManager
        spanCount = (layoutManager as GridLayoutManager).spanCount
        return spanCount
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
        // 兼容Brvah的addFooterView方法设置的Header
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