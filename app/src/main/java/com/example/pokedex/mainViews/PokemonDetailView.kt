package com.example.pokedex.mainViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.shared.Pokemon

@Composable
fun PokemonDetailView(pokemonName: String) {
    val viewModel = PokemonDetailViewModel()
    val pokemon = viewModel.getPokemonByName(pokemonName)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.size(500.dp)
        ) {
            AsyncImage(
                model = pokemon.imageURL,
                contentDescription = "Picture of a Pokemon",
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            modifier = Modifier
                .padding(10.dp)
                .align(CenterHorizontally),
            text = pokemon.name,
            fontSize = 20.sp,
        )
    }
}

class PokemonDetailViewModel {
    val pokemonRepository = PokemonRepository()

    fun getPokemonByName(pokemonName: String): Pokemon {
        return pokemonRepository.getPokemonByName(pokemonName)
    }
}

