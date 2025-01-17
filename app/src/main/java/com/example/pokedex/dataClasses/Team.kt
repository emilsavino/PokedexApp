package com.example.pokedex.dataClasses

import com.example.pokedex.dependencyContainer.DependencyContainer

data class Team (
    val name: String,
    private var pokemons: MutableList<Pokemon> = emptyList<Pokemon>().toMutableList(),
    val weakAgainst: MutableList<String> = emptyList<String>().toMutableList(),
    val strongAgainst: MutableList<String> = emptyList<String>().toMutableList()
){
    fun addPokemon(pokemon: Pokemon)
    {
        pokemons.add(pokemon)
    }

    fun removePokemon(pokemon: Pokemon)
    {
        pokemons.remove(pokemon)
    }

    fun getPokemons(): List<Pokemon>
    {
        return pokemons
    }

    private suspend fun updateWeakAndStrongness()
    {
        var map : HashMap<String,Int> = HashMap()
        strongAgainst.clear()
        weakAgainst.clear()

        for (pokemon in pokemons)
        {
            val types = pokemon.types.map { it.type }
            val damageRelations = DependencyContainer.pokemonDataStore.fetchTypeInfo(types)
            for (damageRelation in damageRelations)
            {
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

        val tempList = map.toList()
        tempList.sortedBy { it.second }
        print(tempList)
    }

}