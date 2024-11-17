package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.WhoIsThatPokemon

class MockPokemonDataStore {
    private val pokemons = listOf(
        Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png"),
        Pokemon("Charmander", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        Pokemon("Squirtle", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        Pokemon("Bulbasaur", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        Pokemon("Jigglypuff", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png"),
        Pokemon("Meowth", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/52.png"),
        Pokemon("Psyduck", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/54.png"),
        Pokemon("Snorlax", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/143.png"),
        Pokemon("Mewtwo", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/150.png"),
        Pokemon("Mew", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/151.png"),
        Pokemon("Chikorita", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/152.png"),
        Pokemon("Cyndaquil", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/155.png"),
        Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png"),
        Pokemon("Charmander", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        Pokemon("Squirtle", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        Pokemon("Bulbasaur", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        Pokemon("Jigglypuff", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png"),
        Pokemon("Meowth", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/52.png"),
        Pokemon("Psyduck", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/54.png"),
        Pokemon("Snorlax", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/143.png"),
        Pokemon("Mewtwo", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/150.png"),
        Pokemon("Mew", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/151.png"),
        Pokemon("Chikorita", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/152.png"),
        Pokemon("Cyndaquil", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/155.png"),
        Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png"),
        Pokemon("Charmander", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        Pokemon("Squirtle", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        Pokemon("Bulbasaur", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        Pokemon("Jigglypuff", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png"),
        Pokemon("Meowth", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/52.png"),
        Pokemon("Psyduck", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/54.png"),
        Pokemon("Snorlax", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/143.png"),
        Pokemon("Mewtwo", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/150.png"),
        Pokemon("Mew", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/151.png"),
        Pokemon("Chikorita", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/152.png"),
        Pokemon("Cyndaquil", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/155.png"),
        Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png"),
        Pokemon("Charmander", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        Pokemon("Squirtle", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        Pokemon("Bulbasaur", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        Pokemon("Jigglypuff", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png"),
        Pokemon("Meowth", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/52.png"),
        Pokemon("Psyduck", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/54.png"),
        Pokemon("Snorlax", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/143.png"),
        Pokemon("Mewtwo", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/150.png"),
        Pokemon("Mew", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/151.png"),
        Pokemon("Chikorita", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/152.png"),
        Pokemon("Cyndaquil", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/155.png"),
        Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png"),
        Pokemon("Charmander", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
        Pokemon("Squirtle", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"),
        Pokemon("Bulbasaur", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
        Pokemon("Jigglypuff", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png"),
        Pokemon("Meowth", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/52.png"),
        Pokemon("Psyduck", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/54.png"),
        Pokemon("Snorlax", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/143.png"),
        Pokemon("Mewtwo", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/150.png"),
        Pokemon("Mew", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/151.png"),
        Pokemon("Chikorita", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/152.png"),
        Pokemon("Cyndaquil", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/155.png")
    )

    private val teams = listOf(
        listOf(pokemons[0], pokemons[1], pokemons[2], pokemons[3], pokemons[4], pokemons[5]),
        listOf(pokemons[3], pokemons[4], pokemons[5], pokemons[6], pokemons[7], pokemons[8]),
        listOf(pokemons[6], pokemons[7], pokemons[8], pokemons[9], pokemons[10], pokemons[11]),
    )

    fun fetchPokemonByName(name: String): Pokemon? {
        return pokemons.find { it.name == name }
    }

    suspend fun fetchPokemons(): List<Pokemon> {
        return pokemons
    }

    suspend fun fetchTeams(): List<List<Pokemon>> {
        return teams
    }

    suspend fun fetchSavedPokemons(): List<Pokemon>
    {
        return pokemons
    }

    private val whoIsThatPokemon = WhoIsThatPokemon(Pokemon("Ditto", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png")
        , listOf("Pikachu", "Charmander", "Squirtle")
    )
    fun fetchWhoIsThatPokemon(): WhoIsThatPokemon
    {
        return whoIsThatPokemon
    }
}