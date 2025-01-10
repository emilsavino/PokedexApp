import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.navigation.Screen
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val googleAuthManager = DependencyContainer.googleAuthenticationManager

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
            resetUserState()
        }
    }

    fun deleteAccount(navController: NavController) {
        viewModelScope.launch {
            googleAuthManager.auth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Profile.route) {
                            inclusive = true
                        }
                    }
                    resetUserState()
                } else {
                    authError.value = task.exception?.message ?: "Failed to delete account"
                }
            }
        }
    }

    private fun resetUserState() {
        email.value = "Guest"
        profilePictureUrl.value = null
        authError.value = null
    }
}
