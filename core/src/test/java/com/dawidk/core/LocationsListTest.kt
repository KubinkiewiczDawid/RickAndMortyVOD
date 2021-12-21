package com.dawidk.core

import LocationsListQuery
import android.location.Location
import com.apollographql.apollo.api.toInput
import com.dawidk.common.locationUtils.FilterLocationUtil
import com.dawidk.core.executors.FetchLocationsListExecutor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import type.FilterLocation

private const val LOCATIONS_LIST_PAGE_SUCCESS_JSON_FILE_NAME = "/locations_list_page_success.json"
private const val LOCATIONS_LIST_FILTER_SUCCESS_JSON_FILE_NAME =
    "/locations_list_filter_success.json"

internal class LocationsListTest {

    private val rickAndMortyAPI = mockk<RickAndMortyAPI>(relaxed = true)
    private val fetchLocationsListExecutor = FetchLocationsListExecutor(rickAndMortyAPI)

    @Test
    fun `should return locations list for given page when standard query response is received`() {
        //given
        val response = LocationsListQuery(page = 1.toInput()).parse(
            FileUtils.getBufferedSource(LOCATIONS_LIST_PAGE_SUCCESS_JSON_FILE_NAME)
        ).data?.locations?.results


        coEvery { rickAndMortyAPI.getLocationsList(page = 1) } returns response
        //when
        val location = runBlocking {
            fetchLocationsListExecutor.getLocationsList(page = 1)
        }
        //then
        Assertions.assertNotNull(location)
        Assertions.assertEquals("1", location[0].id)
        Assertions.assertEquals("Earth (C-137)", location[0].name)

    }

    @Test
    fun `should return emptyList when query response for given page is null`() {
        //given
        val response = null
        coEvery { rickAndMortyAPI.getLocationsList(page = 1) } returns response
        // when
        val locations = runBlocking {
            fetchLocationsListExecutor.getLocationsList(page = 1)
        }
        //then
        Assertions.assertEquals(emptyList<Location>(), locations)

    }

    @Test
    fun `should return locations list for given filter when standard query response is received`() {
        //given
        val response =
            LocationsListQuery(filter = FilterLocation(type = "Spacecraft".toInput()).toInput()).parse(
                FileUtils.getBufferedSource(LOCATIONS_LIST_FILTER_SUCCESS_JSON_FILE_NAME)
            ).data?.locations?.results


        coEvery { rickAndMortyAPI.getLocationsList(filter = FilterLocation(type = "Spacecraft".toInput())) } returns response
        //when
        val location = runBlocking {
            fetchLocationsListExecutor.getLocationsList(filter = FilterLocationUtil(type = "Spacecraft"))
        }
        //then
        Assertions.assertNotNull(location)
        Assertions.assertEquals("54", location[0].id)
        Assertions.assertEquals("Vindicator's Base", location[0].name)

    }

    @Test
    fun `should return emptyList when query response for given filter is null`() {
        //given
        val response = null
        coEvery { rickAndMortyAPI.getLocationsList(filter = FilterLocation(type = "Aircraft".toInput())) } returns response
        // when
        val locations = runBlocking {
            fetchLocationsListExecutor.getLocationsList(filter = FilterLocationUtil(type = "Aircraft"))
        }
        //then
        Assertions.assertEquals(emptyList<Location>(), locations)

    }

    @Test
    fun `should return first page of locations`() {
        //given
        val response = LocationsListQuery(page = null.toInput()).parse(
            FileUtils.getBufferedSource(LOCATIONS_LIST_PAGE_SUCCESS_JSON_FILE_NAME)
        ).data?.locations?.results

        coEvery { rickAndMortyAPI.getLocationsList(page = null) } returns response
        // when
        val location = runBlocking {
            fetchLocationsListExecutor.getLocationsList(page = null)
        }
        //then
        Assertions.assertNotNull(location)
        Assertions.assertEquals("1", location[0].id)
        Assertions.assertEquals("Earth (C-137)", location[0].name)

    }

}