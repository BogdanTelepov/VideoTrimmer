package com.videotrimmer

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class DividerItemDecorator(private val divider: Drawable?) : RecyclerView.ItemDecoration() {
    private val mBounds: Rect = Rect()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child: View = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom: Int = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - divider?.intrinsicHeight!!
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
            outRect.setEmpty()
        } else outRect.set(0, 0, 0, divider?.intrinsicHeight!!)
    }
}

class SpaceDecorator(
    private val recyclerView: RecyclerView,
    private var horizontalSpace: Int = 0,
    private var verticalSpace: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = recyclerView.layoutManager as RecyclerView.LayoutManager

        if (layoutManager is GridLayoutManager) {
            if (layoutManager.orientation == GridLayoutManager.HORIZONTAL) {

                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                    outRect.right = horizontalSpace
                    outRect.left = horizontalSpace
                } else {
                    outRect.right = 0
                    outRect.left = horizontalSpace
                }
                if (parent.getChildAdapterPosition(view) % layoutManager.spanCount == 0) {
                    outRect.bottom = horizontalSpace
                    outRect.left = horizontalSpace
                }
            }
            if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
                outRect.left = horizontalSpace
                outRect.right = horizontalSpace
                outRect.bottom = verticalSpace
            }
        } else if (layoutManager is LinearLayoutManager) {
            if (layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                    outRect.right = horizontalSpace
                    outRect.left = horizontalSpace
                } else {
                    outRect.left = horizontalSpace
                    outRect.right = 0
                }
                outRect.top = verticalSpace
                outRect.bottom = verticalSpace
            } else {
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                    outRect.bottom = verticalSpace
                    outRect.top = verticalSpace
                } else {
                    outRect.top = verticalSpace
                    outRect.bottom = 0
                }
                outRect.left = horizontalSpace
                outRect.right = horizontalSpace
            }
        }
    }
}

fun RecyclerView.addSpaceDecorator(
    verticalSpace: Int = 0,
    horizontalSpace: Int = 0
) {
    addItemDecoration(
        SpaceDecorator(
            recyclerView = this,
            horizontalSpace = horizontalSpace,
            verticalSpace = verticalSpace
        )
    )
}