package com.example.pokedex.repositories

import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Ability
import com.example.pokedex.shared.AbilityDetails
import com.example.pokedex.shared.DamageRelations
import com.example.pokedex.shared.DamageRelationsResult
import com.example.pokedex.shared.EvolutionChainResult
import com.example.pokedex.shared.FlavorTextEntry
import com.example.pokedex.shared.Language
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonAttributes
import com.example.pokedex.shared.Type
import com.example.pokedex.shared.Types
import com.example.pokedex.shared.getSprite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonRepository {
    private var dataStore = DependencyContainer.pokemonDataStore

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()

    private val mutablePokemonAttributesFlow = MutableSharedFlow<PokemonAttributes>()
    val pokemonAttributesFlow: Flow<PokemonAttributes> = mutablePokemonAttributesFlow.asSharedFlow()

    suspend fun getPokemonByName(name: String) {
        val pokemon = dataStore.getPokemonFromMapFallBackAPI(name)
        mutablePokemonFlow.emit(pokemon)
    }

    suspend fun getPokemonDetailsByName(name: String) {
        val pokemon = dataStore.getPokemonFromMapFallBackAPI(name)
        val types = pokemon.types.map { it.type }
        val typesInfoList = dataStore.fetchTypeInfo(types)
        val weaknesses = combineDamageRelations(typesInfoList, types)
        val abilities = fetchPokemonAbilities(pokemon)
        var description = fetchPokemonDescription(name)
        val evolutionChainPokemons = fetchEvolutionChainPokemons(name)

        if (pokemon.getSprite() == null) {
            description = FlavorTextEntry("This Pokémon is so rare, that no photos have been taken!",
                Language("en"))
        }

        val pokemonAttributes = PokemonAttributes(
            pokemon = pokemon,
            description = description,
            types = Types(types),
            weaknesses = weaknesses,
            abilities = abilities,
            pokemons = evolutionChainPokemons
        )

        mutablePokemonAttributesFlow.emit(pokemonAttributes)
    }

    private fun combineDamageRelations(typeInfoList: List<DamageRelations>, ownTypeList: List<Type>): DamageRelationsResult {
        val combinedWeaknesses = mutableListOf<Type>()
        for (typeInfo in typeInfoList) {
            val halfDamageFrom = typeInfo.damage_relations.half_damage_from
            for (type in typeInfo.damage_relations.double_damage_from) {
                if (!combinedWeaknesses.contains(type) &&
                    !halfDamageFrom.contains(type) &&
                    !ownTypeList.contains(type)) {
                    combinedWeaknesses.add(type)
                }
            }
        }
        return DamageRelationsResult(
            double_damage_from = combinedWeaknesses,
            half_damage_from = emptyList()
        )
    }

    private fun fetchPokemonAbilities(pokemon: Pokemon): List<Ability> {
        return pokemon.abilities.map { Ability(AbilityDetails(it.ability.name)) }
    }

    private suspend fun fetchPokemonDescription(name: String): FlavorTextEntry {
        val fallbackEntry = FlavorTextEntry("We do not have much knowledge of this mysterious Pokémon!", Language("en"))
        return try {
            val pokemonSpecies = dataStore.fetchPokemonSpecies(name)
            pokemonSpecies.flavor_text_entries.firstOrNull {
                it.language.name == "en"
            } ?: fallbackEntry
        } catch (e: Exception) {
            fallbackEntry
        }
    }

    private suspend fun fetchEvolutionChainPokemons(name: String): List<Pokemon> {
        return try {
            val evolutionPokemonList = mutableListOf<Pokemon>()

            val pokemonEvoChainUrl = dataStore.fetchPokemonSpecies(name)
            val getEvoChainID = pokemonEvoChainUrl.evolution_chain.url
            val id = getEvoChainID
                .substringAfter("evolution-chain/")
                .substringBefore("/")
                .toInt()

            val evoChainResult = dataStore.fetchNameFromEvoChain(id)

            fun extractPokemonNames(chain: EvolutionChainResult): List<String> {
                val names = mutableListOf(chain.species.name)
                for (evolution in chain.evolves_to) {
                    names.addAll(extractPokemonNames(evolution))
                }
                return names
            }

            val pokemonNames = extractPokemonNames(evoChainResult.chain)

            for (pokemonName in pokemonNames) {
                val getPokemon = dataStore.getPokemonFromMapFallBackAPI(pokemonName)
                evolutionPokemonList.add(getPokemon)
            }

            evolutionPokemonList
        } catch (e: Exception) {
            emptyList()
        }
    }
}