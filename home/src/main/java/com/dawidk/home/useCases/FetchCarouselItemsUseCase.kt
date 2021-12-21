package com.dawidk.home.useCases

import com.dawidk.core.datastore.HomeScreenDataStoreRepository
import com.dawidk.core.datastore.HomeScreenItem
import com.dawidk.core.domain.model.Character
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.domain.model.Location
import com.dawidk.core.executors.*
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.home.model.CarouselItem
import com.dawidk.home.model.CarouselItems
import com.dawidk.home.utils.mapToCarouselItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val CAROUSEL_COUNT = 1
private const val NUMBER_OF_CHARACTERS = 2
private const val NUMBER_OF_LOCATIONS = 2
private const val NUMBER_OF_EPISODES = 1
private const val TOTAL_ITEMS_NUMBER =
    NUMBER_OF_CHARACTERS + NUMBER_OF_LOCATIONS + NUMBER_OF_EPISODES
private const val CHARACTER = 0
private const val LOCATION = 1
private const val EPISODE = 2

class FetchCarouselItemsUseCase(
    private val fetchCharacterByIdExecutor: FetchCharacterByIdExecutor,
    private val fetchCharactersListInfoExecutor: FetchCharactersListInfoExecutor,
    private val fetchLocationByIdExecutor: FetchLocationByIdExecutor,
    private val fetchLocationsListInfoExecutor: FetchLocationsListInfoExecutor,
    private val fetchEpisodeByIdExecutor: FetchEpisodeByIdExecutor,
    private val fetchEpisodesListInfoExecutor: FetchEpisodesListInfoExecutor,
    private val homeScreenDataStoreRepository: HomeScreenDataStoreRepository
) {

    suspend operator fun invoke(): Flow<List<CarouselItems>> = flow {
        homeScreenDataStoreRepository.homeScreenFlow.collect {
            val carousels = emptyList<CarouselItems>().toMutableList()
            it.carouselList.forEachIndexed { i, carousel ->
                val items: MutableList<CarouselItem> = emptyList<CarouselItem>().toMutableList()
                carousel.itemsList.forEach { item ->
                    when (item.type) {
                        HomeScreenItem.ItemType.CHARACTER -> {
                            items += fetchCharacterById(item.itemId).mapToCarouselItem()
                        }
                        HomeScreenItem.ItemType.EPISODE -> {
                            items += fetchEpisodeById(item.itemId).mapToCarouselItem()
                        }
                        else -> items += fetchLocationById(item.itemId).mapToCarouselItem()
                    }
                }
                carousels += CarouselItems(name = i.toString(), items = items)
            }

            if (it.lastSeenCharacterId.isNotBlank()) {
                carousels.map { carouselItems ->
                    val itemsList = carouselItems.items.toMutableList()
                    itemsList.apply {
                        if (size > TOTAL_ITEMS_NUMBER)
                            removeAt(0)
                        add(0, fetchCharacterById(it.lastSeenCharacterId).mapToCarouselItem())
                    }
                    carouselItems.items = itemsList.toList()
                }
            }
            emit(carousels)
        }
    }

    suspend fun generateCarousels() {
        for (i in 1..CAROUSEL_COUNT) {
            getCarouselItems()
        }
    }

    private suspend fun getCarouselItems(): MutableList<CarouselItem> {
        val carouselItems = emptyList<CarouselItem>().toMutableList()
        val items = emptyList<Pair<String, HomeScreenItem.ItemType>>().toMutableList()
        val numberOfCharacters = fetchCharactersListInfoExecutor.getCharactersListInfo().count
        val numberOfLocations = fetchLocationsListInfoExecutor.getLocationsListInfo().count
        val numberOfEpisodes = fetchEpisodesListInfoExecutor.getEpisodesListInfo().count

        addCarouselItems(items, numberOfCharacters, NUMBER_OF_CHARACTERS, CHARACTER)
        addCarouselItems(items, numberOfLocations, NUMBER_OF_LOCATIONS, LOCATION)
        addCarouselItems(items, numberOfEpisodes, NUMBER_OF_EPISODES, EPISODE)

        return carouselItems
    }

    private suspend fun addCarouselItems(
        carouselItems: MutableList<Pair<String, HomeScreenItem.ItemType>>,
        numberOfItems: Int,
        numberOfItemsToGenerate: Int,
        itemType: Int,
    ) {
        val randomNumbers = List(numberOfItemsToGenerate) {
            Random.nextInt(0, numberOfItems - 1).toString()
        }

        repeat(numberOfItemsToGenerate) { index ->
            carouselItems.apply {
                add(
                    when (itemType) {
                        CHARACTER -> Pair(randomNumbers[index], HomeScreenItem.ItemType.CHARACTER)
                        LOCATION -> Pair(randomNumbers[index], HomeScreenItem.ItemType.LOCATION)
                        else -> Pair(randomNumbers[index], HomeScreenItem.ItemType.EPISODE)
                    }
                )
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            homeScreenDataStoreRepository.updateCarousel(items = carouselItems.shuffled())
        }
    }

    private suspend fun fetchCharacterById(id: String): Character {
        return try {
            fetchCharacterByIdExecutor.getCharacterById(id)
        } catch (ex: DataLoadingException) {
            Character.EMPTY
        }
    }

    private suspend fun fetchLocationById(id: String): Location {
        return try {
            fetchLocationByIdExecutor.getLocationById(id)
        } catch (ex: DataLoadingException) {
            Location.EMPTY
        }
    }

    private suspend fun fetchEpisodeById(id: String): Episode {
        return try {
            fetchEpisodeByIdExecutor.getEpisodeById(id)
        } catch (ex: DataLoadingException) {
            Episode.EMPTY
        }
    }
}