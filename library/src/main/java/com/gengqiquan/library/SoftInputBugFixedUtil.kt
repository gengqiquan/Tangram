package com.gengqiquan.library

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout


class SoftInputBugFixedUtil(activity: Activity) {

    private var mOnSoftInputChangeListener: onSoftInputChangeListener? = null
    private val mChildOfContent: View
    private var usableHeightPrevious: Int = 0
    private val frameLayoutParams: FrameLayout.LayoutParams

    init {
        val content = activity.findViewById(android.R.id.content) as FrameLayout
        mChildOfContent = content.getChildAt(0)
        mChildOfContent.viewTreeObserver.addOnGlobalLayoutListener { possiblyResizeChildOfContent(activity) }
        frameLayoutParams = mChildOfContent.layoutParams as FrameLayout.LayoutParams
    }

    fun setOnSoftInputChangeListener(listener: onSoftInputChangeListener) {
        mOnSoftInputChangeListener = listener
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val usableHeightNow = computeUsableHeight(activity)
        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansSoftInput = mChildOfContent.rootView.height
            val heightDifference = usableHeightSansSoftInput - usableHeightNow
            if (heightDifference > usableHeightSansSoftInput / 4) {
                // SoftInput probably just became visible
                frameLayoutParams.height = usableHeightSansSoftInput - heightDifference
                if (mOnSoftInputChangeListener != null) {
                    mOnSoftInputChangeListener!!.change(true)
                }
            } else {
                // SoftInput probably just became hidden//部分手机有虚拟按键，不减去会导致布局显示不全
                frameLayoutParams.height = usableHeightSansSoftInput - getVirtualBarHeigh(activity)
                if (mOnSoftInputChangeListener != null) {
                    mOnSoftInputChangeListener!!.change(false)
                }
            }
            mChildOfContent.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computeUsableHeight(activity: Activity): Int {
        val r = Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top + getStatusHeight(activity)
    }

    /**
     * 监听软件盘弹起和隐藏

     * @author gengqiquan
     * *
     * @date 2017/6/2 下午2:40
     */
    interface onSoftInputChangeListener {
        fun change(show: Boolean)
    }

    companion object {


        fun assist(activity: Activity): SoftInputBugFixedUtil {
            return SoftInputBugFixedUtil(activity)
        }
    }


}

/**
 * 获得状态栏的高度

 * @param context
 * *
 * @return
 */
@android.support.annotation.Keep
@android.annotation.SuppressLint("PrivateApi")
fun getStatusHeight(context: Context): Int {

    var statusHeight = -1
    try {

        val clazz = Class.forName("com.android.internal.R\$dimen")
        val `object` = clazz.newInstance()
        val height = Integer.parseInt(clazz.getField("status_bar_height")
                .get(`object`).toString())
        statusHeight = context.resources.getDimensionPixelSize(height)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return statusHeight
}

/**
 * 获取虚拟功能键高度
 */
fun getVirtualBarHeigh(context: Context): Int {
    var vh = 0
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val dm = DisplayMetrics()
    try {
        val c = Class.forName("android.view.Display")
        val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
        method.invoke(display, dm)
        vh = dm.heightPixels - windowManager.defaultDisplay.height
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return vh
}
