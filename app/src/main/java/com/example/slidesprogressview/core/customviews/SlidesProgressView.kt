package com.example.slidesprogressview.core.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.example.slidesprogressview.R

class SlidesProgressView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val progressBarLayoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    private val spaceLayoutParams = LayoutParams(8, LayoutParams.WRAP_CONTENT)
    private val progressBars: MutableList<PausableProgressBar> = ArrayList()
    private var slidesCount = -1
    private var currentSlideIndex = -1
    private var isComplete = false
    private var isSkipStart = false
    private var isReverseStart = false
    private var onNext: () -> Unit = {}
    private var onPrev: () -> Unit = {}
    private var onComplete: () -> Unit = {}

    init {
        orientation = HORIZONTAL
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidesProgressView)
        slidesCount = typedArray.getInt(R.styleable.SlidesProgressView_progressCount, 0)
        typedArray.recycle()
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()
        for (i in 0 until slidesCount) {
            val progressBarView = createProgressBar()
            progressBars.add(progressBarView)
            addView(progressBarView)
            if (i + 1 < slidesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): PausableProgressBar = PausableProgressBar(context).apply {
        showProgressInitialStateView()
        layoutParams = progressBarLayoutParams
    }

    private fun createSpace(): View = View(context).apply {
        layoutParams = spaceLayoutParams
    }


    fun setSlidesCount(slidesCount: Int) {
        this.slidesCount = slidesCount
        bindViews()
    }


    fun skip() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (currentSlideIndex < 0) return
        isSkipStart = true
        progressBars[currentSlideIndex].setMax()
    }


    fun reverse() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (currentSlideIndex < 0) return
        isReverseStart = true
        progressBars[currentSlideIndex].setMin()
    }


    fun setSlideDuration(duration: Long) {
        for (i in progressBars.indices) {
            progressBars[i].run {
                setDuration(duration)
                setCallback(callback(i))
            }
        }
    }

    fun setOnNextSlideListener(onNext: () -> Unit) {
        this.onNext = onNext
    }

    fun setOnPrevSlideListener(onPrev: () -> Unit) {
        this.onPrev = onPrev
    }

    fun setOnCompleteSlideListener(onComplete: () -> Unit) {
        this.onComplete = onComplete
    }

    private fun callback(index: Int): PausableProgressBar.Callback {
        return object : PausableProgressBar.Callback {
            override fun onStartProgress() {
                currentSlideIndex = index
            }

            override fun onFinishProgress() {
                if (isReverseStart) {
                    onPrev()
                    if (0 <= (currentSlideIndex - 1)) {
                        val p = progressBars[currentSlideIndex - 1]
                        p.setMinWithoutCallback()
                        progressBars[--currentSlideIndex].startProgress()
                    } else {
                        progressBars[currentSlideIndex].startProgress()
                    }
                    isReverseStart = false
                    return
                }
                val next = currentSlideIndex + 1
                if (next <= (progressBars.size - 1)) {
                    onNext()
                    progressBars[next].startProgress()
                } else {
                    isComplete = true
                    onComplete()
                }
                isSkipStart = false
            }
        }
    }

    fun startSlideShow(from: Int) {
        for (i in 0 until from) {
            progressBars[i].setMaxWithoutCallback()
        }
        progressBars[from].startProgress()
    }

    fun destroy() {
        progressBars.forEach {
            it.clear()
        }
    }

    fun pause() {
        if (currentSlideIndex < 0) return
        progressBars[currentSlideIndex].pauseProgress()
    }

    fun resume() {
        if (currentSlideIndex < 0) return
        progressBars[currentSlideIndex].resumeProgress()
    }
}