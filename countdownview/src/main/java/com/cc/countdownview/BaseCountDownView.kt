package com.cc.countdownview

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

/**
 * @ClassName:  BaseCountDownView
 * @author: liujc
 * @date: 2018/11/30
 * @Description: 倒计时控件基础类
 */
abstract class BaseCountDownView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    companion object {
        const val COUNT_DOWN_INTERVAL_1000: Long = 1000 //默认时间间隔
        const val COUNT_DOWN_INTERVAL_100: Long = 100
        const val KEY_POINT_00 = "00"  //默认恢复值
        const val KEY_POINT_0 = "0"

        private const val MILLISECOND_PER_SENCOND: Long = 1000                 // 1000ms / s
        private const val MILLISECOND_PER_MINUTE = (1000 * 60).toLong()        // 1000*60ms/min
        private const val MILLISECOND_PER_HOUR = (1000 * 60 * 60).toLong()     // 1000 * 60 * 60ms/h
        private const val MILLISECOND_PER_DAY = (1000 * 60 * 60 * 24).toLong() // 1000 * 60 * 60 * 24ms/d
    }

    private var countDownInterval = COUNT_DOWN_INTERVAL_1000 //倒计时时间间隔
    private var stillShow = true //设置倒计时结束后，控件是否仍然展示，默认展示
    var isTiming = false //是否正在计时中

    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var mSecond: Int = 0
    private var mMilliSecond: Int = 0
    private var mCountDownTimer: CountDownTimer? = null
    private var mOnCountdownEndListener: OnTimeLimitFinishListener? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(timeLimitLayoutID(), this)
        if (view != null) {
            initTimeLimitView(view)
        }
    }

    /**
     * 倒计时显示布局
     * @return
     */
    protected abstract fun timeLimitLayoutID(): Int

    /**
     * 初始化倒计时布局
     * @param rootView
     */
    protected abstract fun initTimeLimitView(rootView: View)

    /**
     * 倒计时结束，归零显示状态
     */
    protected abstract fun resetZeroState(defaultZero: String)

    /**
     * 刷新倒计时状态
     * @param mDay
     * @param mHour
     * @param mMinute
     * @param mSecond
     * @param mMilliSecond
     */
    protected abstract fun refreshLimitTime(mDay: String, mHour: String, mMinute: String, mSecond: String, mMilliSecond: String)

    /**
     * 开启倒计时
     * @param countDownTime 剩余时间段，单位：ms
     */
    fun start(countDownTime: Long) {
        if (invalidTime(countDownTime)){
            return
        }
        cancelBeforeCountDownAction()
        doStartCountAction(countDownTime)
    }

    /*检查倒计时数据合法性，<= 0 的数据默认无效数据*/
    private fun invalidTime(countDownTime: Long): Boolean {
        if (countDownTime <= 0) {
            if (stillShow) {
                visibility = View.VISIBLE
            }else{
                visibility = View.GONE
            }
            return true
        }else{
            return false
        }
    }

    /*开启倒计时事件*/
    private fun doStartCountAction(countDownTime: Long) {
        isTiming = true
        visibility = View.VISIBLE
        mCountDownTimer = object : CountDownTimer(countDownTime, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                updateShow(millisUntilFinished)
            }

            override fun onFinish() {
                isTiming = false
                resetZeroState(KEY_POINT_00)
                if (null != mOnCountdownEndListener) {
                    mOnCountdownEndListener!!.timeLimitFinish()
                }
                if (!stillShow) {
                    visibility = View.GONE
                }
            }
        }
        mCountDownTimer!!.start()
    }

    /*取消之前的倒计时事件*/
    private fun cancelBeforeCountDownAction() {
        if (null != mCountDownTimer) {
            mCountDownTimer!!.cancel()
            mCountDownTimer = null
        }
    }

    fun setStillShow(stillShow: Boolean): BaseCountDownView {
        this.stillShow = stillShow
        return this
    }

    /**
     * 设置倒计时时间间隔
     * @param countDownInterval
     */
    fun setCountDownInterval(countDownInterval: Long): BaseCountDownView {
        this.countDownInterval = countDownInterval
        return this
    }

    fun setOnCountdownEndListener(onCountdownEndListener: OnTimeLimitFinishListener): BaseCountDownView {
        mOnCountdownEndListener = onCountdownEndListener
        return this
    }

    private fun updateShow(ms: Long) {
        mDay = (ms / MILLISECOND_PER_DAY).toInt()
        mHour = (ms % MILLISECOND_PER_DAY / MILLISECOND_PER_HOUR).toInt()
        mMinute = (ms % MILLISECOND_PER_HOUR / MILLISECOND_PER_MINUTE).toInt()
        mSecond = (ms % MILLISECOND_PER_MINUTE / MILLISECOND_PER_SENCOND).toInt()
        mMilliSecond = (ms % MILLISECOND_PER_SENCOND / 100).toInt()
        refreshLimitTime(formatNum(mDay), formatNum(mHour), formatNum(mMinute), formatNum(mSecond), mMilliSecond.toString())
    }

    fun getDay():Int{
        return this.mDay;
    }
    fun getHour():Int{
        return this.mHour;
    }
    fun getMinute():Int{
        return this.mMinute;
    }
    fun getSecond():Int{
        return this.mSecond;
    }
    fun getMilliSecond():Int{
        return this.mMilliSecond;
    }

    interface OnTimeLimitFinishListener {
        fun timeLimitFinish()
    }

    /**
     * 格式化显示数据：10 -> 10 , 6 -> 06
     */
    private fun formatNum(time: Int): String {
        return if (time < 10) "0$time" else time.toString()
    }

}