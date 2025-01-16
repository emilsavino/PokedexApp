package com.example.pokedex.dataClasses

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.pokedex.R
import com.example.pokedex.dependencyContainer.DependencyContainer
import kotlinx.coroutines.runBlocking


class PokemonTypeResources {
    data class TypeResource(val drawableRes: Int, val color: Color)
    val pokemonOfTheDayRepository = DependencyContainer.pokemonOfTheDayRepository

    private val typeResources: Map<String, TypeResource> = mapOf(
        "bug" to TypeResource(R.drawable.bug, Color(0xFFB0D700)),
        "dark" to TypeResource(R.drawable.dark, Color(0xFF3A3A3A)),
        "dragon" to TypeResource(R.drawable.dragon, Color(0xFF6F35FC)),
        "electric" to TypeResource(R.drawable.electric, Color(0xFFF7D02C)),
        "fairy" to TypeResource(R.drawable.fairy, Color(0xFFFDB9E9)),
        "fighting" to TypeResource(R.drawable.fighting, Color(0xFFC22E28)),
        "fire" to TypeResource(R.drawable.fire, Color(0xFFF08030)),
        "flying" to TypeResource(R.drawable.flying, Color(0xFFA98FF3)),
        "ghost" to TypeResource(R.drawable.ghost, Color(0xFF5D3583)),
        "grass" to TypeResource(R.drawable.grass, Color(0xFF7AC74C)),
        "ground" to TypeResource(R.drawable.ground, Color(0xFFECC164)),
        "ice" to TypeResource(R.drawable.ice, Color(0xFF98D8D8)),
        "normal" to TypeResource(R.drawable.normal, Color(0xFFA8A77A)),
        "poison" to TypeResource(R.drawable.poison, Color(0xFF960CC0)),
        "psychic" to TypeResource(R.drawable.psychic, Color(0xFFF85888)),
        "rock" to TypeResource(R.drawable.rock, Color(0xFF8D7D2A)),
        "steel" to TypeResource(R.drawable.steel, Color(0xFFB7B7CE)),
        "water" to TypeResource(R.drawable.water, Color(0xFF6390F0))
    )

    @Composable
    fun getTypeImage(type: String): Painter {
        val resource = typeResources[type]?.drawableRes ?: R.drawable.unknown
        return painterResource(id = resource)
    }

    fun getTypeGradient(type: String): Brush {
        val typeColor = getTypeColor(type)
        return Brush.linearGradient(
            colors = listOf(typeColor, Color.White),
            start = Offset(0f, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY)
        )
    }

    fun getTypeColor(type: String): Color {
        return typeResources[type]?.color ?: Color.Gray
    }

    fun appGradient(): Brush {
        val pokemonOfTheDay = runBlocking {  pokemonOfTheDayRepository.determinePokemonOfTheDay() }
        val primaryType = pokemonOfTheDay?.types?.firstOrNull()?.type?.name ?: "normal"
        return getTypeGradient(primaryType)
    }

    fun getAllTypes(): List<String> {
        return typeResources.keys.toList()
    }
}