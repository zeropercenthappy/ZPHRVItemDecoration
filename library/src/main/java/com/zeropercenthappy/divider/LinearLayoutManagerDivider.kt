package com.zeropercenthappy.divider

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.annotation.ColorInt
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class LinearLayoutManagerDivider(@ColorInt dividerColor: Int,
                                 private val dividerWidth: Int) : RecyclerView.ItemDecoration() {

    constructor(@ColorInt dividerColor: Int,
                dividerWidth: Int,
                fullWrap: Boolean) : this(dividerColor, dividerWidth) {
        this.fullWrap = fullWrap
    }

    private val paint: Paint = Paint()
    private val headerViewList = arrayListOf<View>()
    private val footerViewList = arrayListOf<View>()
    private var fullWrap = true
    private var orientation: Int = LinearLayoutManager.VERTICAL

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

    private fun drawFullWrap(canvas: Canvas, parent: RecyclerView) {
        for (i in 0 until parent.childCount) {
            val childView = parent.getChildAt(i)
            // HeaderView和FooterView不处理
            if (isHeader(childView) || isFooter(childView)) {
                continue
            }
            // 绘制分割线
            if (orientation == LinearLayoutManager.VERTICAL) {
                // 上边
                canvas.drawRect(childView.left.toFloat(),
                        childView.top.toFloat() - dividerWidth,
                        childView.right.toFloat(),
                        childView.top.toFloat(), paint)
                // 下边
                canvas.drawRect(childView.left.toFloat(),
                        childView.bottom.toFloat(),
                        childView.right.toFloat(),
                        childView.bottom.toFloat() + dividerWidth,
                        paint)
            } else {
                // 左边
                canvas.drawRect(childView.left.toFloat() - dividerWidth,
                        childView.top.toFloat(),
                        childView.left.toFloat(),
                        childView.bottom.toFloat(),
                        paint)
                // 右边
                canvas.drawRect(childView.right.toFloat(),
                        childView.top.toFloat(),
                        childView.right.toFloat() + dividerWidth,
                        childView.bottom.toFloat(),
                        paint)
            }
        }
    }

    private fun drawNotFullWrap(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // 计算真实item数量
        val realItemCount = state.itemCount - headerViewList.size - footerViewList.size
        for (i in 0 until parent.childCount) {
            val childView = parent.getChildAt(i)
            // HeaderView和FooterView不处理
            if (isHeader(childView) || isFooter(childView)) {
                continue
            }
            // 找到当前ChildView在真实列表中的position（从1数起）
            val positionInLinear = parent.getChildLayoutPosition(childView) + 1 - headerViewList.size
            if (orientation == LinearLayoutManager.VERTICAL) {
                // 下边
                if (positionInLinear == realItemCount) {
                    // 最后一个不绘制下边
                } else {
                    canvas.drawRect(childView.left.toFloat(),
                            childView.bottom.toFloat(),
                            childView.right.toFloat(),
                            childView.bottom.toFloat() + dividerWidth,
                            paint)
                }
            } else {
                // 右边
                if (positionInLinear == realItemCount) {
                    // 最后一个不绘制右边
                } else {
                    canvas.drawRect(childView.right.toFloat(),
                            childView.top.toFloat(),
                            childView.right.toFloat() + dividerWidth,
                            childView.bottom.toFloat(),
                            paint)
                }
            }

        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager
        if (layoutManager !is LinearLayoutManager) {
            throw IllegalArgumentException("LinearLayoutManagerDivider can only use with LinearLayoutManager")
        } else {
            orientation = layoutManager.orientation
        }
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
            // 第一次第二行item的偏移量后，重新计算第一行的item的偏移量
            // 因为第一行的item在第一次计算时，是当作同时是第一行和最后一行计算的
            // 所以开始出现第二行时，要重新计算一次第一行
            if (realItemCount == 2) {
                parent.postDelayed({
                    for (i in headerViewList.size + 0 until headerViewList.size + 1) {
                        parent.adapter.notifyItemChanged(i)
                    }
                }, 50)
            }
        } else {
            outRect.set(getNotFullWrapOffsets())
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
            if (orientation == LinearLayoutManager.VERTICAL) {
                // 同时是第一行和最后一行，即只有一行
                topOffset = dividerWidth
                bottomOffset = dividerWidth
            } else {
                // 同时是第一列和最后一列，即只有一列
                leftOffset = dividerWidth
                rightOffset = dividerWidth
            }
        } else if (position == 1) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                // 第一行
                topOffset = dividerWidth
                bottomOffset = (1f / total * dividerWidth).toInt()
            } else {
                // 第一列
                leftOffset = dividerWidth
                rightOffset = (1f / total * dividerWidth).toInt()
            }

        } else if (position == total) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                // 最后一行
                topOffset = (1f / total * dividerWidth).toInt()
                bottomOffset = dividerWidth
            } else {
                // 最后一列
                leftOffset = (1f / total * dividerWidth).toInt()
                rightOffset = dividerWidth
            }
        } else {
            if (orientation == LinearLayoutManager.VERTICAL) {
                // 中间行
                topOffset = ((total + 1f - position) / total * dividerWidth).toInt()
                bottomOffset = (position.toFloat() / total * dividerWidth).toInt()
            } else {
                // 中间列
                leftOffset = ((total + 1f - position) / total * dividerWidth).toInt()
                rightOffset = (position.toFloat() / total * dividerWidth).toInt()
            }
        }
        // 计算完毕
        rect.set(leftOffset, topOffset, rightOffset, bottomOffset)
        return rect
    }

    /**
     * 在真实列表中，非全包裹的分割线，计算指定position（从1开始）在列表中应该设置的偏移量
     */
    private fun getNotFullWrapOffsets(): Rect {
        val rect = Rect()
        var leftOffset = 0
        var topOffset = 0
        var rightOffset = 0
        var bottomOffset = 0
        if (orientation == LinearLayoutManager.VERTICAL) {
            // 上下偏移量
            topOffset = 0
            bottomOffset = dividerWidth
        } else {
            // 左右偏移量
            leftOffset = 0
            rightOffset = dividerWidth
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