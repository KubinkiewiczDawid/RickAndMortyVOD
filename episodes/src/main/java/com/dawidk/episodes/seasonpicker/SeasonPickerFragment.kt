package com.dawidk.episodes.seasonpicker

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dawidk.common.binding.viewBinding
import com.dawidk.episodes.R
import com.dawidk.episodes.databinding.SeasonPickerFragmentBinding

const val SELECTED_SEASON = "selected_season"
const val SEASONS_LIST = "seasons_list"
const val UPDATE_FUNCTION = "update_function"

class SeasonPickerFragment : Fragment(R.layout.season_picker_fragment) {

    private val binding by viewBinding(SeasonPickerFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seasonsList =
            requireArguments().getStringArrayList(SEASONS_LIST)?.toList() ?: emptyList()
        val selectedSeason = requireArguments().getString(SELECTED_SEASON) ?: ""
        val onSeasonSelected =
            requireArguments().getSerializable(UPDATE_FUNCTION) as (seasonId: String) -> Unit
        val spinnerAdapter = SeasonsAdapter(seasonsList, selectedSeason, SeasonOnClickListener {
            onSeasonSelected(it)
            parentFragmentManager.popBackStack()
        })

        binding.seasonsList.apply {
            adapter = spinnerAdapter
            val dividerItemDecorator =
                DividerItemDecorator(context.getDrawable(R.drawable.line_divider)!!)
            addItemDecoration(dividerItemDecorator)
        }
    }
}

class DividerItemDecorator(private val mDivider: Drawable) :
    RecyclerView.ItemDecoration() {

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop = child.top + params.topMargin
            val dividerBottom = dividerTop + mDivider.intrinsicHeight
            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            mDivider.draw(canvas)
        }
    }
}