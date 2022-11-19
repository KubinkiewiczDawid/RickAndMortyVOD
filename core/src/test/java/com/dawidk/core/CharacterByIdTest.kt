package com.dawidk.core

import CharacterByIdQuery
import com.dawidk.core.executors.FetchCharacterByIdExecutor
import com.dawidk.core.utils.DataLoadingException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

private const val CHARACTER_BY_ID_SUCCESS_JSON_FILE_NAME = "/character_by_id_success.json"

internal class CharacterByIdTest {

    private val rickAndMortyAPI = mockk<RickAndMortyAPI>(relaxed = true)
    private val fetchCharacterByIdExecutor = FetchCharacterByIdExecutor(rickAndMortyAPI)

    @Test
    fun `should return character when standard query response is received`() {
        //given
        val response = CharacterByIdQuery("1").parse(
            FileUtils.getBufferedSource(CHARACTER_BY_ID_SUCCESS_JSON_FILE_NAME)
        ).data?.character


        coEvery { rickAndMortyAPI.getCharacterById("1") } returns response
        //when
        val character = runBlocking {
            fetchCharacterByIdExecutor.getCharacterById("1")
        }
        //then
        assertNotNull(character)
        assertEquals("1", character.id)
        assertEquals("Rick Sanchez", character.name)

    }

    @Test
    fun `should throw exception when query response is null`() {
        //given
        val response = null
        coEvery { rickAndMortyAPI.getCharacterById("1") } returns response
        //then
        assertThrows(
            DataLoadingException::class.java
        ) {
            runBlocking { fetchCharacterByIdExecutor.getCharacterById("1") }
        }

    }

}
