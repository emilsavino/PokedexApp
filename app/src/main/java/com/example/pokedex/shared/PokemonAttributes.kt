package com.example.pokedex.shared

data class PokemonAttributes(
    val pokemon: Pokemon,
    val description: FlavorTextEntry,
    val types: Types,
    val weaknesses: DamageRelationsResult,
    val abilities: List<Ability>,
    val sprites: List<Sprites>
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
    val ability: AbilityDetails
)

data class AbilityDetails(
    val name: String
)

//Evolutions and its data classes
data class EvolutionChain(
    val chain: EvolutionChainResult
)

data class EvolutionChainResult(
    val species: Species,
    val evolves_to: List<EvolutionChainResult> = emptyList()
)

data class EvolutionChainUrlFromSpecies(
    val url: String
)

data class Species(
    val name: String
)

data class Varieties(
    val varieties: List<VarietiesResult>
)

data class VarietiesResult(
    val is_default: Boolean,
    val pokemon: VarietiesPokemon
)

data class VarietiesPokemon(
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
    val evolution_chain: EvolutionChainUrlFromSpecies,
    val flavor_text_entries: List<FlavorTextEntry>
)


