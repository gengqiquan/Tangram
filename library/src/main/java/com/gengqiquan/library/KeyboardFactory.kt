package com.gengqiquan.library

import android.content.Context
import android.inputmethodservice.Keyboard
import android.util.DisplayMetrics
import android.view.WindowManager
import org.jetbrains.anko.dip


object KeyboardFactory {

    fun createKeyboard(context: Context, type: KeyboardType): KeyboardContent {
        return Builder(context)
                .type(type)
                .build()
    }

//    fun getXML(type:KeyboardType): Int {
//        when (type) {KeyboardType.NONE -> return R.xml.keyboard_number
//           KeyboardType.NUMBER -> return R.xml.keyboard_number
//           KeyboardType.NUMBER_DECIMAL -> return R.xml.keyboard_number
//           KeyboardType.PHONE -> return R.xml.keyboard_number
//           KeyboardType.TEXT -> return R.xml.vin_keyboard
//           KeyboardType.PASSWORD -> return R.xml.keyboard_number
//           KeyboardType.VIN_CODE -> return R.xml.keyboard_number
//        }
//    }

    class Builder(private val mContext: Context) {
        private var mXmlID: Int = 0
        private var mType: KeyboardType = KeyboardType.NONE
        private var mHeight: Int = 0
        private var mWidth: Int = 0
        private var mModeId: Int = 0


        fun xmlID(mXmlID: Int): Builder {
            this.mXmlID = mXmlID
            return this
        }

        fun height(mHeight: Int): Builder {
            this.mHeight = mHeight
            return this
        }

        fun width(mWidth: Int): Builder {
            this.mWidth = mWidth
            return this
        }

        fun type(mType: KeyboardType): Builder {
            this.mType = mType
            return this
        }

        fun build(): KeyboardContent {
            when (mType) {KeyboardType.NONE -> {
                mXmlID = R.xml.keyboard_number
                mHeight = (getScreenWidth(mContext).toFloat() * 72 / 100).toInt()
            }
                KeyboardType.NUMBER -> {
                    mXmlID = XML_NUMBER
                    mHeight = (getScreenWidth(mContext).toFloat() * 60 / 100).toInt()
                }
                KeyboardType.NUMBER_DECIMAL -> {
                    mXmlID = XML_NUMBER_DECIMAL
                    mHeight = (getScreenWidth(mContext).toFloat() * 60 / 100).toInt()
                }
                KeyboardType.PHONE -> {
                    mXmlID = XML_PHONE
                    mHeight = (getScreenWidth(mContext).toFloat() * 60 / 100).toInt()
                }
                KeyboardType.TEXT -> {
                    mXmlID = XML_TEXT
                    mHeight = mContext.dip(200)
                }
                KeyboardType.Email -> {
                    mXmlID = XML_EMAIL
                    mHeight = (getScreenWidth(mContext).toFloat() * 60 / 100).toInt()
                }
                KeyboardType.VIN_CODE -> mXmlID = XML_VIN_CODE
            }
            if (mModeId < 0) {
                mModeId = 0
            }
            if (mWidth <= 0) {
                mWidth = getScreenWidth(mContext)
            }
            if (mHeight <= 0) {
                mHeight = getScreenWidth(mContext)
            }
            return KeyboardContent(Keyboard(mContext, mXmlID, 0, mWidth, mHeight), mHeight)
        }
    }

    class KeyboardContent constructor(var keyboard: Keyboard, var contentHeight: Int)

    /**
     * 获得屏幕宽度

     * @param context
     * *
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    val XML_NUMBER = R.xml.keyboard_number
    val XML_NUMBER_DECIMAL = R.xml.keyboard_number_decimal
    val XML_PHONE = R.xml.keyboard_phone
    val XML_EMAIL = R.xml.keyboard_email_address
    val XML_TEXT = R.xml.keyboard_number
    val XML_VIN_CODE = R.xml.vin_keyboard

}
