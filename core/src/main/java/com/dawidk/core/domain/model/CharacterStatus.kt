package com.dawidk.core.domain.model

enum class CharacterStatus(val value: String) {
    ALIVE("alive"),
    DEAD("dead"),
    UNKNOWN("unknown");

    companion object {

        fun fromStatus(status: String?): CharacterStatus = values().firstOrNull {
            it.value == status?.lowercase()
        } ?: UNKNOWN
    }
}