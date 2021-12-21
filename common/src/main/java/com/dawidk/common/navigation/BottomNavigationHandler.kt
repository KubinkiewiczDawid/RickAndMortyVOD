package com.dawidk.common.navigation

import androidx.navigation.NavController
import java.util.Stack

class BottomNavigationHandler(
    private val navController: NavController,
    private val primaryFragmentId: Int,
    private val primaryNavBarPositionId: Int
) {

    private val tabIdsStack: Stack<Int> = Stack()

    fun handleBackButton(
        changeTab: (tabId: Int) -> Unit,
        applicationFinish: () -> Unit,
    ) {
        if (navController.backQueue.last().destination.id != primaryFragmentId) {
            if (navController.currentDestination?.parent?.startDestinationId == navController.currentDestination?.id) {
                tabIdsStack.pop()
                changeTab(tabIdsStack.pop())
            } else {
                navController.popBackStack()
            }
        } else {
            applicationFinish()
        }
    }

    fun handleDestinationTabChange(tabId: Int) {
        if (tabIdsStack.empty()) tabIdsStack.push(primaryNavBarPositionId)
        if (tabIdsStack.contains(tabId))
            tabIdsStack.remove(tabId)
        tabIdsStack.push(tabId)
    }
}