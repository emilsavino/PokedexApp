package com.example.pokedex.manager

import android.content.Context
import com.example.pokedex.dependencyContainer.DependencyContainer
import java.io.File

class CacheManager(private val context: Context) {

    suspend fun clearAllCache() {
        DependencyContainer.favouritesRepository.clearAllCache()
        DependencyContainer.recentlySearchedRepository.clearAllCache()
        DependencyContainer.recentlyViewedRepository.clearAllCache()
        DependencyContainer.teamsRepository.clearAllCache()
    }
}
