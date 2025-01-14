package com.example.pokedex.shared

import com.example.pokedex.R

data class Pokemon(
    val id: Int,
    var name: String,
    var sprites: Sprites,
    val types: List<TypeObject>,
    val abilities: List<Ability>
)

fun Pokemon.getSprite(): Comparable<*> {
    return this.sprites.front_default ?: R.drawable.unknown
}

fun String.formatPokemonName (): String {
    return this.split("-").joinToString (" "){ word -> word.replaceFirstChar { it.uppercase() } }
}