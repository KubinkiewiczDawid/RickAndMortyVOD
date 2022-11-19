package com.dawidk.core

import LocationByIdQuery
import com.dawidk.core.executors.FetchLocationByIdExecutor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private const val LOCATION_BY_ID_SUCCESS_JSON_FILE_NAME = "/location_by_id_success.json"

internal class LocationByIdTest {

    private val rickAndMortyAPI = mockk<RickAndMortyAPI>(relaxed = true)
    private val fetchLocationByIdExecutor = FetchLocationByIdExecutor(rickAndMortyAPI)

    @Test
    fun `should return location when standard query response is received`() {
        //given
        val response = LocationByIdQuery("1").parse(
            FileUtils.getBufferedSource(LOCATION_BY_ID_SUCCESS_JSON_FILE_NAME)
        ).data?.location

        coEvery { rickAndMortyAPI.getLocationById("1") } returns response
        //when
        val location = runBlocking {
            fetchLocationByIdExecutor.getLocationById("1")
        }
        //then
        Assertions.assertNotNull(location)
        Assertions.assertEquals("1", location.id)
        Assertions.assertEquals("Earth (C-137)", location.name)

    }

}