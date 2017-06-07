package com.gengqiquan.library

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText

/**
 * Created by gengqiquan on 2017/6/7.
 */
class TangramEditText : EditText {
    var mType: KeyboardType = KeyboardType.NONE

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.TangramEditText, defStyleAttr, defStyleRes)
        mType = KeyboardType.from(a.getInt(R.styleable.TangramEditText_keyboard_type, -1))
        a.recycle()

    }


}