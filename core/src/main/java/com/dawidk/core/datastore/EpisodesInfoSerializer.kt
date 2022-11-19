package com.dawidk.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object EpisodesInfoSerializer : Serializer<EpisodesInfo> {

    override val defaultValue: EpisodesInfo
        get() = EpisodesInfo.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): EpisodesInfo {
        try {
            return EpisodesInfo.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: EpisodesInfo, output: OutputStream) = t.writeTo(output)
}