package com.dawidk.characters.charactersHome

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dawidk.characters.charactersHome.CharacterHomeAdapter.CharacterHomeViewHolder
import com.dawidk.characters.databinding.CharacterHomeItemBinding
import com.dawidk.characters.utils.capitalizeString
import com.dawidk.common.constants.CHARACTER_IMAGE_MEDIUM
import com.dawidk.common.utils.fetchBitmapImage
import com.dawidk.core.domain.model.Character
import com.dawidk.core.domain.model.CharacterStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CharacterHomeAdapter :
    PagingDataAdapter<Character, CharacterHomeViewHolder>(CharacterDiffUtilCallback()) {

    private val _clickEvent: MutableSharedFlow<Character> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val clickEvent: SharedFlow<Character> = _clickEvent

    private class CharacterDiffUtilCallback : DiffUtil.ItemCallback<Character>() {

        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterHomeViewHolder {
        return CharacterHomeViewHolder(
            CharacterHomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CharacterHomeViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            getItem(position)?.let { _clickEvent.tryEmit(it) }
        }
        getItem(position)?.let { holder.applyItem(it) }
    }

    override fun onViewRecycled(holder: CharacterHomeViewHolder) {
        super.onViewRecycled(holder)
        holder.scope.cancel()
    }

    inner class CharacterHomeViewHolder(binding: CharacterHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val characterName: TextView =
            binding.characterNameAndStatusLayout.nameTextView
        private val characterImage: ImageView = binding.characterImageView
        private val status: TextView = binding.characterNameAndStatusLayout.statusTextView
        private val location: TextView = binding.characterLocationLayout.locationTextView
        private val firstSeen: TextView = binding.characterFirstSeenLayout.firstSeenTextView
        private val statusImage: ImageView =
            binding.characterNameAndStatusLayout.statusCircleImageView
        lateinit var scope: CoroutineScope

        fun applyItem(characterItem: Character) {
            scope = CoroutineScope(Dispatchers.Main)
            val statusAndSpecies =
                "${characterItem.status.value.capitalizeString()} - ${characterItem.species}"
            characterName.apply {
                text = characterItem.name
            }
            status.text = statusAndSpecies
            location.text = characterItem.location.name
            firstSeen.text = characterItem.episode.firstOrNull()?.name

            scope.launch {
                val image = fetchBitmapImage(
                    characterImage.context,
                    characterItem.image,
                    CHARACTER_IMAGE_MEDIUM
                )
                characterImage.load(image)
            }
            val statusColor = if (characterItem.status == CharacterStatus.ALIVE) {
                Color.GREEN
            } else if (characterItem.status == CharacterStatus.DEAD) {
                Color.RED
            } else {
                Color.GRAY
            }
            statusImage.setColorFilter(statusColor, PorterDuff.Mode.SRC_ATOP)
        }
    }
}




