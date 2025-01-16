package com.example.pokedex.data

import com.example.pokedex.dataClasses.DamageRelations
import com.example.pokedex.dataClasses.EvolutionChain
import com.example.pokedex.dataClasses.FlavorTextAndEvolutionChain
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonAPIService {
    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int, @Query("offset") offset: Int): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): Pokemon

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): FlavorTextAndEvolutionChain

    @GET("type/{type}")
    suspend fun getTypeInfo(@Path("type") type: String): DamageRelations

    @GET("evolution-chain/{id}")
    suspend fun getEvoChain(@Path("id") id: Int): EvolutionChain
}