package com.dawidk.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object HomeScreenSerializer : Serializer<HomeScreenContents> {

    override val defaultValue: HomeScreenContents = HomeScreenContents.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): HomeScreenContents {
        try {
            return HomeScreenContents.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: HomeScreenContents, output: OutputStream) = t.writeTo(output)
}
