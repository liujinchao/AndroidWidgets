package com.liujc.androidwidgets

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cc.countdownview.BaseCountDownView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testCountDownView()
    }

    private fun testCountDownView() {
        dhms_count_down.setCountDownInterval(BaseCountDownView.COUNT_DOWN_INTERVAL_100)
                .setOnCountdownEndListener(object : BaseCountDownView.OnTimeLimitFinishListener{
                    override fun timeLimitFinish() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
                .start(100000000)
        current_time.setOnClickListener { show_msg.text = "${dhms_count_down.getDay()} - ${dhms_count_down.getHour()} - ${dhms_count_down.getMinute()} - ${dhms_count_down.getSecond()} - ${dhms_count_down.getMilliSecond()}" }

        btn_camera.setOnClickListener{
            CameraActivity.intentTo(this)
        }
    }
}
