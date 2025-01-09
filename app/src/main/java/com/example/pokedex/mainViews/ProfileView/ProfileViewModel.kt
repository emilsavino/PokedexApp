import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.manager.AuthResponse
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val googleAuthManager = DependencyContainer.googleAuthenticationManager

    var isSignedIn = mutableStateOf(false)
    var email = mutableStateOf("Guest")
    var profilePictureUrl = mutableStateOf<String?>(null)
    var authError = mutableStateOf<String?>(null)


    init {
        initializeUserState()
    }

    private fun initializeUserState() {
        viewModelScope.launch {
            isSignedIn.value = googleAuthManager.fetchSignedIn()
            if (isSignedIn.value) {
                val currentUser = googleAuthManager.auth.currentUser
                email.value = currentUser?.email ?: "Unknown User"
                profilePictureUrl.value = currentUser?.photoUrl?.toString()
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            googleAuthManager.signInWithGoogle().collectLatest { response ->
                when (response) {
                    is AuthResponse.Success -> {
                        val currentUser = googleAuthManager.auth.currentUser
                        isSignedIn.value = true
                        email.value = currentUser?.email ?: "Unknown User"
                        profilePictureUrl.value = currentUser?.photoUrl?.toString()
                        authError.value = null
                    }
                    is AuthResponse.Error -> {
                        authError.value = response.message
                    }
                }
            }
        }
    }

    fun signOut() {
        googleAuthManager.auth.signOut()
        resetUserState()
    }

    fun deleteAccount() {
        viewModelScope.launch {
            googleAuthManager.auth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resetUserState()
                } else {
                    authError.value = task.exception?.message ?: "Failed to delete account"
                }
            }
        }
    }

    private fun resetUserState() {
        isSignedIn.value = false
        email.value = "Guest"
        profilePictureUrl.value = null
        authError.value = null
    }
}
