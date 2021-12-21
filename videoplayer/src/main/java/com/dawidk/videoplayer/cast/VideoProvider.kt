package com.dawidk.videoplayer.cast

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.dawidk.common.video.MediaItem
import kotlinx.coroutines.*
import org.json.JSONException
import java.io.*
import java.net.URL
import java.util.*

class VideoProvider {

    // extension function to get string data from url
    private fun URL.getString(): String {
        val stream = openStream()
        return try {
            val r = BufferedReader(InputStreamReader(stream))
            val result = StringBuilder()
            var line: String?
            while (r.readLine().also { line = it } != null) {
                result.append(line).appendLine()
            }
            result.toString()
        } catch (e: IOException) {
            e.toString()
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
                    // ignore
                }
            }
        }
    }

    suspend fun parseUrl(urlString: String?): JsonObject = withContext(Dispatchers.Default) {
        var url: URL? = null
        kotlin.runCatching {
            url = URL(urlString)
        }
        val jsonString = url?.getString()
        return@withContext JsonParser().parse(jsonString).asJsonObject
    }

    companion object {

        private const val TAG = "VideoProvider"
        private const val TAG_VIDEOS = "videos"
        private const val TAG_HLS = "hls"
        private const val TAG_DASH = "dash"
        private const val TAG_MP4 = "mp4"
        private const val TAG_IMAGES = "images"
        private const val TAG_VIDEO_TYPE = "type"
        private const val TAG_VIDEO_URL = "url"
        private const val TAG_VIDEO_MIME = "mime"
        private const val TAG_CATEGORIES = "categories"
        private const val TAG_NAME = "name"
        private const val TAG_STUDIO = "studio"
        private const val TAG_SOURCES = "sources"
        private const val TAG_SUBTITLE = "subtitle"
        private const val TAG_DURATION = "duration"
        private const val TAG_THUMB = "image-480x270" // "thumb";
        private const val TAG_IMG_780_1200 = "image-780x1200"
        private const val TAG_TITLE = "title"
        const val KEY_DESCRIPTION = "description"
        private const val TARGET_FORMAT = TAG_MP4
        private var mediaList: MutableList<MediaItem> = emptyList<MediaItem>().toMutableList()

        @Throws(JSONException::class)
        suspend fun buildMedia(url: String?): List<MediaItem> {
            if (mediaList.isNotEmpty()) {
                return mediaList
            }
            val urlPrefixMap: MutableMap<String, String> = HashMap()
            mediaList = ArrayList<MediaItem>()
            val jsonObj = VideoProvider().parseUrl(url)
            val categories = jsonObj.getAsJsonArray(TAG_CATEGORIES)
            for (i in 0 until categories.size()) {
                val category = categories.get(i).asJsonObject
                urlPrefixMap[TAG_HLS] = category.get(TAG_HLS).asString
                urlPrefixMap[TAG_DASH] = category.get(TAG_DASH).asString
                urlPrefixMap[TAG_MP4] = category.get(TAG_MP4).asString
                urlPrefixMap[TAG_IMAGES] = category.get(TAG_IMAGES).asString
                category.get(TAG_NAME).asString
                val videos = category.getAsJsonArray(TAG_VIDEOS)
                for (j in 0 until videos.size()) {
                    var videoUrl: String? = null
                    var mimeType: String? = null
                    val video = videos.get(j).asJsonObject
                    val subTitle = video.get(TAG_SUBTITLE).asString
                    val videoSpecs = video.getAsJsonArray(TAG_SOURCES)
                    if (videoSpecs.size() == 0) {
                        continue
                    }
                    for (k in 0 until videoSpecs.size()) {
                        val videoSpec = videoSpecs.get(k).asJsonObject
                        val videoType = videoSpec.get(TAG_VIDEO_TYPE).asString
                        if (TARGET_FORMAT == videoType) {
                            videoUrl = urlPrefixMap[TARGET_FORMAT].toString() + videoSpec
                                .get(TAG_VIDEO_URL).asString
                            mimeType = videoSpec.get(TAG_VIDEO_MIME).asString
                        }
                    }
                    if (videoUrl == null) {
                        continue
                    }
                    val imageUrl = urlPrefixMap[TAG_IMAGES].toString() + video.get(
                        TAG_THUMB
                    ).asString
                    val bigImageUrl = urlPrefixMap[TAG_IMAGES].toString() + video
                        .get(TAG_IMG_780_1200).asString
                    val title = video.get(TAG_TITLE).asString
                    val studio = video.get(TAG_STUDIO).asString
                    val duration = video.get(TAG_DURATION).asLong
                    mediaList.add(
                        buildMediaInfo(
                            title, studio, subTitle, duration, videoUrl,
                            mimeType, imageUrl, bigImageUrl
                        )
                    )
                }
            }
            return mediaList
        }

        private fun buildMediaInfo(
            title: String, studio: String, subTitle: String,
            duration: Long, url: String, mimeType: String?, imgUrl: String, bigImageUrl: String
        ): MediaItem {
            val media = MediaItem()
            media.url = url
            media.title = title
            media.subTitle = subTitle
            media.studio = studio
            media.images.add(imgUrl)
            media.images.add(bigImageUrl)
            media.contentType = mimeType
            media.duration = duration
            return media
        }
    }
}