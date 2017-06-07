package com.gengqiquan.library

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Handler
import android.text.InputType.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import org.jetbrains.anko.dip

@SuppressLint("InflateParams")
class Tangram private constructor(private val mActivity: Activity) {
    var mParent: FrameLayout = LayoutInflater.from(mActivity).inflate(R.layout.layout_vin_keyboard, null) as FrameLayout
    lateinit private var mEditView: EditText
    private val mContentView: FrameLayout
    private var mOnSureClicklistener: onSureClicklistener? = null
    private var isShow = false
    var mKeyboardView: KeyboardView
    var mKeyboard: Keyboard? = null

    private val mChildOfContent: View
    private val frameLayoutParams: FrameLayout.LayoutParams

    init {

        mKeyboardView = mParent.findViewById(R.id.keyboard_view) as KeyboardView

        mContentView = mActivity.findViewById(android.R.id.content) as FrameLayout
        mChildOfContent = mContentView.getChildAt(0)
        mChildOfContent.viewTreeObserver.addOnGlobalFocusChangeListener { _, newFocus ->
            if (newFocus is EditText) {
                mEditView = newFocus
                mEditView.setOnClickListener { showKeyboard() }
                mEditView.setOnKeyListener(keyListener)
                if (!isShow)
                    showKeyboard()
                else
                    changeKeyboard()// 根据输入类型切换软键盘
            } else {
                hideKeyboard()
            }
        }
        frameLayoutParams = mChildOfContent.layoutParams as FrameLayout.LayoutParams
    }

    private fun changeKeyboard() {
        val content: KeyboardFactory.KeyboardContent = if (mEditView is TangramEditText) KeyboardFactory.createKeyboard(mActivity, (mEditView as TangramEditText).mType) else getKeyboard(mEditView)
        mKeyboard = content.keyboard
        val height = content.contentHeight
        setSystemKeyboardEnable(false)
        hideSystemKeyboard(mActivity, mEditView)
        mKeyboardView.keyboard = mKeyboard
        mKeyboardView.isPreviewEnabled = false
        mKeyboardView.isEnabled = true
        mKeyboardView.setOnKeyboardActionListener(listener)
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        layoutParams.gravity = Gravity.BOTTOM
        mContentView.removeView(mParent)
        mContentView.addView(mParent, layoutParams)
    }


    fun showKeyboard() {
        if (isShow) {
            return
        }
        changeKeyboard()
        isShow = true
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(mParent)
        Handler().postDelayed({ moveEditShow(mActivity) }, 200)
    }

    private fun getKeyboard(editText: EditText): KeyboardFactory.KeyboardContent {
        val inputType = editText.inputType
        var type = KeyboardType.NONE
        when (inputType) {
            TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_NORMAL//number
            -> type = KeyboardType.NUMBER
            TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL//numberDecimal
            -> type = KeyboardType.NUMBER_DECIMAL
            TYPE_CLASS_PHONE//phone
            -> type = KeyboardType.PHONE
            TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL//text
            -> type = KeyboardType.TEXT
            TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_EMAIL_ADDRESS//email_address
            -> type = KeyboardType.Email
            else -> {
            }
        }

        return KeyboardFactory.createKeyboard(mActivity, type)
    }


    val listener = object : KeyboardView.OnKeyboardActionListener {
        override fun swipeUp() {}

        override fun swipeRight() {}

        override fun swipeLeft() {}

        override fun swipeDown() {}

        override fun onText(text: CharSequence) {
            val editable = mEditView.text
            val start = mEditView.selectionStart
            editable!!.insert(start, text.toString())
        }

        override fun onRelease(primaryCode: Int) {}

        override fun onPress(primaryCode: Int) {
        }

        //一些特殊操作按键的codes是固定的比如完成、回退等
        override fun onKey(primaryCode: Int, keyCodes: IntArray) {
            val editable = mEditView.text
            val start = mEditView.selectionStart
            when (primaryCode) {
                Keyboard.KEYCODE_DELETE -> if (editable != null && editable.isNotEmpty()) {
                    if (start > 0) {
                        editable.delete(start - 1, start)
                    }
                }
                -10 -> {
                    //handle paste
                    val myClipboard = mActivity.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = myClipboard.primaryClip
                    if (clip != null && clip.itemCount > 0)
                        editable!!.insert(start, clip.getItemAt(0).coerceToText(mActivity))
                }
                Keyboard.KEYCODE_SHIFT -> changeKey()
                Keyboard.KEYCODE_DONE -> if (mOnSureClicklistener == null || !mOnSureClicklistener!!.onClick(mEditView)) {
                    hideKeyboard()
                }
                KeyEvent.KEYCODE_BACK -> {
                    if (isShow) {
                        hideKeyboard()
                    }
                }
                else -> editable!!.insert(start, Character.toString(primaryCode.toChar()))
            }
        }
    }
    val keyListener = View.OnKeyListener { _, keyCode, _ ->
        if (keyCode == KeyEvent.KEYCODE_BACK && isShow) {
            hideKeyboard()
            true
        } else {
            false
        }
    }

    /**
     * 设置系统键盘是否弹出

     * @param enable true 弹出系统键盘，false 不弹出
     * *
     * @author hsh
     * *
     * @time 2017/6/1 001 下午 04:09
     * *
     * @version 1.7.6
     */
    private fun setSystemKeyboardEnable(enable: Boolean) {
        try {
            val setShowSoftInputOnFocus = mEditView.javaClass.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
            setShowSoftInputOnFocus.isAccessible = true
            setShowSoftInputOnFocus.invoke(mEditView, enable)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: java.lang.reflect.InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

    }

    /**
     * 返回true表示调用者拦截事件自己处理

     * @author gengqiquan
     * *
     * @date 2017/6/1 下午2:39
     */
    fun setOnSureClicklistener(sureClicklistener: onSureClicklistener) {
        mOnSureClicklistener = sureClicklistener
    }

    fun hideKeyboard() {
        if (!isShow) {
            return
        }
        isShow = false
        moveEditHide(mActivity)
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(mParent)
        Handler().postDelayed({
            mContentView.removeView(mParent)
            isShow = false
        }, 300)

    }

    private fun moveEditShow(activity: android.content.Context) {
        val usableHeightNow = computeUsableHeight(activity)
        frameLayoutParams.height = usableHeightNow - dip(256)
        mChildOfContent.requestLayout()
    }

    private fun moveEditHide(activity: android.content.Context) {
        val usableHeightNow = computeUsableHeight(activity)
        frameLayoutParams.height = usableHeightNow
        mChildOfContent.requestLayout()
    }

    private fun computeUsableHeight(activity: android.content.Context): Int {
        val r = android.graphics.Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top + getStatusHeight(activity)
    }

    /**
     * 键盘大小写切换
     */
    private fun changeKey() {
        val keylist = mKeyboard!!.keys
        val shift: Keyboard.Key = keylist.lastOrNull { it.codes.single() == Keyboard.KEYCODE_SHIFT } ?: return
        if (shift.sticky) {//大写切换小写
            for (key in keylist) {
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase()
                    key.codes[0] = key.codes[0] + 32
                }
            }
            shift.label = shift.label.toString().toLowerCase()
            shift.sticky = false
        } else {//小写切换大写
            for (key in keylist) {
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase()
                    key.codes[0] = key.codes[0] - 32
                }
            }
            shift.label = shift.label.toString().toUpperCase()
            shift.sticky = true
        }
        mKeyboardView.keyboard = mKeyboard
    }

    private fun isLetter(lable: String): Boolean {
        if (lable.length > 1) {
            return false
        }
        val a: Char = lable.single()
        if (a in 'a'..'z' || a in 'A'..'Z') {
            return true
        }
        return false
    }

    /**
     * 隐藏系统键盘

     * @param v
     */
    private fun hideSystemKeyboard(context: android.content.Context, v: View) {
        val inputManger = context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManger.hideSoftInputFromWindow(v.windowToken, 0)
    }

    /**
     * 返回true表示调用者拦截事件自己处理

     * @author gengqiquan
     * *
     * @date 2017/6/1 下午2:39
     */
    interface onSureClicklistener {
        fun onClick(v: EditText): Boolean
    }

    companion object {

        fun track(activity: Activity): Tangram {
            return Tangram(activity)
        }
    }

    fun dip(value: Int) = mActivity.dip(value)
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