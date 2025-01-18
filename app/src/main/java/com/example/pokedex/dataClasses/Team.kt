package com.example.pokedex.dataClasses

import com.example.pokedex.dependencyContainer.DependencyContainer
import kotlinx.coroutines.runBlocking

data class Team (
    val name: String,
    private var pokemons: MutableList<Pokemon> = emptyList<Pokemon>().toMutableList(),
    val weakAgainst: MutableList<String> = emptyList<String>().toMutableList(),
    val strongAgainst: MutableList<String> = emptyList<String>().toMutableList(),
    var combinedHP: Int = 0
){
    fun addPokemon(pokemon: Pokemon)
    {
        pokemons.add(pokemon)
        updateWeakAndStrongness()
        updateCombinedHP()
    }

    fun removePokemon(pokemon: Pokemon)
    {
        pokemons.remove(pokemon)
        updateWeakAndStrongness()
        updateCombinedHP()
    }

    fun getPokemons(): List<Pokemon>
    {
        return pokemons
    }

    private fun updateWeakAndStrongness() = runBlocking {
        val map : HashMap<String,Int> = HashMap()
        strongAgainst.clear()
        weakAgainst.clear()

        for (pokemon in pokemons)
        {
            val types = pokemon.types.map { it.type }
            val damageRelations = DependencyContainer.pokemonDataStore.fetchTypeInfo(types)
            for (damageRelation in damageRelations)
            {
                for (strong in damageRelation.damage_relations.double_damage_to)
                {
                    if (map.containsKey(strong.name))
                    {
                        map[strong.name] = map[strong.name]!! + 1
                    }
                    else
                    {
                        map[strong.name] = 1
                    }
                }
                for (strong in damageRelation.damage_relations.half_damage_from)
                {
                    if (map.containsKey(strong.name))
                    {
                        map[strong.name] = map[strong.name]!! + 1
                    }
                    else
                    {
                        map[strong.name] = 1
                    }
                }
                for (weak in damageRelation.damage_relations.half_damage_to)
                {
                    if (map.containsKey(weak.name))
                    {
                        map[weak.name] = map[weak.name]!! - 1
                    }
                    else
                    {
                        map[weak.name] = -1
                    }
                }
                for (weak in damageRelation.damage_relations.double_damage_from)
                {
                    if (map.containsKey(weak.name))
                    {
                        map[weak.name] = map[weak.name]!! - 1
                    }
                    else
                    {
                        map[weak.name] = -1
                    }
                }
            }
        }

        var tempList = map.toList()
        tempList = tempList.sortedBy { it.second }
        var index = 0
        for (value in tempList)
        {
            if (index == 2 || value.second > -1)
            {
                break
            }
            weakAgainst.add(value.first)
            index++
        }
        index = 0
        for (value in tempList.reversed())
        {
            if (index == 2 || value.second < 1)
            {
                break
            }
            strongAgainst.add(value.first)
            index++
        }
        println(tempList)
        println(weakAgainst)
        println(strongAgainst)
    }

    private fun updateCombinedHP()
    {
        combinedHP = 0
        for (pokemon in pokemons)
        {
            combinedHP += pokemon.stats[0].base_stat
        }

    }
}