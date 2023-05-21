package com.singhaayush.slidesprogressview.core.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.singhaayush.slidesprogressview.R
import com.singhaayush.slidesprogressview.core.utils.gone
import com.singhaayush.slidesprogressview.core.utils.visible
import com.singhaayush.slidesprogressview.core.utils.visibleIf
import com.singhaayush.slidesprogressview.databinding.PausableProgressViewBinding

class PausableProgressBar constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val binding: PausableProgressViewBinding by lazy {
        PausableProgressViewBinding.inflate(
            LayoutInflater.from(this.context),
            this,
            true
        )
    }
    private var animation: PausableScaleAnimation? = null
    private var duration = DEFAULT_PROGRESS_DURATION.toLong()
    private var callback: Callback? = null

    interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }

    constructor(context: Context) : this(context, null)


    fun setDuration(duration: Long) {
        this.duration = duration
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setMax() {
        finishProgress(true)
    }

    fun setMin() {
        finishProgress(false)
    }

    fun setMinWithoutCallback() {
        binding.progressCompletedStateView.run {
            setBackgroundResource(R.color.progress_initial)
            visible()
        }
        animation?.run {
            setAnimationListener(null)
            cancel()
        }


    }

    fun setMaxWithoutCallback() {
        binding.progressCompletedStateView.run {
            visible()
            setBackgroundResource(R.color.progress_completed)
        }
        animation?.run {
            setAnimationListener(null)
            cancel()
        }
    }

    private fun finishProgress(isMax: Boolean) {
        binding.progressCompletedStateView.run {
            visibleIf(isMax)
            if (isMax) setBackgroundResource(R.color.progress_completed)
        }
        animation?.run {
            setAnimationListener(null)
            cancel()
        }
        callback?.onFinishProgress()
    }

    fun showProgressInitialStateView() {
        binding.progressInitialStateView.visible()
    }

    fun startProgress() {
        binding.progressCompletedStateView.gone()
        animation = PausableScaleAnimation(
            0f,
            1f,
            1f,
            1f,
            Animation.ABSOLUTE,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f
        )
        animation?.run {
            duration = this@PausableProgressBar.duration
            interpolator = LinearInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    binding.progressRunningStateView.visible()
                    callback?.onStartProgress()
                }

                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    callback?.onFinishProgress()
                }
            })
            fillAfter = true
            binding.progressRunningStateView.startAnimation(animation)
        }

    }

    fun pauseProgress() {
        animation?.pause()
    }

    fun resumeProgress() {
        animation?.resume()
    }

    fun clear() {
        animation?.setAnimationListener(null)
        animation?.cancel()
        animation = null
    }

    private inner class PausableScaleAnimation(
        fromX: Float, toX: Float, fromY: Float,
        toY: Float, pivotXType: Int, pivotXValue: Float, pivotYType: Int,
        pivotYValue: Float
    ) :
        ScaleAnimation(
            fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
            pivotYValue
        ) {
        private var mElapsedAtPause: Long = 0
        private var mPaused = false
        override fun getTransformation(
            currentTime: Long,
            outTransformation: Transformation,
            scale: Float
        ): Boolean {
            if (mPaused && mElapsedAtPause == 0L) {
                mElapsedAtPause = currentTime - startTime
            }
            if (mPaused) {
                startTime = currentTime - mElapsedAtPause
            }
            return super.getTransformation(currentTime, outTransformation, scale)
        }

        fun pause() {
            if (mPaused) return
            mElapsedAtPause = 0
            mPaused = true
        }

        fun resume() {
            mPaused = false
        }
    }

    companion object {
        private const val DEFAULT_PROGRESS_DURATION = 2000
    }
}