package com.videotrimmer

import android.app.Activity
import android.content.Context
import android.graphics.Outline
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

const val CLICK_DELAY_MILLIS = 500L

fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        val listener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            // This notification will arrive just before the InputMethodManager gets set up.
            if (hasFocus) showTheKeyboardNow()
        }
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(listener)

        doOnDetach { viewTreeObserver.removeOnWindowFocusChangeListener(listener) }
    }
}

fun Activity.hideKeyboard() {
    currentFocus?.hideKeyboard()
}

fun View.hideKeyboard() {
    post {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }
}

fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.toast(@StringRes textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, textRes, duration).show()
}

fun Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), text, duration).show()
}

fun Fragment.toast(@StringRes textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), textRes, duration).show()
}

fun Context.dip(value: Int): Int = dipF(value).toInt()
fun Context.dipF(value: Int): Float = value * resources.displayMetrics.density
fun Context.dipF(value: Float): Float = value * resources.displayMetrics.density
fun View.getDrawableCompat(@DrawableRes resId: Int) = ContextCompat.getDrawable(context, resId)
fun Context.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)
fun View.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)
fun Fragment.getColorCompat(@ColorRes colorRes: Int) =
    ContextCompat.getColor(requireContext(), colorRes)

fun View.dip(value: Int): Int = context.dip(value)
fun View.dipF(value: Int): Float = context.dipF(value)

fun Fragment.dip(value: Int): Int = requireContext().dip(value)
fun Fragment.dipF(value: Int): Float = requireContext().dipF(value)
fun Fragment.dip(value: Float): Int = requireContext().dipF(value).toInt()

fun RecyclerView.attachAdapter(adapter: RecyclerView.Adapter<*>) {
    doOnAttach { this.adapter = adapter }
    doOnDetach { this.adapter = null }
}


inline fun View.setThrottleOnClickListener(crossinline callback: (view: View) -> Unit) {
    var lastClickTime = 0L
    this.setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > CLICK_DELAY_MILLIS) {
            lastClickTime = currentTimeMillis
            callback.invoke(it)
        }
    }
}

fun View.getResourceName(viewType: Int) {
    context.resources.getResourceName(viewType)
}

fun String.highlightUrlSpan(
    @ColorInt color: Int,
    onUrlClicked: (String) -> Unit
): SpannableStringBuilder {
    val spannableString = HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ) as SpannableStringBuilder
    val annotations = spannableString.getSpans(0, spannableString.length, URLSpan::class.java)
    annotations?.forEach {
        val start = spannableString.getSpanStart(it)
        val end = spannableString.getSpanEnd(it)

        spannableString.setSpan(
            SpanClickListener { onUrlClicked(it.url) },
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            0
        )
        spannableString.removeSpan(it)
    }
    return spannableString
}

private class SpanClickListener(
    private val onClicked: () -> Unit
) : ClickableSpan() {
    var lastClickTime = 0L
    override fun onClick(widget: View) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > CLICK_DELAY_MILLIS) {
            lastClickTime = currentTimeMillis
            onClicked()
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }
}

fun View.topRoundedCorner(radius: Int = dip(12)) {
    val viewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height + radius, radius.toFloat())
        }
    }
    outlineProvider = viewOutlineProvider
    clipToOutline = true
}

fun View.bottomRoundedCorner(radius: Int = dip(12)) {
    val viewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, -radius, view.width, view.height, radius.toFloat())
        }
    }
    outlineProvider = viewOutlineProvider
    clipToOutline = true
}

fun View.enable(isEnabled: Boolean) {
    this.isEnabled = isEnabled
}

fun View.visiblity(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun AutoCompleteTextView.dropDown() {
    this.setOnFocusChangeListener { _, b ->
        if (b) this.showDropDown()
    }
    this.setOnClickListener {
        this.showDropDown()
    }
}

inline fun ViewPager2.onChangePage(crossinline bloc: (Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            bloc(position)
        }

    })
}