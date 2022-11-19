package com.dawidk.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dawidk.settings.databinding.SettingTypeButtonItemBinding
import com.dawidk.settings.databinding.SettingTypeInfoItemBinding
import com.dawidk.settings.databinding.SettingTypeTitleItemBinding
import com.dawidk.settings.model.Setting
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

private const val SETTING_TYPE_TITLE = 0
private const val SETTING_TYPE_INFO = 1
private const val SETTING_TYPE_BUTTON = 2

class SettingsAdapter :
    ListAdapter<Setting, RecyclerView.ViewHolder>(
        SettingsDiffUtilCallback()
    ) {

    private val _clickEvent: MutableSharedFlow<Setting> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val clickEvent: SharedFlow<Setting> = _clickEvent

    private class SettingsDiffUtilCallback : DiffUtil.ItemCallback<Setting>() {

        override fun areItemsTheSame(oldItem: Setting, newItem: Setting): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Setting, newItem: Setting): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SETTING_TYPE_TITLE -> SettingTypeTitleViewHolder(
                SettingTypeTitleItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            SETTING_TYPE_INFO -> SettingTypeInfoViewHolder(
                SettingTypeInfoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            SETTING_TYPE_BUTTON -> SettingTypeButtonViewHolder(
                SettingTypeButtonItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid type of data")
        }
    }

    class SettingTypeTitleViewHolder(private val binding: SettingTypeTitleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun applyItem(title: Setting.Title) {
            binding.settingsItemTitle.text = title.title
        }
    }

    class SettingTypeInfoViewHolder(private val binding: SettingTypeInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun applyItem(info: Setting.Info) {
            binding.settingItemInfoDescription.text = info.description
        }
    }

    class SettingTypeButtonViewHolder(private val binding: SettingTypeButtonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun applyItem(button: Setting.Button) {
            binding.settingsItemButton.text = button.text
            binding.settingsItemButton.setOnClickListener {
                button.onClick()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            getItem(position).let {
                _clickEvent.tryEmit(
                    it
                )
            }
        }
        when (getItemViewType(position)) {
            SETTING_TYPE_TITLE -> (holder as SettingTypeTitleViewHolder).applyItem(
                getItem(position) as Setting.Title
            )
            SETTING_TYPE_INFO -> (holder as SettingTypeInfoViewHolder).applyItem(
                getItem(position) as Setting.Info
            )
            SETTING_TYPE_BUTTON -> (holder as SettingTypeButtonViewHolder).applyItem(
                getItem(position) as Setting.Button
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Setting.Title -> SETTING_TYPE_TITLE
            is Setting.Info -> SETTING_TYPE_INFO
            is Setting.Button -> SETTING_TYPE_BUTTON
        }
    }
}