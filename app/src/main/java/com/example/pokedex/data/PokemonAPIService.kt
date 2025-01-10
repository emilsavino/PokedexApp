package com.example.pokedex.data

import com.example.pokedex.shared.DamageRelations
import com.example.pokedex.shared.FlavorTextAndEvolutionChain
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Species
import com.example.pokedex.shared.VarietiesPokemon
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PokemonAPIService {
    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int, @Query("offset") offset: Int): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): Pokemon

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): FlavorTextAndEvolutionChain

    @GET("type/{type}")
    suspend fun getTypeInfo(@Path("type") type: String): DamageRelations

    @GET
    suspend fun getPokemonSpeciesFromURL(@Url url: String): Species

    @GET
    suspend fun getVarPokemonFromURL(@Url url: String): VarietiesPokemon
}