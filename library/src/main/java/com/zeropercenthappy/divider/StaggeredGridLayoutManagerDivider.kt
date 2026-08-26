package com.zeropercenthappy.divider

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.roundToInt

class StaggeredGridLayoutManagerDivider(
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

    override fun onDraw(
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
            drawDivider(canvas, childView, parent)
        }
    }

    /**
     * 绘制item四条边的分割线
     *
     * 偏移量大于0的边，分割线以满宽度绘制，而不是按偏移量本身的大小绘制
     * 因为瀑布流中相邻span的item在流动方向上往往不对齐，无法依赖两侧item的偏移量
     * 拼接出span方向上完整的分割线；而偏移量的比例分配保证了相邻span之间的间隙
     * 恒为一个分割线的size，所以满宽度的分割线恰好能完整覆盖该间隙，与相邻item
     * 是否存在、是否对齐无关
     * 偏移量为0的边不绘制分割线
     */
    private fun drawDivider(canvas: Canvas, childView: View, parent: RecyclerView) {
        val offsetRect = getOffsets(childView, parent)
        // 分割线的绘制边界：偏移量大于0的边延伸至满分割线size，否则不延伸
        val left = childView.left - if (offsetRect.left > 0) verticalDividerWidth else 0
        val top = childView.top - if (offsetRect.top > 0) horizontalDividerHeight else 0
        val right = childView.right + if (offsetRect.right > 0) verticalDividerWidth else 0
        val bottom = childView.bottom + if (offsetRect.bottom > 0) horizontalDividerHeight else 0
        // 左边
        canvas.drawRect(
            left.toFloat(),
            top.toFloat(),
            childView.left.toFloat(),
            bottom.toFloat(),
            paint
        )
        // 上边
        canvas.drawRect(
            left.toFloat(),
            top.toFloat(),
            right.toFloat(),
            childView.top.toFloat(),
            paint
        )
        // 右边
        canvas.drawRect(
            childView.right.toFloat(),
            top.toFloat(),
            right.toFloat(),
            bottom.toFloat(),
            paint
        )
        // 下边
        canvas.drawRect(
            left.toFloat(),
            childView.bottom.toFloat(),
            right.toFloat(),
            bottom.toFloat(),
            paint
        )
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager
        require(layoutManager is StaggeredGridLayoutManager) {
            "StaggeredGridLayoutManagerDivider can only use with StaggeredGridLayoutManager"
        }
        // HeaderView和FooterView不设置偏移量
        if (isHeader(view) || isFooter(view)) {
            outRect.set(0, 0, 0, 0)
            return
        }
        outRect.set(getOffsets(view, parent))
    }

    /**
     * 计算item的偏移量
     *
     * 瀑布流中同一span内相邻item紧密排布，分割线规则：
     * 1.span方向上（纵向时为左右方向，横向时为上下方向），每个item按spanIndex比例分配
     *   分割线，使相邻span之间的分割线总宽度为一个分割线的size；
     *   fullWrap时最外侧的span的外侧留出完整分割线，非fullWrap时为0；
     * 2.流动方向上（纵向时为上下方向，横向时为左右方向），同一span内相邻item间只保留
     *   一条分割线：fullWrap时每个item在流动方向的后侧留出分割线（包括最末尾的item），
     *   且每个span的第一个item在前侧也留出分割线；非fullWrap时每个item在流动方向的
     *   前侧留出分割线（每个span的第一个item除外），后侧不留出。
     * 这样两种模式下，同一span内相邻item之间的流动方向分割线总宽度均为一个分割线的
     * size，且非fullWrap时最外侧不绘制分割线。
     *
     * StaggeredGridLayoutManager会把item分配到已占用空间最小的span中，所以真实列表的
     * 前spanCount个item必然分别是各span的第一个item（假设item高度均大于0）。
     */
    private fun getOffsets(
        view: View,
        parent: RecyclerView
    ): Rect {
        val layoutManager = parent.layoutManager as StaggeredGridLayoutManager
        val spanCount = layoutManager.spanCount
        val layoutParams = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        val spanIndex = layoutParams.spanIndex
        val fullSpan = layoutParams.isFullSpan
        // 找到当前item在真实列表中的position（从1数起）
        val positionInStaggered =
            parent.getChildLayoutPosition(view) + 1 - headerViewList.size
        val isFirstInSpan = positionInStaggered in 1..spanCount

        val rect = Rect()
        var leftOffset = 0
        var topOffset = 0
        var rightOffset = 0
        var bottomOffset = 0
        if (layoutManager.orientation == StaggeredGridLayoutManager.VERTICAL) {
            // 纵向时，span为列，流动方向为上下
            // 左右偏移量（span方向），FullSpan的item占满所有span，不分配
            if (!fullSpan) {
                leftOffset = if (fullWrap) {
                    ((spanCount - spanIndex) / spanCount.toFloat() * verticalDividerWidth).roundToInt()
                } else {
                    (spanIndex / spanCount.toFloat() * verticalDividerWidth).roundToInt()
                }
                rightOffset = if (fullWrap) {
                    ((spanIndex + 1) / spanCount.toFloat() * verticalDividerWidth).roundToInt()
                } else {
                    ((spanCount - 1 - spanIndex) / spanCount.toFloat() * verticalDividerWidth).roundToInt()
                }
            }
            // 上下偏移量（流动方向）
            if (fullWrap) {
                topOffset = if (isFirstInSpan) horizontalDividerHeight else 0
                bottomOffset = horizontalDividerHeight
            } else {
                topOffset = if (isFirstInSpan) 0 else horizontalDividerHeight
                bottomOffset = 0
            }
        } else {
            // 横向时，span为行，流动方向为左右
            // 上下偏移量（span方向），FullSpan的item占满所有span，不分配
            if (!fullSpan) {
                topOffset = if (fullWrap) {
                    ((spanCount - spanIndex) / spanCount.toFloat() * horizontalDividerHeight).roundToInt()
                } else {
                    (spanIndex / spanCount.toFloat() * horizontalDividerHeight).roundToInt()
                }
                bottomOffset = if (fullWrap) {
                    ((spanIndex + 1) / spanCount.toFloat() * horizontalDividerHeight).roundToInt()
                } else {
                    ((spanCount - 1 - spanIndex) / spanCount.toFloat() * horizontalDividerHeight).roundToInt()
                }
            }
            // 左右偏移量（流动方向）
            if (fullWrap) {
                leftOffset = if (isFirstInSpan) verticalDividerWidth else 0
                rightOffset = verticalDividerWidth
            } else {
                leftOffset = if (isFirstInSpan) 0 else verticalDividerWidth
                rightOffset = 0
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
        // 兼容Brvah的addFooterView方法设置的Footer
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
