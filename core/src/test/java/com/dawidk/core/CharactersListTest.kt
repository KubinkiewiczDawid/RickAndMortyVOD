package com.dawidk.core

import CharactersListQuery
import com.apollographql.apollo.api.toInput
import com.dawidk.common.characterUtils.FilterCharacterUtil
import com.dawidk.core.executors.FetchCharactersListExecutor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import type.FilterCharacter

private const val CHARACTERS_LIST_PAGE_SUCCESS_JSON_FILE_NAME = "/characters_list_page_success.json"
private const val CHARACTERS_LIST_FILTER_SUCCESS_JSON_FILE_NAME =
    "/characters_list_filter_success.json"

internal class CharactersListTest {

    private val rickAndMortyAPI = mockk<RickAndMortyAPI>(relaxed = true)
    private val fetchCharactersListExecutor = FetchCharactersListExecutor(rickAndMortyAPI)

    @Test
    fun `should return characters list for given page when standard query response is received`() {
        //given
        val response = CharactersListQuery(page = 1.toInput()).parse(
            FileUtils.getBufferedSource(CHARACTERS_LIST_PAGE_SUCCESS_JSON_FILE_NAME)
        ).data?.characters?.results


        coEvery { rickAndMortyAPI.getCharactersList(page = 1) } returns response
        //when
        val character = runBlocking {
            fetchCharactersListExecutor.getCharactersList(page = 1)
        }
        //then
        Assertions.assertNotNull(character)
        Assertions.assertEquals("1", character[0].id)
        Assertions.assertEquals("Rick Sanchez", character[0].name)

    }

    @Test
    fun `should return emptyList when query response for given page is null`() {
        //given
        val response = null
        coEvery { rickAndMortyAPI.getCharactersList(page = 1) } returns response
        // when
        val characters = runBlocking {
            fetchCharactersListExecutor.getCharactersList(page = 1)
        }
        //then
        Assertions.assertEquals(emptyList<Character>(), characters)

    }

    @Test
    fun `should return characters list for given filter when standard query response is received`() {
        //given
        val response = CharactersListQuery(
            filter = FilterCharacter(
                name = "Rick".toInput(),
                status = "alive".toInput()
            ).toInput()
        ).parse(
            FileUtils.getBufferedSource(CHARACTERS_LIST_FILTER_SUCCESS_JSON_FILE_NAME)
        ).data?.characters?.results


        coEvery {
            rickAndMortyAPI.getCharactersList(
                filter = FilterCharacter(
                    name = "Rick".toInput(),
                    status = "alive".toInput()
                )
            )
        } returns response
        //when
        val character = runBlocking {
            fetchCharactersListExecutor.getCharactersList(
                filter = FilterCharacterUtil(
                    name = "Rick",
                    status = "alive"
                )
            )
        }
        //then
        Assertions.assertNotNull(character)
        Assertions.assertEquals("1", character[0].id)
        Assertions.assertEquals("Rick Sanchez", character[0].name)

    }

    @Test
    fun `should return emptyList when query response for given filter is null`() {
        //given
        val response = null
        coEvery {
            rickAndMortyAPI.getCharactersList(
                filter = FilterCharacter(
                    name = "Rick".toInput(),
                    status = "alive".toInput()
                )
            )
        } returns response
        // when
        val characters = runBlocking {
            fetchCharactersListExecutor.getCharactersList(
                filter = FilterCharacterUtil(
                    name = "Rick",
                    status = "alive"
                )
            )
        }
        //then
        Assertions.assertEquals(emptyList<Character>(), characters)

    }

}