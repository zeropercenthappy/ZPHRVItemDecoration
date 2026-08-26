package com.zeropercenthappy.divider

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
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
    private var orientation: Int = androidx.recyclerview.widget.GridLayoutManager.VERTICAL

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
            if (orientation == androidx.recyclerview.widget.GridLayoutManager.VERTICAL) {
                // 纵向时，行为排布方向，列满spanCount
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
                if (!isLastGroup(positionInGrid, spanCount, realItemCount)) {
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
            } else {
                // 横向时，列为排布方向，行满spanCount
                // 左边
                // 分割线的下端绘制到从item的下侧+分割线宽度为止，以解决未排满spanCount时的分割线绘制不准确问题
                canvas.drawRect(
                    (childView.left - offsetRect.left).toFloat(),
                    (childView.top - offsetRect.top).toFloat(),
                    childView.left.toFloat(),
                    (childView.bottom + verticalDividerWidth).toFloat(),
                    paint
                )
                // 右边
                // 右边的分割线只在非最后一列才绘制
                if (!isLastGroup(positionInGrid, spanCount, realItemCount)) {
                    canvas.drawRect(
                        childView.right.toFloat(),
                        (childView.top - offsetRect.top).toFloat(),
                        (childView.right + verticalDividerWidth).toFloat(),
                        (childView.bottom + offsetRect.bottom).toFloat(),
                        paint
                    )
                }
                // 上边
                canvas.drawRect(
                    (childView.left - offsetRect.left).toFloat(),
                    (childView.top - offsetRect.top).toFloat(),
                    (childView.right + offsetRect.right).toFloat(),
                    childView.top.toFloat(),
                    paint
                )
                // 下边
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

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager
        require(layoutManager is androidx.recyclerview.widget.GridLayoutManager) {
            "GridLayoutManagerDivider can only use with GridLayoutManager"
        }
        orientation = layoutManager.orientation
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
        // 第一次计算真实表格第二组第一个item（纵向为第二行第一个item，横向为第二列第一个item）的偏移量后，重新计算第一组item的偏移量
        // 因为第一组的item在第一次计算时，是当作同时是第一组和最后一组计算的
        // 所以开始出现第二组时，要重新计算一次第一组
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
        val groupCount = getGroupCount(spanCount, total)
        var leftOffset = 0
        var topOffset = 0
        var rightOffset = 0
        var bottomOffset = 0
        if (orientation == androidx.recyclerview.widget.GridLayoutManager.VERTICAL) {
            // 纵向时，组为行，组内序号为列
            // 上下偏移量
            if (isFirstGroup(position, spanCount) && isLastGroup(position, spanCount, total)) {
                // 同时是第一行和最后一行，即只有一行
                topOffset = horizontalDividerHeight
                bottomOffset = horizontalDividerHeight
            } else if (isFirstGroup(position, spanCount)) {
                // 第一行
                topOffset = horizontalDividerHeight
                bottomOffset = (1f / groupCount * horizontalDividerHeight).roundToInt()
            } else if (isLastGroup(position, spanCount, total)) {
                // 最后一行
                topOffset = (1f / groupCount * horizontalDividerHeight).roundToInt()
                bottomOffset = horizontalDividerHeight
            } else {
                // 中间行
                val atRow = atGroup(position, spanCount)
                topOffset =
                    ((groupCount + 1f - atRow) / groupCount * horizontalDividerHeight).roundToInt()
                bottomOffset = (atRow.toFloat() / groupCount * horizontalDividerHeight).roundToInt()
            }
            // 左右偏移量
            if (isFirstInGroup(position, spanCount) && isLastInGroup(position, spanCount, total)) {
                // 同时是第一列和最后一列，即只有一列
                leftOffset = verticalDividerWidth
                rightOffset = verticalDividerWidth
            } else if (isFirstInGroup(position, spanCount)) {
                // 第一列
                leftOffset = verticalDividerWidth
                rightOffset = (1f / spanCount * verticalDividerWidth).roundToInt()
            } else if (isLastInGroup(position, spanCount, total, false)) {
                // 最后一列
                leftOffset = (1f / spanCount * verticalDividerWidth).roundToInt()
                rightOffset = verticalDividerWidth
            } else {
                // 中间列
                val atColumn = atInGroup(position, spanCount)
                leftOffset =
                    ((spanCount + 1f - atColumn) / spanCount * verticalDividerWidth).roundToInt()
                rightOffset = (atColumn.toFloat() / spanCount * verticalDividerWidth).roundToInt()
            }
        } else {
            // 横向时，组为列，组内序号为行
            // 左右偏移量
            if (isFirstGroup(position, spanCount) && isLastGroup(position, spanCount, total)) {
                // 同时是第一列和最后一列，即只有一列
                leftOffset = verticalDividerWidth
                rightOffset = verticalDividerWidth
            } else if (isFirstGroup(position, spanCount)) {
                // 第一列
                leftOffset = verticalDividerWidth
                rightOffset = (1f / groupCount * verticalDividerWidth).roundToInt()
            } else if (isLastGroup(position, spanCount, total)) {
                // 最后一列
                leftOffset = (1f / groupCount * verticalDividerWidth).roundToInt()
                rightOffset = verticalDividerWidth
            } else {
                // 中间列
                val atColumn = atGroup(position, spanCount)
                leftOffset =
                    ((groupCount + 1f - atColumn) / groupCount * verticalDividerWidth).roundToInt()
                rightOffset = (atColumn.toFloat() / groupCount * verticalDividerWidth).roundToInt()
            }
            // 上下偏移量
            if (isFirstInGroup(position, spanCount) && isLastInGroup(position, spanCount, total)) {
                // 同时是第一行和最后一行，即只有一行
                topOffset = horizontalDividerHeight
                bottomOffset = horizontalDividerHeight
            } else if (isFirstInGroup(position, spanCount)) {
                // 第一行
                topOffset = horizontalDividerHeight
                bottomOffset = (1f / spanCount * horizontalDividerHeight).roundToInt()
            } else if (isLastInGroup(position, spanCount, total, false)) {
                // 最后一行
                topOffset = (1f / spanCount * horizontalDividerHeight).roundToInt()
                bottomOffset = horizontalDividerHeight
            } else {
                // 中间行
                val atRow = atInGroup(position, spanCount)
                topOffset =
                    ((spanCount + 1f - atRow) / spanCount * horizontalDividerHeight).roundToInt()
                bottomOffset = (atRow.toFloat() / spanCount * horizontalDividerHeight).roundToInt()
            }
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
        val groupCount = getGroupCount(spanCount, total)

        var leftOffset = 0
        var topOffset = 0
        var rightOffset = 0
        var bottomOffset = 0

        if (orientation == androidx.recyclerview.widget.GridLayoutManager.VERTICAL) {
            // 纵向时，组为行，组内序号为列
            // 上下偏移量
            if (isFirstGroup(position, spanCount) && isLastGroup(position, spanCount, total)) {
                // 同时是第一行和最后一行，即只有一行
                topOffset = 0
                bottomOffset = 0
            } else if (isFirstGroup(position, spanCount)) {
                // 第一行
                topOffset = 0
                bottomOffset = ((groupCount - 1f) / groupCount * horizontalDividerHeight).roundToInt()
            } else if (isLastGroup(position, spanCount, total)) {
                // 最后一行
                topOffset = ((groupCount - 1f) / groupCount * horizontalDividerHeight).roundToInt()
                bottomOffset = 0
            } else {
                // 中间行
                val atRow = atGroup(position, spanCount)
                topOffset = ((atRow - 1f) / groupCount * horizontalDividerHeight).roundToInt()
                bottomOffset =
                    (((groupCount - 1f) - (atRow - 1f)) / groupCount * horizontalDividerHeight).roundToInt()
            }
            // 左右偏移量
            if (isFirstInGroup(position, spanCount) && isLastInGroup(position, spanCount, total)) {
                // 同时是第一列和最后一列，即只有一列
                leftOffset = 0
                rightOffset = 0
            } else if (isFirstInGroup(position, spanCount)) {
                // 第一列
                leftOffset = 0
                rightOffset = ((spanCount - 1f) / spanCount * verticalDividerWidth).roundToInt()
            } else if (isLastInGroup(position, spanCount, total, false)) {
                // 最后一列
                leftOffset = ((spanCount - 1f) / spanCount * verticalDividerWidth).roundToInt()
                rightOffset = 0
            } else {
                // 中间列
                val atColumn = atInGroup(position, spanCount)
                leftOffset = ((atColumn - 1f) / spanCount * verticalDividerWidth).roundToInt()
                rightOffset =
                    (((spanCount - 1f) - (atColumn - 1f)) / spanCount * verticalDividerWidth).roundToInt()
            }
        } else {
            // 横向时，组为列，组内序号为行
            // 左右偏移量
            if (isFirstGroup(position, spanCount) && isLastGroup(position, spanCount, total)) {
                // 同时是第一列和最后一列，即只有一列
                leftOffset = 0
                rightOffset = 0
            } else if (isFirstGroup(position, spanCount)) {
                // 第一列
                leftOffset = 0
                rightOffset = ((groupCount - 1f) / groupCount * verticalDividerWidth).roundToInt()
            } else if (isLastGroup(position, spanCount, total)) {
                // 最后一列
                leftOffset = ((groupCount - 1f) / groupCount * verticalDividerWidth).roundToInt()
                rightOffset = 0
            } else {
                // 中间列
                val atColumn = atGroup(position, spanCount)
                leftOffset = ((atColumn - 1f) / groupCount * verticalDividerWidth).roundToInt()
                rightOffset =
                    (((groupCount - 1f) - (atColumn - 1f)) / groupCount * verticalDividerWidth).roundToInt()
            }
            // 上下偏移量
            if (isFirstInGroup(position, spanCount) && isLastInGroup(position, spanCount, total)) {
                // 同时是第一行和最后一行，即只有一行
                topOffset = 0
                bottomOffset = 0
            } else if (isFirstInGroup(position, spanCount)) {
                // 第一行
                topOffset = 0
                bottomOffset = ((spanCount - 1f) / spanCount * horizontalDividerHeight).roundToInt()
            } else if (isLastInGroup(position, spanCount, total, false)) {
                // 最后一行
                topOffset = ((spanCount - 1f) / spanCount * horizontalDividerHeight).roundToInt()
                bottomOffset = 0
            } else {
                // 中间行
                val atRow = atInGroup(position, spanCount)
                topOffset = ((atRow - 1f) / spanCount * horizontalDividerHeight).roundToInt()
                bottomOffset =
                    (((spanCount - 1f) - (atRow - 1f)) / spanCount * horizontalDividerHeight).roundToInt()
            }
        }

        // 计算完毕
        rect.set(leftOffset, topOffset, rightOffset, bottomOffset)
        return rect
    }

    /**
     * 计算真实表格的分组数（纵向时为行数，横向时为列数）
     */
    private fun getGroupCount(spanCount: Int, total: Int): Int {
        return when {
            total <= spanCount -> {
                // 不足一个分组
                1
            }
            total % spanCount != 0 -> {
                // 多于一个分组且未排满定义的spanCount
                total / spanCount + 1
            }
            else -> {
                // 多于一个分组且刚好排满定义的spanCount
                total / spanCount
            }

        }
    }

    /**
     * 在真实表格中，判断position（从1开始）是否在第一个分组（纵向时为第一行，横向时为第一列）
     */
    private fun isFirstGroup(position: Int, spanCount: Int): Boolean {
        return position <= spanCount
    }

    /**
     * 在真实表格中，判断position（从1开始）是否在最后一个分组（纵向时为最后一行，横向时为最后一列）
     */
    private fun isLastGroup(position: Int, spanCount: Int, total: Int): Boolean {
        return when {
            total <= spanCount -> {
                // 只有一个分组
                true
            }
            total % spanCount == 0 -> {
                // 多于一个分组
                // 总数刚好将每个分组排满
                position > (total / spanCount - 1) * spanCount
            }
            else -> {
                // 多于一个分组
                // 总数不够将最后一个分组排满
                // 利用int/int向下取整，得到排满的分组数，再乘以spanCount，算出最后一组的条件
                position > (total / spanCount) * spanCount
            }

        }
    }

    /**
     * 在真实表格中，计算指定position（从1开始）在第几个分组（纵向时为行号，横向时为列号）
     */
    private fun atGroup(position: Int, spanCount: Int): Int {
        return if (position % spanCount == 0) {
            position / spanCount
        } else {
            position / spanCount + 1
        }
    }

    /**
     * 在真实表格中，判断position（从1开始）是否为分组内的第一个（纵向时为第一列，横向时为第一行）
     */
    private fun isFirstInGroup(position: Int, spanCount: Int): Boolean {
        return when (spanCount) {
            1 -> {
                // 分组内只有一个位置
                true
            }
            else -> {
                position % spanCount == 1
            }
        }
    }

    /**
     * 在真实表格中，判断position（从1开始）是否为分组内的最后一个（纵向时为最后一列，横向时为最后一行）
     * @param isCountLastItem 是否将最后一个item当作分组内最后一个来处理
     */
    private fun isLastInGroup(
        position: Int,
        spanCount: Int,
        total: Int,
        isCountLastItem: Boolean = false
    ): Boolean {
        return when {
            spanCount == 1 -> {
                // 分组内只有一个位置
                true
            }
            position % spanCount == 0 -> {
                // position刚好处于分组内最后一个位置上
                true
            }
            (position == total && isCountLastItem) -> {
                // position是最后一个item
                true
            }
            else -> {
                false
            }
        }
    }

    /**
     * 在真实表格中，计算指定position（从1开始）为分组内第几个（纵向时为列号，横向时为行号）
     */
    private fun atInGroup(position: Int, spanCount: Int): Int {
        return when {
            position % spanCount != 0 -> {
                // 非分组内最后一个
                position % spanCount
            }
            else -> {
                // 分组内最后一个
                spanCount
            }
        }
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        val spanCount: Int
        val layoutManager = parent.layoutManager
        spanCount = (layoutManager as androidx.recyclerview.widget.GridLayoutManager).spanCount
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
                val chideView = view.getChildAt(i)
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
                val chideView = view.getChildAt(i)
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
