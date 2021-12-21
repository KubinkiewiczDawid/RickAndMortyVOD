package com.dawidk.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserCredentialsSerializer : Serializer<UserCredentials> {

    override val defaultValue: UserCredentials
        get() = UserCredentials.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserCredentials {
        try {
            return UserCredentials.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserCredentials, output: OutputStream) = t.writeTo(output)
}