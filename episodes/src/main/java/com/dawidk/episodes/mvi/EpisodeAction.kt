package com.dawidk.episodes.mvi

import com.dawidk.common.mvi.ViewAction
import com.dawidk.core.domain.model.EpisodeFilter

sealed class EpisodeAction: ViewAction {
    object Init : EpisodeAction()
    data class Update(val episodeFilter: EpisodeFilter) : EpisodeAction()
    object Load : EpisodeAction()
    object DataLoaded : EpisodeAction()
    data class HandleError(val error: Throwable) : EpisodeAction()
    data class NavigateToEpisodeDetailsScreen(val id: String) : EpisodeAction()
    object NavigateToSeasonsList : EpisodeAction()
    data class NavigateToVideoPlayerScreen(val episodeId: String) : EpisodeAction()
}