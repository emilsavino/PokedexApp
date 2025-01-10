package com.example.pokedex.shared

data class PokemonAttributes(
    val description: FlavorTextEntry,
    val types: Types,
    val weaknesses: DamageRelationsResult,
    val abilities: Abilities,
    val evolution_chain: EvolutionChain
)

//Description and its data classes
data class FlavorTextEntry(
    val flavor_text: String,
    val language: Language
)

data class Language(
    val name: String
)

//Types and its data classes
data class Types(
    val types: List<Type>
)
data class Type(
    val name: String
)

//Weaknesses and its data classes
data class DamageRelationsResult(
    val double_damage_from: List<Type>,
    val half_damage_from: List<Type>
)

data class DamageRelations(
    val damage_relations: DamageRelationsResult
)

//Abilities and its data classes
data class Abilities(
    val abilities: List<Ability>
)

data class Ability(
    val name: String
)

//Evolutions and its data classes
data class EvolutionChain(
    val url: String
)

//------------MISC.----------------
data class TypeObject(
    val type: Type
)

data class Sprites(
    val front_default: String
)

data class PokemonList (
    val results: List<Result>
)

data class Result (
    val name: String,
    val url: String
)

data class FlavorTextAndEvolutionChain(
    val evolution_chain: EvolutionChain,
    val flavor_text_entries: List<FlavorTextEntry>
)


