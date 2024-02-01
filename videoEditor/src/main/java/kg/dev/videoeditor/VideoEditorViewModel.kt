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

class VideoEditorViewModel : ViewModel() {


    private val _frameArray: MutableStateFlow<List<Bitmap>> = MutableStateFlow(emptyList())
    val frameArray get() = _frameArray.asStateFlow()

    fun loadThumbNails(retriever: MediaMetadataRetriever, uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val array = mutableListOf<Bitmap>()
            retriever.setDataSource(context, uri)
            val duration =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                    ?: 0L

            var currentTime = 1000L
            while (currentTime < duration) {
                val frame = retriever.getFrameAtTime(
                    currentTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                frame?.let {
                    array.add(it)
                }
                currentTime += 1000
            }
            withContext(Dispatchers.Main) {
                _frameArray.value = array
            }
        }
    }

}