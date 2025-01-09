package com.example.pokedex.data

import com.example.pokedex.shared.EvolutionChain
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.PokemonSpecies
import com.example.pokedex.shared.Weaknesses
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonAPIService {
    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int, @Query("offset") offset: Int): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): Pokemon

    @GET("type/{type}")
    suspend fun getWeakness(@Path("type") type: String): Weaknesses

    @GET("pokemon-species/{name}")
    suspend fun getPokemonDesc(@Path("name") name: String): PokemonSpecies

    @GET("evolution-chain/{id}")
    suspend fun getEvoChain(@Path("id") id: Int): EvolutionChain
}