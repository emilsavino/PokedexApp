package com.example.pokedex.shared

data class Ability(
    val name: String,
    val url: String
)

data class Type(
    val name: String,
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

data class PokemonList (
    val results: List<Result>
)

data class Result (
    val name: String,
    val url: String
)

data class Species(
    val url: String
)

data class EvolutionChain (
    val chain: Chain
)

data class Chain (
    val species: SpeciesDetails,
    val evolves_to: List<Chain>
)

data class SpeciesDetails (
    val name: String
)

data class PokemonSpecies(
    val flavor_text_entries: List<FlavorTextEntry>
)

data class FlavorTextEntry(
    val flavor_text: String,
    val language: Language,
    val version: Version
)

data class Language(val name: String)
data class Version(val name: String)