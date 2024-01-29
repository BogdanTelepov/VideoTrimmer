package kg.dev.videoeditor

import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

interface VideoEditorDelegate {
    fun setupPlayer(videoPlayer: ExoPlayer, playerView: PlayerView, fragment: Fragment)
}

