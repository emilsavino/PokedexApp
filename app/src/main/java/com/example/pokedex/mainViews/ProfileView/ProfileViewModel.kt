import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.mainViews.ProfileView.signIn.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val googleAuthManager = DependencyContainer.googleAuthenticationManager

    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> get() = _isSignedIn

    private val _email = MutableStateFlow("Guest")
    val email: StateFlow<String> get() = _email

    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: StateFlow<String?> get() = _profilePictureUrl

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> get() = _authError

    init {
        initializeUserState()
    }

    private fun initializeUserState() {
        viewModelScope.launch {
            _isSignedIn.value = googleAuthManager.fetchSignedIn()
            if (_isSignedIn.value) {
                val currentUser = googleAuthManager.auth.currentUser
                _email.value = currentUser?.email ?: "Unknown User"
                _profilePictureUrl.value = currentUser?.photoUrl?.toString()
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            googleAuthManager.signInWithGoogle().collectLatest { response ->
                when (response) {
                    is AuthResponse.Success -> {
                        val currentUser = googleAuthManager.auth.currentUser
                        _isSignedIn.value = true
                        _email.value = currentUser?.email ?: "Unknown User"
                        _profilePictureUrl.value = currentUser?.photoUrl?.toString()
                        _authError.value = null
                    }
                    is AuthResponse.Error -> {
                        _authError.value = response.message
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
                    _authError.value = task.exception?.message ?: "Failed to delete account"
                }
            }
        }
    }

    private fun resetUserState() {
        _isSignedIn.value = false
        _email.value = "Guest"
        _profilePictureUrl.value = null
        _authError.value = null
    }
}
