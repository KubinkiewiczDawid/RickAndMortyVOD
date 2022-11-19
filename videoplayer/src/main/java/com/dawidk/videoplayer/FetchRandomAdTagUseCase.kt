package com.dawidk.videoplayer

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private const val AD_TAGS_JSON_FILENAME = "ad_tags.json"

class FetchRandomAdTagUseCase(private val context: Context) {

    operator fun invoke(): String {
        val adTags = getAdTagsList(context) ?: emptyList()
        val adTagsSize = adTags.size
        val index = (0 until adTagsSize).random()

        return adTags[index].uri
    }

    private fun getAdTagsList(context: Context): List<AdTag>? {
        val jsonFileString = context.getJsonDataFromAsset(AD_TAGS_JSON_FILENAME)
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val listType = Types.newParameterizedType(List::class.java, AdTag::class.java)
        val adapter: JsonAdapter<List<AdTag>> = moshi.adapter(listType)

        return adapter.fromJson(jsonFileString)
    }
}