package com.coding.aplikasistoryapp.util

import android.content.Context
import android.util.AttributeSet
import android.widget.Button

class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.buttonStyle
) : androidx.appcompat.widget.AppCompatButton(context, attrs, defStyleAttr) {

    fun enableButton(enable: Boolean) {
        isEnabled = enable
        alpha = if (enable) 1.0f else 0.5f
    }
}