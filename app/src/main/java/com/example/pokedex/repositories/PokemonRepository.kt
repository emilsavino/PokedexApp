package com.example.pokedex.repositories

import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Abilities
import com.example.pokedex.shared.Ability
import com.example.pokedex.shared.DamageRelations
import com.example.pokedex.shared.DamageRelationsResult
import com.example.pokedex.shared.EvolutionChain
import com.example.pokedex.shared.FlavorTextEntry
import com.example.pokedex.shared.Language
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonAttributes
import com.example.pokedex.shared.Type
import com.example.pokedex.shared.TypeObject
import com.example.pokedex.shared.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonRepository {
    private var dataStore = DependencyContainer.pokemonDataStore

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    val filterOptions = mutableListOf("fire","grass","ASAP-Rocky")
    val sortOptions = mutableListOf("NameASC","NameDSC")

    private val mutableSearchFlow = MutableSharedFlow<List<Pokemon>>()
    val searchFlow: Flow<List<Pokemon>> = mutableSearchFlow.asSharedFlow()

    private val mutablePokemonAttributesFlow = MutableSharedFlow<PokemonAttributes>()
    val pokemonAttributesFlow: Flow<PokemonAttributes> = mutablePokemonAttributesFlow.asSharedFlow()

    suspend fun searchPokemonByNameAndFilterWithSort(name : String, offset : Int, filterOptions : List<String>, sortOption : String)
    {
        var foundElements = 0
        val elementsToFind = 20
        var mutableFilteredList = mutableListOf<Pokemon>()
        var index = offset
        var allPokemonResults = dataStore.getAllPokemonResults()
        if (sortOption == "NameASC")
        {
            allPokemonResults = allPokemonResults.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }).toMutableList()
        }
        else if (sortOption == "NameDSC")
        {
            allPokemonResults = allPokemonResults.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }).toMutableList()
            allPokemonResults = allPokemonResults.reversed()
        }

        while (index < allPokemonResults.size && foundElements < elementsToFind)
        {
            val result = allPokemonResults.get(index)
            if (result.name.contains(name, ignoreCase = true))
            {
                var pokemon = dataStore.getPokemonFromMapFallBackAPIPlaygroundClassFeature(result.name)
                var typeRelevant = false
                for (type in filterOptions)
                {
                    for (innerType in pokemon.types)
                    {
                        if (type == innerType.type.name)
                        {
                            typeRelevant = true
                            break
                        }
                        if (typeRelevant)
                        {
                            break
                        }
                    }
                }
                if (filterOptions.isEmpty())
                {
                    typeRelevant = true
                }

                if (!typeRelevant)
                {
                    index++
                    continue;
                }

                mutableFilteredList.add(pokemon)
                foundElements++
            }
            index++
        }
        mutableSearchFlow.emit(mutableFilteredList)
    }

    suspend fun getPokemonByName(name: String) {
        var pokemon = dataStore.getPokemonFromMapFallBackAPIPlaygroundClassFeature(name)
        mutablePokemonFlow.emit(pokemon)
    }

    suspend fun getPokemonDetailsByName(name: String) {
        val pokemon = dataStore.getPokemonFromMapFallBackAPIPlaygroundClassFeature(name)
        val pokemonSpecies = dataStore.fetchPokemonSpecies(name)

        val types = pokemon.types.map { it.type }
        val typesInfoList = dataStore.fetchTypeInfo(types)

        val weaknesses = combineDamageRelations(typesInfoList)

        val abilities = Abilities(
            abilities = pokemon.abilities
                .filter { it.name != null }
                .map { Ability(it.name ?: "Unknown Ability") }
        )

        val description = pokemonSpecies.flavor_text_entries.firstOrNull() {
            it.language.name == "en"
        } ?: FlavorTextEntry("This Pokemon doesnt have many sightnings", Language("en"))

        val evolutionChain = EvolutionChain(
            url = pokemonSpecies.evolution_chain.url
        )

        val pokemonAttributes = PokemonAttributes(
            pokemon = pokemon,
            description = description,
            types = Types(types),
            weaknesses = weaknesses,
            abilities = abilities,
            evolution_chain = evolutionChain
        )

        mutablePokemonAttributesFlow.emit(pokemonAttributes)
    }

    private fun combineDamageRelations(typeInfoList: List<DamageRelations>): DamageRelationsResult {
        val combinedWeaknesses = mutableListOf<Type>()
        for (typeInfo in typeInfoList) {
            val halfDamageFrom = typeInfo.damage_relations.half_damage_from
            for (type in typeInfo.damage_relations.double_damage_from) {
                if (!combinedWeaknesses.contains(type) && !halfDamageFrom.contains(type)) {
                    combinedWeaknesses.add(type)
                }
            }
        }

        return DamageRelationsResult(
            double_damage_from = combinedWeaknesses,
            half_damage_from = emptyList()
        )
    }
}