package com.mytiktok.app.simpleclasses

import android.content.Context
import android.net.Uri
import com.mytiktok.app.compressionmodule.CompressionListener
import com.mytiktok.app.compressionmodule.VideoCompressor
import com.mytiktok.app.compressionmodule.VideoQuality
import com.mytiktok.app.compressionmodule.config.Configuration
import com.mytiktok.app.compressionmodule.config.SharedStorageConfiguration

object CompressionHelper {

    public fun processVideo(context: Context, uris:List<Uri>,listener: CompressionListener) {
        VideoCompressor.start(
                context,
                uris,
                isStreamable = false,
                sharedStorageConfiguration = SharedStorageConfiguration(
                        subFolderName = "${(Variables.APP_HIDED_RESULT_FOLDER).replace("/","")}"
                ),
                configureWith = Configuration(
                        quality = VideoQuality.MEDIUM,
                        videoNames = uris.map { uri -> uri.pathSegments.last() },
                        isMinBitrateCheckEnabled = true,
                ),
                listener = listener,
        )
    }
}