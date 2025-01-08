package com.example.pokedex.shared

data class Pokemon(
    var name: String,
    var sprites: Sprites,
    val abilities: List<AbilityObject>,
    val types: List<TypeObject>
)


fun String.FormatPokemonName (): String {
    return this.split("-")
        .joinToString (" "){ word -> word.replaceFirstChar { it.uppercase() } }
}