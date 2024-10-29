package com.example.pokedex.shared

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pokedex.R
import java.util.jar.Attributes.Name

@Composable
fun PokemonGridItem(modifier: Modifier = Modifier, pokemon: Pokemon) {
    Column()
        {
            AsyncImage(
                model = pokemon.imageURL,
                contentDescription = "Picture of a Pokemon",
                modifier = modifier.fillMaxSize()
            )
        }

        }

@Preview(showBackground = true)
@Composable
fun PokemonGridItemPreview() {
    val testPokemon = Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png")
    PokemonGridItem(pokemon = testPokemon)
}
