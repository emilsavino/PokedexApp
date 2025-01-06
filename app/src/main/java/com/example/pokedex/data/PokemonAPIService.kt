package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonAPIService {
    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int, @Query("offset") offset: Int): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): Pokemon
}