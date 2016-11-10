package at.metalab.meks.metapp

import android.content.Context
import android.content.res.Resources

/**
 * Created by meks on 02.09.2016.
 */

inline fun Any.wasInit(f: () -> Unit): Boolean {
    try {
        f()
    }
    catch(e: UninitializedPropertyAccessException) {
        return false
    }
    return true
}

fun convertDpToPixel(dp: Float): Float {
    val metrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return Math.round(px).toFloat()
}


fun pxFromDp(dp: Float, context: Context): Float {
    return dp * context.getResources().getDisplayMetrics().density
}