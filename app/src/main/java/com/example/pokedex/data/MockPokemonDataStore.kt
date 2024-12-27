package com.example.pokedex.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.WhoIsThatPokemon
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pokemon_preferences")

class MockPokemonDataStore(private val context: Context) {
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

    private val favouritePokemons = mutableListOf<Pokemon>()
    private val FAVOURITE_POKEMONS_KEY = stringPreferencesKey("favourite_pokemons")
    private val gson = Gson()

    private val teams = listOf(
        listOf(pokemons[0], pokemons[1], pokemons[2], pokemons[3], pokemons[4], pokemons[5]),
        listOf(pokemons[3], pokemons[4], pokemons[5], pokemons[6], pokemons[7], pokemons[8]),
        listOf(pokemons[6], pokemons[7], pokemons[8], pokemons[9], pokemons[10], pokemons[11]),
    )

    suspend fun initializeCache() {
        val savedPokemons = fetchSavedPokemonsFromDataStore()
        favouritePokemons.clear()
        favouritePokemons.addAll(savedPokemons)
    }

    fun fetchPokemonByName(name: String): Pokemon? {
        return pokemons.find { it.name == name }
    }

    suspend fun fetchPokemons(): List<Pokemon> {
        return pokemons
    }

    suspend fun fetchTeams(): List<List<Pokemon>> {
        return teams
    }

    fun fetchSavedPokemons(): List<Pokemon> {
        return favouritePokemons
    }

    private val whoIsThatPokemon = WhoIsThatPokemon(Pokemon("Ditto", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png")
        , listOf(Option("Pikachu", Color.Black), Option("Charmander", Color.Black), Option("Squirtle", Color.Black), Option("Ditto", Color.Black))
    )

    suspend fun fetchWhoIsThatPokemon(): WhoIsThatPokemon
    {
        return whoIsThatPokemon
    }

    suspend fun searchPokemonByName(name: String): List<Pokemon> {
        return pokemons.filter { it.name.contains(name, ignoreCase = true) }
    }

    suspend fun savePokemon(pokemon: Pokemon) {
        if (!favouritePokemons.contains(pokemon)) {
            // Add to cache
            favouritePokemons.add(pokemon)
            // Update DataStore
            updateDataStore()
        }
    }

    suspend fun removeFromFavourites(pokemon: Pokemon) {
        if (favouritePokemons.contains(pokemon)) {
            // Remove from cache
            favouritePokemons.remove(pokemon)
            // Update DataStore
            updateDataStore()
        }
    }

    fun pokemonIsFavourite(pokemon: Pokemon): Boolean {
        return favouritePokemons.contains(pokemon)
    }

    private suspend fun updateDataStore() {
        val pokemonJson = gson.toJson(favouritePokemons)
        context.dataStore.edit { preferences ->
            preferences[FAVOURITE_POKEMONS_KEY] = pokemonJson
        }
    }

    // Fetch saved Pokémon from DataStore (used during initialization)
    private suspend fun fetchSavedPokemonsFromDataStore(): List<Pokemon> {
        val preferences = context.dataStore.data.first()
        val pokemonJson = preferences[FAVOURITE_POKEMONS_KEY] ?: "[]"
        return gson.fromJson(pokemonJson, Array<Pokemon>::class.java).toList()
    }
}
