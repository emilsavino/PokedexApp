package com.example.pokedex.shared

import com.example.pokedex.R

data class Pokemon(
    val id: Int = 0,
    var name: String = "",
    var sprites: Sprites = Sprites(""),
    val types: List<TypeObject> = emptyList(),
    val abilities: List<Ability> = emptyList()
)

fun Pokemon.getSprite(): Comparable<*> {
    return this.sprites.front_default ?: R.drawable.unknown
}

fun String.formatPokemonName (): String {
    return this.split("-").joinToString (" "){ word -> word.replaceFirstChar { it.uppercase() } }
}