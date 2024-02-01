package kg.dev.videoeditor

import PlayerPositionListener
import PlayerPositionTracker
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import kg.dev.videoeditor.adapter.VideoDurationInSecAdapter
import kg.dev.videoeditor.adapter.VideoThumbNailAdapter
import kg.dev.videoeditor.databinding.FragmentVideoEditorBinding
import kg.dev.videoeditor.extensions.args
import kg.dev.videoeditor.extensions.dip
import kg.dev.videoeditor.extensions.getDrawableCompat
import kg.dev.videoeditor.extensions.withArgs
import kg.dev.videoeditor.utils.SpaceItemDecoration
import kg.dev.videoeditor.utils.SpaceItemDecoration.Companion.HORIZONTAL
import kg.dev.videoeditor.utils.formatSeconds
import kg.dev.videoeditor.utils.getDuration
import kg.dev.videoeditor.utils.getDurationInMs
import kotlinx.coroutines.launch


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
        setupAdapter()
        parseDuration(totalDuration)
        setupSlider()
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

        }
    }

    @OptIn(UnstableApi::class)
    override fun onPlayerPositionChanged(position: Long) {
        binding.tvVideoStartDuration.text = formatSeconds(position.div(1000))
        // Log.d("Video ->", position.toString())
        Log.d("Video ->", "${position.toInt()}")
        binding.seekBar.progress = position.toInt()
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
                                checkboxPlay.apply {
                                    isChecked = false
                                    isSelected = false
                                }
                                videoPlayer?.playWhenReady = false
                                binding.seekBar.progress = 0
                            }

                            Player.STATE_READY -> {
                                isVideoEnded = false
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

    private fun onVideoClicked(isChecked: Boolean) {
        val icon = if (isChecked) R.drawable.ic_pause else R.drawable.ic_play
        binding.checkboxPlay.setImageDrawable(requireContext().getDrawableCompat(icon))
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue)
                videoPlayer?.playWhenReady = true
                return
            }
            videoPlayer?.playWhenReady = isChecked
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
        rvVideoSteps.adapter = videoAdapter
        rvVideoSteps.addItemDecoration(
            DividerItemDecoration(
                requireContext(), DividerItemDecoration.HORIZONTAL
            )
        )
        rvVideoDurationInSec.apply {
            this.adapter = videoDurationInSecAdapter
            this.addItemDecoration(SpaceItemDecoration(dip(2), HORIZONTAL))
        }
    }

    @OptIn(UnstableApi::class)
    private fun getVideoData() {
        totalDuration = requireContext().getDuration(filePath)
        totalDurationInMs = requireContext().getDurationInMs(filePath).toInt()
        Log.d("Video total Duration ->", totalDurationInMs.toString())
        lastMaxValue = totalDuration
    }

    private fun seekTo(sec: Long) {
        if (videoPlayer != null) videoPlayer?.seekTo(sec * 1000)
    }

    private fun muteSound(isChecked: Boolean) {
        val icon = if (isChecked) R.drawable.ic_mute else R.drawable.ic_volume
        binding.checkboxVolume.setImageDrawable(requireContext().getDrawableCompat(icon))
        if (isChecked) {
            videoPlayer?.volume = 0f
        } else {
            videoPlayer?.volume = currentVolume
        }
    }

    @OptIn(UnstableApi::class)
    private fun changeVideoSizeMode(isChecked: Boolean) = with(binding) {
        if (isChecked) {
            playerViewLib.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        } else {
            playerViewLib.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
    }

    private fun parseDuration(duration: Long) {
        val list = (1..duration.toInt()).toMutableList()
        if (duration > 15) {
            val firstElement = list.first()
            val lastElement = list.last()
            val divisibleBy10 = list.filter { it % 10 == 0 }
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

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(UnstableApi::class)
    private fun setupSlider() = with(binding) {
        seekBar.max = totalDurationInMs.toInt()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("Video current posit ->", progress.toString())
                videoPlayer?.seekTo(progress.toLong())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })



        videoSlider.valueFrom = lastMinValue.toFloat()
        videoSlider.valueTo = totalDurationInMs.toFloat()
        videoSlider.addOnChangeListener { slider, value, fromUser ->
            Log.d("Video total Duration ->", value.toString())
            videoPlayer?.seekTo(value.toLong())
        }
    }
}


