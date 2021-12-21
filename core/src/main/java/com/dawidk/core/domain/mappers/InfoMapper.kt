package com.dawidk.core.domain.mappers

import com.dawidk.core.domain.model.Info
import fragment.InfoDetails

fun InfoDetails.mapToDomainModel() = Info(
    count = count ?: 0,
    pages = pages ?: 0,
    next = next,
    prev = prev
)