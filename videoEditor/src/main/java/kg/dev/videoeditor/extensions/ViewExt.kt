package kg.dev.videoeditor.extensions

import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.getDrawableCompat(@DrawableRes resId: Int) = ContextCompat.getDrawable(this, resId)

fun View.getResourceName(viewType: Int) {
    context.resources.getResourceName(viewType)
}


fun View.getDrawableCompat(@DrawableRes resId: Int) = ContextCompat.getDrawable(context, resId)