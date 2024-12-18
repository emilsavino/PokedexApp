package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import retrofit2.http.GET

interface PokemonAPIService {
    @GET("pokemon")
    suspend fun getPokemons(): List<Pokemon>

    @GET("pokemon/{name}")
    suspend fun getPokemon(name: String): Pokemon
}