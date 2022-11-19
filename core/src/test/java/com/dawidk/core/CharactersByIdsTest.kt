package com.dawidk.core

import CharactersByIdsQuery
import com.dawidk.core.domain.model.Character
import com.dawidk.core.executors.FetchCharactersByIdsExecutor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

private const val CHARACTERS_BY_IDS_SUCCESS_JSON_FILE_NAME = "/characters_by_ids_success.json"

internal class CharactersByIdsTest {

    private val rickAndMortyAPI = mockk<RickAndMortyAPI>(relaxed = true)
    private val fetchCharactersByIdsExecutor = FetchCharactersByIdsExecutor(rickAndMortyAPI)

    @Test
    fun `should return characters list when standard query response is received`() {
        //given
        val response = CharactersByIdsQuery(listOf("1", "2", "3")).parse(
            FileUtils.getBufferedSource(CHARACTERS_BY_IDS_SUCCESS_JSON_FILE_NAME)
        ).data?.charactersByIds


        coEvery { rickAndMortyAPI.getCharactersByIds(listOf("1", "2", "3")) } returns response
        //when
        val characters = runBlocking {
            fetchCharactersByIdsExecutor.getCharactersByIds(listOf("1", "2", "3"))
        }
        //then
        assertNotNull(characters)
        assertEquals(3, characters.size)
        assertEquals("Rick Sanchez", characters[0].name)

    }

    @Test
    fun `should return emptyList when query response is null`() {
        //given
        val response = null
        coEvery { rickAndMortyAPI.getCharactersByIds(listOf("1", "2", "3")) } returns response
        // when
        val characters = runBlocking {
            fetchCharactersByIdsExecutor.getCharactersByIds(listOf("1", "2", "3"))
        }
        //then
        assertEquals(emptyList<Character>(), characters)

    }


}
