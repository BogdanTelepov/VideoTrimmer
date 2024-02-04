package kg.dev.videoeditor.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kg.dev.videoeditor.R

class HorizontalCenteredDotItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val dotRadius = 2 // Радиус точки в пикселях
    private val dotColor = context.getColor(R.color.secondary_text)

    private val paint = Paint()

    init {
        paint.color = dotColor
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val nextChild = parent.getChildAt(i + 1)

            val centerX = (child.right + nextChild.left) / 2f
            val centerY = child.top + (child.height / 2f)

            c.drawCircle(centerX, centerY, dotRadius.toFloat(), paint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Пропустить последний элемент
        if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount?.minus(1)) {
            outRect.set(0, 0, 0, 0)
        } else {
            outRect.set(0, 0, dotRadius, 0)
        }
    }
}