package com.softbankrobotics.dx.robotfocusandandroidlifecycle

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy

class MainActivity: RobotActivity(), RobotLifecycleCallbacks {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var dancingFuture: Future<Void>
    private var shouldBeDancing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY)
    }

    override fun onResume() {
        Log.i(TAG, "onResume() called")
        super.onResume()

        Log.i(TAG, "Registering...")
        QiSDK.register(this, this)
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "onRobotFocusGained() called")
        this.shouldBeDancing = true

        runOnUiThread{
            setContentView(R.layout.activity_main);
        }

        Log.i(TAG, "Starting music from onRobotFocusGained")
        startPlayingMusic()

        Log.i(TAG, "Starting dance from onRobotFocusGained")
        startDancing(qiContext)
    }

    override fun onRobotFocusLost() {
        // Called only when going to sleep mode
        Log.i(TAG, "onRobotFocusLost() called $this")
        this.shouldBeDancing = false

        // Stop dancing
        Log.i(TAG, "onRobotFocusLost() - request dance cancellation")
        cancelDancing()

        // Pause music
        Log.i(TAG, "onPause() - mediaplayer Pause call...")
        //pauseMusic()

        Log.i(TAG, "Launch UnavailableActivity from onRobotFocusLost...")
        // Call of an activity that will try to gain the focus
        // after a timeout it will finish() and try to restart MainActivity
        launchRobotUnavailableActivity()
    }

    override fun onRobotFocusRefused(reason: String?) {
        // Called when app is restarted if robot in rest mode
        Log.i(TAG, "onRobotFocusRefused: $reason - $this")
        this.shouldBeDancing = false

        // Stop dancing
        Log.i(TAG, "onRobotFocusRefused() - request dance cancellation")
        cancelDancing()

        Log.i(TAG, "Launch UnavailableActivity from onRobotFocusRefused...")
        // Call of another activity that will try to gain the focus
        // after a timeout it will finish() and try to restart MainActivity
        launchRobotUnavailableActivity()
    }

    override fun onPause() {
        Log.i(TAG, "onPause() called")
        // just in case onRobotFocusLost or onRobotFocusRefused which assign shouldBeDancing to false are not called first
        this.shouldBeDancing = false

        // Pause music
        Log.i(TAG, "onPause() - request mediaplayer pause...")
        pauseMusic()

        // Stop dancing
        Log.i(TAG, "onPause() - request dance cancellation")
        cancelDancing()

        Log.i(TAG, "Unregistering...")
        QiSDK.unregister(this, this)

        super.onPause()
    }

    override fun onDestroy() {
        Log.i(TAG, "call onDestroy()")

        // Release MediaPlayer
        stopMusic()

        super.onDestroy()
    }

    private fun launchRobotUnavailableActivity() {
        Log.i(TAG, "Unregister before UnavailableActivity")
        // The launchRobotUnavailableActivity() is called when robot focus is lost or refused
        // But losing the focus or getting it refused does not mean the app gets unregistered.
        // As the RobotUnavailableActivity tries to get the focus immediately through startActivity below
        // it is necessary to have the calling activity unregistered beforehand
        QiSDK.unregister(this, this)

        Log.i(TAG, "Launching RobotUnavailableActivity")
        val intent = Intent(this, RobotUnavailableActivity::class.java)
        runOnUiThread{
            setContentView(R.layout.activity_robot_unavailable)
        }
        startActivity(intent)
    }

    ////////////////////// Helper functions //////////////////////

    private fun startDancing(qiContext: QiContext) {
        val animation = AnimationBuilder.with(qiContext)
                .withResources(R.raw.headbang_a001)
                .build()
        val animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation)
                .build()

        dancingFuture = continuouslyRun(animate)
    }

    private fun continuouslyRun(animate: Animate): Future<Void> {
        // if robot should be dancing, it would start again (with thenCompose)
        Log.i(TAG, "constinuouslyRun called")
        return if (this.shouldBeDancing) {
            Log.i(TAG, "pepper should be dancing")
            animate.async().run().thenCompose {
                if ( it.hasError() ) {
                    Log.e(TAG, "ERROR on animateFuture: ${it.errorMessage}")
                }
                continuouslyRun(animate)
            }
        } else {
            Log.i(TAG, "pepper should not be dancing")
            Future.cancelled()
        }
    }

    private fun cancelDancing() {
        if ( this::dancingFuture.isInitialized ) {
            if ( !dancingFuture.isCancelled) {
                Log.i(TAG, "dance was not yet cancelled, requesting cancellation")
                dancingFuture.requestCancellation()
            }
            else {
                Log.i(TAG, "dance was cancelled already")
            }
        }
        else {
            Log.i(TAG, "dance was not initialized")
        }
    }

    private fun startPlayingMusic() {
        mediaPlayer = MediaPlayer.create(
                applicationContext,
                R.raw.music
        ).apply {
            isLooping = true
            start()
        }
    }

    private fun pauseMusic() {
        // Pause music
        Log.i(TAG, "pauseMusic() called...")
        if (mediaPlayer?.isPlaying == true) {
            Log.i(TAG, "mediaplayer was not yet paused, pausing mediaplayer")
            mediaPlayer?.pause()
        } else {
            Log.i(TAG, "mediaplayer was not playing or not initialized")
        }
    }

    private fun stopMusic() {
        // Pause music
        Log.i(TAG, "releasing media player")
        mediaPlayer?.release()
    }

    companion object {
        const val TAG = "RobotVibes"
    }
}