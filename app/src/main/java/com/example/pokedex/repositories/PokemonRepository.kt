package com.example.pokedex.repositories

import com.example.pokedex.R
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Ability
import com.example.pokedex.dataClasses.AbilityDetails
import com.example.pokedex.dataClasses.DamageRelations
import com.example.pokedex.dataClasses.DamageRelationsResult
import com.example.pokedex.dataClasses.EvolutionChainResult
import com.example.pokedex.dataClasses.FlavorTextEntry
import com.example.pokedex.dataClasses.Language
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.PokemonAttributes
import com.example.pokedex.dataClasses.Type
import com.example.pokedex.dataClasses.Types
import com.example.pokedex.dataClasses.getSprite
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

        if (pokemon.getSprite() == R.drawable.unknown) {
            description = FlavorTextEntry("This Pokémon is so rare, that no photos have been taken!",
                Language("en")
            )
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
            half_damage_from = emptyList(),
            double_damage_to = emptyList(),
            half_damage_to = emptyList()
        )
    }

    private fun fetchPokemonAbilities(pokemon: Pokemon): List<Ability> {
        return pokemon.abilities.map { Ability(AbilityDetails(it.ability.name), it.is_hidden) }
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