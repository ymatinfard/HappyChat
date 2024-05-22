
import android.media.MediaMetadataRetriever

object MediaUtils {
    fun getDuration(filePath: String?): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)
        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()

        return durationString?.toLong() ?: -1 // Error or unknown duration
    }
}

