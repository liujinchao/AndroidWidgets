package com.liujc.androidwidgets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.app.util.swipeback.SwipeBackActivityBase
import com.app.util.swipeback.SwipeBackActivityHelper
import com.app.util.swipeback.SwipeBackLayout
import com.app.util.swipeback.Utils

/**
 * @author liujc
 * @date 2020/5/4
 * @Description (这里用一句话描述这个类的作用)
 */
class WidgetsActivity : AppCompatActivity(), SwipeBackActivityBase{

    lateinit var mSwipeHelper: SwipeBackActivityHelper

    companion object{
        @JvmStatic
        fun intentTo(context: Context?) {
            val intent = Intent(context, WidgetsActivity::class.java)
            context?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widgets)
        mSwipeHelper = SwipeBackActivityHelper(this)
        mSwipeHelper.onActivityCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mSwipeHelper.onPostCreate()
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mSwipeHelper.swipeBackLayout
    }

    override fun scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this)
        swipeBackLayout.scrollToFinishActivity()
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout.setEnableGesture(enable)
    }
}