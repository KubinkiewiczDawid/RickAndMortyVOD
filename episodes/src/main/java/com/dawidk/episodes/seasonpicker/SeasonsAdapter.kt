package com.dawidk.episodes.seasonpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dawidk.episodes.R
import com.dawidk.episodes.databinding.SeasonItemBinding
import com.dawidk.episodes.utils.mapToPrettySeasonName

class SeasonOnClickListener(val clickListener: (seasonName: String) -> Unit) {

    fun onClick(seasonName: String) = clickListener(seasonName)
}

class SeasonsAdapter(
    private val seasonsList: List<String>,
    private val selectedSeason: String,
    private val clickListener: SeasonOnClickListener,
) : RecyclerView.Adapter<SeasonsAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    class ViewHolder(private val binding: SeasonItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun applyItem(seasonName: String, selectedSeason: String) {
            if (seasonName == selectedSeason) {
                binding.seasonName.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.check_icon,
                    0
                )
            }
            if (seasonName == context.resources.getString(R.string.all)) {
                binding.seasonName.text = context.resources.getString(R.string.all_seasons)
            } else {
                binding.seasonName.text = seasonName.mapToPrettySeasonName(context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SeasonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.applyItem(seasonsList[position], selectedSeason)
        holder.itemView.setOnClickListener {
            seasonsList[position].let { clickListener.onClick(it) }
        }
    }

    override fun getItemCount(): Int {
        return seasonsList.size
    }
}