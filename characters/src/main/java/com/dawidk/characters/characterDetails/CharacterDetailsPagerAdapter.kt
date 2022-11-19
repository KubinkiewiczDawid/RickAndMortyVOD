package com.dawidk.characters.characterDetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class CharacterDetailsPagerAdapter(characterDetailsFragment: Fragment) :
    FragmentStateAdapter(characterDetailsFragment) {

    private var tabs: List<Fragment> = emptyList()

    fun setTabs(items: List<Fragment>) {
        this.tabs = items
    }

    override fun createFragment(position: Int): Fragment {
        return when {
            position < itemCount -> tabs[position]
            else -> tabs.first()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}