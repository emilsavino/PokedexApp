package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonAPIService {
    @GET("pokemon?limit=100000&offset=0")
    suspend fun getPokemons(): List<Pokemon>

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): Pokemon
}