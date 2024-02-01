package kg.dev.videoeditor.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kg.dev.videoeditor.R

class DotDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val dividerHeight = 4 // Высота разделителя в пикселях
    private val dividerColor = ContextCompat.getColor(context, R.color.white)

    private val paint = Paint()

    init {
        paint.color = dividerColor
        paint.strokeWidth = dividerHeight.toFloat()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val startX = child.right.toFloat() + params.rightMargin
            val startY = child.top.toFloat() + params.topMargin
            val stopX = startX
            val stopY = child.bottom.toFloat() - params.bottomMargin

            c.drawLine(startX, startY, stopX, stopY, paint)
        }
    }

    // Пропустить разделитель для последнего элемента
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position < state.itemCount - 1) {
            outRect.set(0, 0, dividerHeight, 0)
        } else {
            outRect.setEmpty()
        }
    }
}