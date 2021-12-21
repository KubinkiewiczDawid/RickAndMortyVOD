package com.dawidk.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object VideoStateSerializer: Serializer<VideoState> {
    override val defaultValue: VideoState
        get() = VideoState.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): VideoState {
        try {
            return VideoState.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: VideoState, output: OutputStream) = t.writeTo(output)
}