package com.dawidk.settings.model

sealed class Setting {
    data class Title(val title: String) : Setting()
    data class Info(val description: String) : Setting()
    data class Button(val text: String, val onClick: () -> Unit) : Setting()
}