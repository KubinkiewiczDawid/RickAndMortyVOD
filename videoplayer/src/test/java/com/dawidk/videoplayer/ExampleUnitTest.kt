package com.dawidk.videoplayer

import com.google.gson.JsonParser
import com.dawidk.videoplayer.cast.VideoProvider
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

private const val GOOGLE_CAST_VIDEOS_JSON_FILE_NAME = "google_cast_videos.json"

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testUrlParser() = runBlocking {
        val url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/f.json"
        val videoProvider = VideoProvider()
        val jsonStringFromFile =
            FileUtil.readFileWithNewLineFromResources(GOOGLE_CAST_VIDEOS_JSON_FILE_NAME)
        val response = JsonParser().parse(jsonStringFromFile).asJsonObject
        val videoProviderJson = videoProvider.parseUrl(url)
        assertEquals(response, videoProviderJson)
    }

    @Test
    fun testMediaBuilder() {
        runBlocking {
            val url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/f.json"
            val mediaItemsListCorrectSize = 17
            val mediaItemsList = VideoProvider.buildMedia(url).first()

            assertEquals(mediaItemsListCorrectSize, mediaItemsList.size)
        }
    }

    object FileUtil {

        @Throws(IOException::class)
        fun readFileWithNewLineFromResources(fileName: String): String {
            var inputStream: InputStream? = null
            try {
                inputStream =
                    javaClass.classLoader?.getResourceAsStream(fileName)
                val builder = StringBuilder()
                val reader = BufferedReader(InputStreamReader(inputStream))
                var theCharNum = reader.read()
                while (theCharNum != -1) {
                    builder.append(theCharNum.toChar())
                    theCharNum = reader.read()
                }

                return builder.toString()
            } finally {
                inputStream?.close()
            }
        }
    }
}