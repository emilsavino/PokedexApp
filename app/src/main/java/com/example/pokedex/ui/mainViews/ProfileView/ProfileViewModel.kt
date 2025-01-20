import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.ui.navigation.Screen
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val googleAuthManager = DependencyContainer.googleAuthenticationManager
    private val connectivityRepository = DependencyContainer.connectivityRepository
    var showNoInternetAlert by mutableStateOf(false)

    var email = mutableStateOf("Guest")
    var profilePictureUrl = mutableStateOf<String?>(null)
    var authError = mutableStateOf<String?>(null)

    init {
        initializeUserState()
    }

    private fun initializeUserState() {
        viewModelScope.launch {
            val currentUser = googleAuthManager.auth.currentUser
            email.value = currentUser?.email ?: "Unknown User"
            profilePictureUrl.value = currentUser?.photoUrl?.toString()

        }
    }

    fun signOut(navController: NavController) {
        viewModelScope.launch {
            googleAuthManager.auth.signOut()
            navController.navigate(Screen.SignIn.route) {
                popUpTo(Screen.Profile.route) {
                    inclusive = true
                }
            }
        }
    }

    fun deleteAccount(navController: NavController) {
        if (connectivityRepository.isConnected.asLiveData().value == false) {
            showNoInternetAlert = true
        }
        viewModelScope.launch {
            googleAuthManager.auth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Profile.route) {
                            inclusive = true
                        }
                    }
                } else {
                    authError.value = task.exception?.message ?: "Failed to delete account"
                }
            }
        }
    }

}
