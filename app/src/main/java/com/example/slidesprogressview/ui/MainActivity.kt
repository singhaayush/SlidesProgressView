package com.example.slidesprogressview.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.example.slidesprogressview.databinding.ActivityMainBinding
import com.example.slidesprogressview.ui.utils.onClick
import com.example.slidesprogressview.ui.utils.toast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var pressTime = 0L
    private var initialSlideIndex = 0
    private val onTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent): Boolean {

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    binding.slidesProgressView.pause()
                    return false
                }
                MotionEvent.ACTION_UP -> {
                    binding.slidesProgressView.resume()
                    val millisPassedSincePressedDown = System.currentTimeMillis() - pressTime
                    return MINIMUM_PRESS_TIME_TO_PAUSE_PROGRESS < millisPassedSincePressedDown
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.run {
            actionPreviousSlide.run {
                onClick { slidesProgressView.reverse() }
                setOnTouchListener(onTouchListener)
            }
            actionNextSide.run {
                onClick { slidesProgressView.skip() }
                setOnTouchListener(onTouchListener)
            }
            slidesProgressView.run {
                setSlidesCount(SLIDES_COUNT)
                setSlideDuration(SLIDE_SHOW_INTERVAL)
                setOnNextSlideListener(onNext = {
                    toast("onNext")
                })
                setOnPrevSlideListener(onPrev = {
                    toast("onPrev")
                })
                setOnCompleteSlideListener(onComplete = {
                    toast("onComplete")
                })
                startSlideShow(initialSlideIndex)
            }
        }

    }

    override fun onDestroy() {
        binding.slidesProgressView.destroy()
        super.onDestroy()
    }

    companion object {
        private const val MINIMUM_PRESS_TIME_TO_PAUSE_PROGRESS = 500L
        private const val SLIDE_SHOW_INTERVAL = 3000L
        private const val SLIDES_COUNT = 5
    }
}