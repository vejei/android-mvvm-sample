package com.example.uikit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DividerDecoration(private val context: Context?) : RecyclerView.ItemDecoration() {
    private var divider: Drawable? = null

    init {
        val typedArray = context?.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        divider = typedArray?.getDrawable(0)
        typedArray?.recycle()
    }

    fun setDrawable(drawable: Drawable?) {
        if (drawable == null) {
            throw IllegalArgumentException("Drawable cannot be null.")
        }
        divider = drawable
    }

    fun getDrawable(): Drawable? {
        return divider
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount - 1 // ignore last item
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + layoutParams.bottomMargin
            val intrinsicHeight = divider?.intrinsicHeight ?: 0
            val bottom = top + intrinsicHeight

            divider?.setBounds(left, top, right, bottom)
            divider?.draw(c)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // ignore last item
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            return
        }

        if (divider == null) {
            outRect.set(0, 0, 0, 0)
            return
        }
        outRect.set(0, 0, 0, divider?.intrinsicHeight ?: 0)
    }
}

class GapItemDecoration(private val context: Context?, val gap: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gap.toFloat(),
                context?.resources?.displayMetrics
            ).toInt()
        }
    }
}