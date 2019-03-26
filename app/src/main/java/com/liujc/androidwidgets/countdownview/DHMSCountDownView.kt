package com.liujc.androidwidgets.countdownview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.cc.countdownview.BaseCountDownView
import com.liujc.androidwidgets.R
import kotlinx.android.synthetic.main.layout_dhms_count_down.view.*

/**
 * @ClassName:  DHMSCountDownView
 * @author: liujc
 * @date: 2019/3/20
 * @Description: 事例倒计时控件，继承BaseCountDownView即可
 */
class DHMSCountDownView(context: Context, attrs: AttributeSet) : BaseCountDownView(context, attrs) {
    private lateinit var mDayTv: TextView
    private lateinit var mHourTv: TextView
    private lateinit var mMinTv: TextView
    private lateinit var mSecTv: TextView
    private lateinit var mMilliSecTv: TextView

    override fun timeLimitLayoutID(): Int {
        return R.layout.layout_dhms_count_down
    }

    override fun initTimeLimitView(rootView: View) {
        mDayTv = rootView.findViewById(R.id.tv_day);
        mHourTv = rootView.findViewById(R.id.tv_hour);
        mMinTv = rootView.findViewById(R.id.tv_min);
        mSecTv = rootView.findViewById(R.id.tv_second);
        mMilliSecTv = rootView.findViewById(R.id.tv_ms);
    }

    override fun resetZeroState(defaultZero: String) {
        mDayTv.text = defaultZero
        mHourTv.text = defaultZero
        mMinTv.text = defaultZero
        mSecTv.text = defaultZero
        mMilliSecTv.text = BaseCountDownView.KEY_POINT_0
    }

    override fun refreshLimitTime(mDay: String, mHour: String, mMinute: String, mSecond: String, mMilliSecond: String) {
        mDayTv.text = mDay
        mHourTv.text = mHour
        mMinTv.text = mMinute
        mSecTv.text = mSecond
        mMilliSecTv.text = mMilliSecond
    }

}