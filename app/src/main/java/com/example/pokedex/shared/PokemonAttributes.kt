package com.example.pokedex.shared

data class Ability(
    val name: String,
    val url: String
)

data class Type(
    val name: String,
    val url: String
)

data class Sprites(
    val front_default: String
)

data class AbilityObject(
    val ability: Ability
)

data class TypeObject(
    val type: Type
)
