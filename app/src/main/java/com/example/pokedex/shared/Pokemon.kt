package com.example.pokedex.shared

data class Pokemon(
    val id: Int,
    var name: String,
    var sprites: Sprites,
    val types: List<TypeObject>,
    val abilities: List<Ability>
)


fun String.formatPokemonName (): String {
    return this.split("-").joinToString (" "){ word -> word.replaceFirstChar { it.uppercase() } }
}