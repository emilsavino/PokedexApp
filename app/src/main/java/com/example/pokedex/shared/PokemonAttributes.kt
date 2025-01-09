package com.example.pokedex.shared

data class PokemonAttributes(
    val description: FlavorTextEntry,
    val types: Types,
    val weaknesses: DamageRelations,
    val abilities: Abilities,
    val evolutions: Evolutions
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
data class DamageRelations(
    val double_damage_from: List<Type>
)

//Abilities and its data classes
data class Abilities(
    val abilities: List<Ability>
)

data class Ability(
    val name: String
)

//Evolutions and its data classes
data class Evolutions(
    val evolutions: String
)

data class Chain(
    val species: Species,
)

data class Species(
    val name: String
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


