package com.softbankrobotics.dx.robotfocusandandroidlifecycle

import android.os.Bundle
import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import java.util.*
import kotlin.concurrent.timer

class RobotUnavailableActivity: RobotActivity(), RobotLifecycleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_unavailable)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY)
    }

    private var countdownTimer: Timer? = null
    private var isRunning: Boolean = false;

    private fun startTimer(timeInMilliseconds: Long) {
        Log.i(TAG, "startTimer() called")

        countdownTimer?.cancel()
        isRunning = true
        countdownTimer = timer("HoldOn", initialDelay = timeInMilliseconds, period = 1000 ) {
            isRunning = false
            finish()
        }
    }

    override fun onResume() {
        Log.i(TAG, "onResume() called")
        super.onResume()
        QiSDK.register(this, this)

    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "onRobotFocusGained() called")
        countdownTimer?.cancel()
        finish()
    }

    override fun onRobotFocusLost() {
        Log.i(TAG, "onRobotFocusLost() called")
        countdownTimer?.cancel()
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.i(TAG, "onRobotFocusRefused() called because: $reason")
        startTimer(5000)
    }

    override fun onPause() {
        Log.i(TAG, "onPause() called")
        QiSDK.unregister(this, this)
        super.onPause()
    }

    override fun onStop() {
        Log.i(TAG, "onStop() called")
        countdownTimer?.cancel()
        super.onStop()
    }

    companion object {
        const val TAG = "RobotUnavailable"
    }
}
