package kg.dev.videoeditor

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeThumbnailViewModel : ViewModel() {

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap get() = _bitmap.asStateFlow()

    fun loadThumbNails(retriever: MediaMetadataRetriever, uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {

            retriever.setDataSource(context, uri)
            val duration =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                    ?: 0L

            var currentTime = 1000L
            while (currentTime < duration) {
                val frame = retriever.getFrameAtTime(
                    currentTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )

                withContext(Dispatchers.Main) {
                    frame.let {
                        _bitmap.value = it
                    }
                }
                currentTime += 1000
            }
            retriever.release()
        }
    }

}