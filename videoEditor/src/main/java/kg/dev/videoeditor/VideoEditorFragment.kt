package kg.dev.videoeditor

import PlayerPositionListener
import PlayerPositionTracker
import android.animation.ValueAnimator
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import kg.dev.videoeditor.adapter.VideoDurationInSecAdapter
import kg.dev.videoeditor.adapter.VideoThumbNailAdapter
import kg.dev.videoeditor.databinding.FragmentVideoEditorBinding
import kg.dev.videoeditor.extensions.args
import kg.dev.videoeditor.extensions.getDrawableCompat
import kg.dev.videoeditor.extensions.withArgs
import kg.dev.videoeditor.utils.VideoTrimUtils.MAX_COUNT_RANGE
import kg.dev.videoeditor.utils.VideoTrimUtils.MAX_CUT_DURATION
import kg.dev.videoeditor.utils.VideoTrimUtils.MIN_CUT_DURATION
import kg.dev.videoeditor.utils.VideoTrimUtils.PADDING
import kg.dev.videoeditor.utils.VideoTrimUtils.PADDING_RIGHT
import kg.dev.videoeditor.utils.VideoTrimUtils.RECYCLER_VIEW_PADDING
import kg.dev.videoeditor.utils.VideoTrimUtils.VIDEO_FRAMES_WIDTH
import kg.dev.videoeditor.utils.formatSeconds
import kg.dev.videoeditor.utils.getDuration
import kg.dev.videoeditor.utils.getDurationInMs
import kg.dev.videoeditor.widgets.HorizontalCenteredDotItemDecoration
import kg.dev.videoeditor.widgets.RangeSeekBar
import kg.dev.videoeditor.widgets.VideoThumbSpacingItemDecoration
import kotlinx.coroutines.launch
import kotlin.math.abs


class VideoEditorFragment : Fragment(R.layout.fragment_video_editor), PlayerPositionListener {
    companion object {
        private const val EXTRA_VIDEO_URI = "EXTRA_VIDEO_URI"
        fun create(filePath: Uri?) = VideoEditorFragment().withArgs(EXTRA_VIDEO_URI to filePath)

    }

    private val binding: FragmentVideoEditorBinding by viewBinding()
    private val viewModel: VideoEditorViewModel by viewModels()
    private val filePath: Uri by args(EXTRA_VIDEO_URI)
    private var videoPlayer: ExoPlayer? = null
    private var isVideoEnded: Boolean = false
    private var lastMinValue: Long = 0L
    private var totalDuration: Long = 0L
    private var totalDurationInMs: Int = 0
    private var lastMaxValue: Long = 0L
    private var currentVolume: Float = 0f
    private var listSeconds = mutableListOf<Int>()
    private val retriever = MediaMetadataRetriever()
    private val videoAdapter by lazy {
        VideoThumbNailAdapter()
    }
    private var tracker: PlayerPositionTracker? = PlayerPositionTracker()
    private val videoDurationInSecAdapter by lazy {
        VideoDurationInSecAdapter()
    }

    private lateinit var seekBar: RangeSeekBar

    private var leftProgress: Long = 0
    private var rightProgress: Long = 0

    private var scrollPos: Long = 0
    private var isSeeking = false
    private var lastScrollX = 0
    private val mScaledTouchSlop = 0
    private var averageMsPx = 0f
    private var averagePxMs = 0f
    private var isOverScaledTouchSlop = false
    private lateinit var animator: ValueAnimator


    private val mOnRangeSeekBarChangeListener: RangeSeekBar.OnRangeSeekBarChangeListener =
        object : RangeSeekBar.OnRangeSeekBarChangeListener {
            override fun onRangeSeekBarValuesChanged(
                bar: RangeSeekBar?,
                minValue: Long,
                maxValue: Long,
                action: Int,
                isMin: Boolean,
                pressedThumb: RangeSeekBar.Thumb?
            ) {
                leftProgress = (minValue + scrollPos)
                rightProgress = (maxValue + scrollPos)
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        isSeeking = false
                        //    anim()
                        // videoPause()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        isSeeking = true


                        //   seekTo((if (pressedThumb === RangeSeekBar.Thumb.MIN) leftProgress else rightProgress))


                    }

                    MotionEvent.ACTION_UP -> {

                        isSeeking = false

                        videoPlayer?.seekTo(leftProgress)
                        // anim()
//                    videoStart()
//                    mTvShootTip.setText(
//                        String.format(
//                            "裁剪 %d s", (rightProgress - leftProgress) / 1000
//                        )
//                    )
                    }

                    else -> {}
                }
            }

            override fun onDrawProcess(r: Float) {
                Log.e("onDrawProcess Left", r.toString())
                binding.positionIcon.x = r + PADDING
            }

            override fun onDrawProcessRight(r: Float) {
                //binding.positionIcon.x = r - PADDING
                Log.e("onDrawProcess Right", r.toString())
            }
        }

    private val mOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isSeeking = false
                    //                videoStart();
                } else {
                    isSeeking = true
                    if (isOverScaledTouchSlop) {
                        //videoPause()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isSeeking = false
                val scrollX = getScrollXDistance()

                if (abs(lastScrollX - scrollX) < mScaledTouchSlop) {
                    isOverScaledTouchSlop = false
                    return
                }
                isOverScaledTouchSlop = true

                //初始状态,why ? 因为默认的时候有56dp的空白！
                if (scrollX == RECYCLER_VIEW_PADDING) {
                    scrollPos = 0
                } else {
                    // why 在这里处理一下,因为onScrollStateChanged早于onScrolled回调
                    // videoPause()
                    isSeeking = true
                    scrollPos = (averageMsPx * (RECYCLER_VIEW_PADDING + scrollX)).toLong()

                    leftProgress = seekBar.selectedMinValue + scrollPos
                    rightProgress = seekBar.selectedMaxValue + scrollPos

                    // videoPlayer?.seekTo(leftProgress)
                }
                lastScrollX = scrollX
                //  anim()
            }
        }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        initPlayer()
        buildMediaSource()
        getVideoData()
        setupVideoSettings()
        parseDuration(totalDuration)
        binding.rvVideoSteps.apply {
            adapter = videoAdapter
            addOnScrollListener(mOnScrollListener)

        }
        setupAdapter()
        anim()
        viewModel.loadThumbNails(retriever, filePath, requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.frameArray.collect {
                videoAdapter.submitList(it)

            }
        }
        videoDurationInSecAdapter.setItems(listSeconds)
    }

    override fun onPause() {
        super.onPause()
        videoPlayer?.playWhenReady = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (videoPlayer != null) {
            videoPlayer?.release()
            videoPlayer = null
            tracker?.release()
            tracker = null
            binding.rvVideoSteps.removeOnScrollListener(mOnScrollListener)
        }
    }

    @OptIn(UnstableApi::class)
    override fun onPlayerPositionChanged(position: Long) {
        binding.tvVideoStartDuration.text = formatSeconds(position.div(1000))
        Log.e("Current position ->", formatSeconds(position.div(1000)))
        if ((videoPlayer?.currentPosition ?: 0L) > rightProgress) {
            videoPlayer?.playWhenReady = false
            binding.checkboxPlay.apply {
                isChecked = false
                isSelected = false
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initPlayer() = with(binding) {
        try {
            videoPlayer = ExoPlayer.Builder(requireContext()).build()
            playerViewLib.apply {
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                player = videoPlayer
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val audioAttributes = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build()
                videoPlayer?.setAudioAttributes(audioAttributes, true)
            }


            tracker?.apply {
                player = videoPlayer
                playerPositionListener = this@VideoEditorFragment
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildMediaSource() = with(binding) {
        try {
            val mediaItem = MediaItem.Builder().setUri(filePath).setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder().setStartPositionMs(0)
                    .setEndPositionMs(60000).build()
            ).build()

            videoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_ENDED -> {
                                isVideoEnded = true
                                if (animator.isRunning) {
                                    animator.cancel()
                                }
                                anim()
                                checkboxPlay.apply {
                                    isChecked = false
                                    isSelected = false
                                }
                                videoPlayer?.playWhenReady = false

                            }

                            Player.STATE_READY -> {
                                isVideoEnded = false
//

                            }

                            Player.STATE_BUFFERING, Player.STATE_IDLE -> {}
                        }
                    }

                })
            }
            currentVolume = videoPlayer?.volume ?: 0f
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onVideoClicked(isCheckedBox: Boolean) {
        val icon = if (isCheckedBox) R.drawable.ic_pause else R.drawable.ic_play
        binding.checkboxPlay.apply {
            setImageDrawable(requireContext().getDrawableCompat(icon))
            isChecked = isCheckedBox
            isSelected = isCheckedBox

        }
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue)
                videoPlayer?.playWhenReady = true
                return
            }
            //  videoPlayer?.seekTo(leftProgress)
            videoPlayer?.playWhenReady = isCheckedBox


            if (isCheckedBox) {
                animator.resume()
            } else {
                animator.pause()
            }

            if (((animator.animatedValue as? Int) ?: 0) <= 0) {
                animator.start()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupVideoSettings() = with(binding) {
        checkboxPlay.setOnCheckedChangeListener { _, isChecked ->
            onVideoClicked(isChecked)
        }

        checkboxVolume.setOnCheckedChangeListener { _, isChecked ->
            muteSound(isChecked)
        }

        tvVideoStopDuration.text = formatSeconds(totalDuration)
        ivZoom.setOnCheckedChangeListener { _, isChecked ->
            changeVideoSizeMode(isChecked)
        }
    }

    private fun setupAdapter() = with(binding) {
        //for video edit
        val startPosition: Long = 0
        val endPosition: Long = totalDuration * 1000L
        val thumbnailsCount: Int
        val rangeWidth: Int
        val over10S: Boolean
        if (endPosition <= MAX_CUT_DURATION) {
            over10S = false
            thumbnailsCount = MAX_COUNT_RANGE
            rangeWidth = VIDEO_FRAMES_WIDTH
        } else {
            over10S = true
            thumbnailsCount =
                (endPosition * 1.0f / (MAX_CUT_DURATION * 1.0f) * MAX_COUNT_RANGE).toInt()
            rangeWidth = VIDEO_FRAMES_WIDTH / MAX_COUNT_RANGE * thumbnailsCount
        }

        rvVideoSteps.apply {
            addItemDecoration(
                VideoThumbSpacingItemDecoration(
                    RECYCLER_VIEW_PADDING, thumbnailsCount
                )
            )
        }

        //init seekBar

        //init seekBar
        if (over10S) {
            seekBar = RangeSeekBar(
                requireContext(), 0L, MAX_CUT_DURATION
            )
            seekBar.selectedMinValue = 0L
            seekBar.selectedMaxValue = MAX_CUT_DURATION
        } else {
            seekBar = RangeSeekBar(requireContext(), 0L, endPosition)
            seekBar.selectedMinValue = 0L
            seekBar.selectedMaxValue = endPosition
        }

        seekBar.setMin_cut_time(MIN_CUT_DURATION) //设置最小裁剪时间

        seekBar.isNotifyWhileDragging = true
        seekBar.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener)
        binding.seekBarLayout.addView(seekBar)
        averageMsPx = totalDuration * 1.0f / rangeWidth * 1.0f


        //init pos icon start
        //leftProgress = 0
        rightProgress = if (over10S) {
            MAX_CUT_DURATION
        } else {
            endPosition
        }

        averagePxMs = VIDEO_FRAMES_WIDTH * 1.0f / (rightProgress - leftProgress)

        rvVideoDurationInSec.apply {
            this.adapter = videoDurationInSecAdapter
            this.addItemDecoration(HorizontalCenteredDotItemDecoration(requireContext()))
        }
    }


    private fun getScrollXDistance(): Int {
        val layoutManager: LinearLayoutManager =
            binding.rvVideoSteps.layoutManager as LinearLayoutManager
        val position: Int = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView: View? = layoutManager.findViewByPosition(position)
        val itemWidth = firstVisibleChildView?.width
        return position * itemWidth!! - firstVisibleChildView.left
    }

    @OptIn(UnstableApi::class)
    private fun getVideoData() {
        totalDuration = requireContext().getDuration(filePath)
        totalDurationInMs = requireContext().getDurationInMs(filePath).toInt()
        lastMaxValue = totalDuration
    }

    private fun seekTo(sec: Long) {
        if (videoPlayer != null) videoPlayer?.seekTo(sec)
    }

    private fun muteSound(isChecked: Boolean) {
        val icon = if (isChecked) R.drawable.ic_mute else R.drawable.ic_volume
        binding.checkboxVolume.setImageDrawable(requireContext().getDrawableCompat(icon))
        val volume = if (isChecked) 0f else currentVolume
        videoPlayer?.volume = volume

    }

    @OptIn(UnstableApi::class)
    private fun changeVideoSizeMode(isChecked: Boolean) = with(binding) {
        val mode =
            if (isChecked) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerViewLib.resizeMode = mode

    }

    private fun parseDuration(duration: Long) {
        val list = (0..duration.toInt()).toMutableList()
        if (duration > 15) {
            val firstElement = list.first()
            val lastElement = list.last()
            val divisibleBy10 = list.filter { it % 5 == 0 }
            val resultArray = mutableListOf<Int>().apply {
                add(firstElement)
                addAll(divisibleBy10)
                add(lastElement)
            }
            listSeconds.addAll(resultArray)
        } else {
            listSeconds.addAll(list)
        }
    }


    private fun anim() = with(binding) {
        val params = positionIcon.layoutParams as ConstraintLayout.LayoutParams
        val start: Int =
            (RECYCLER_VIEW_PADDING + ((videoPlayer?.currentPosition
                ?: 0) - scrollPos) * averagePxMs).toInt()
        val end: Int = (PADDING_RIGHT + (rightProgress - scrollPos) * averagePxMs).toInt()
        animator = ValueAnimator.ofInt(start, end)
        animator.duration =
            rightProgress - scrollPos - ((videoPlayer?.currentPosition ?: 0) - scrollPos)
        Log.e("Anim duration ->", animator.duration.toString())
        Log.e("Anim duration rightProgress ->", rightProgress.toString())
        Log.e("Anim duration scrollPos ->", scrollPos.toString())

        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            params.leftMargin = animation.animatedValue as Int
            positionIcon.layoutParams = params
        }
    }
}


