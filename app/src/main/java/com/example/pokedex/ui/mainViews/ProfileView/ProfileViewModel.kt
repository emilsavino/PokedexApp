import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dependencyContainer.DependencyContainer.connectivityRepository
import com.example.pokedex.manager.AuthResponse
import com.example.pokedex.ui.navigation.Screen
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel : ViewModel() {
    private val googleAuthManager = DependencyContainer.googleAuthenticationManager
    private val emailAuthManager = DependencyContainer.emailAuthManager
    private val cacheManager = DependencyContainer.cacheManager
    var showNoInternetAlert by mutableStateOf(false)
    val db = Firebase.firestore
    val documents = listOf("favorites", "recentlySearched", "recentlyViewed", "teams")


    var email = mutableStateOf("Guest")
    var uid = mutableStateOf<String?>(null)
    var profilePictureUrl = mutableStateOf<String?>(null)
    var authError = mutableStateOf<String?>(null)

    init {
        initializeUserState()
    }

    private fun initializeUserState() {
        viewModelScope.launch {
            val currentUser =
                googleAuthManager.auth.currentUser ?: emailAuthManager.auth.currentUser
            uid.value = currentUser?.uid
            email.value = currentUser?.email ?: "Unknown User"
            profilePictureUrl.value = currentUser?.photoUrl?.toString()

        }
    }

    fun signOut(navController: NavController) {
        viewModelScope.launch {
            googleAuthManager.auth.signOut()
            cacheManager.clearAllCache()
            navController.navigate(Screen.SignIn.route) {
                popUpTo(Screen.Profile.route) {
                    inclusive = true
                }
            }
        }
    }

    fun deleteAccount(navController: NavController) {
        viewModelScope.launch {
            deleteUserData()
            reauthenticateGoogleUser()

            googleAuthManager.auth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Profile.route) {
                            inclusive = true
                        }
                    }
                } else {
                    emailAuthManager.auth.currentUser?.delete()?.addOnCompleteListener { task ->
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
    }

    private suspend fun reauthenticateGoogleUser() {
        if (connectivityRepository.isConnected.asLiveData().value == false) {
        }
        googleAuthManager.signInWithGoogle().collectLatest { response ->
            when (response) {
                is AuthResponse.Success -> {
                }

                is AuthResponse.Error -> {
                    authError.value = response.message
                }
            }
        }
    }

    private fun deleteUserData() {
        val user = googleAuthManager.auth.currentUser ?: emailAuthManager.auth.currentUser
        val uid = user?.uid ?: return

        val deletionTasks = mutableListOf<Task<Void>>()

        for (documentName in documents) {
            val task =
                db.collection("users").document(uid).collection("userData").document(documentName)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "Deleted $documentName") }
                    .addOnFailureListener { e -> Log.w(TAG, "did not delete $documentName", e) }
            deletionTasks.add(task)
        }

        val rootTask = db.collection("users").document(uid).delete()
            .addOnSuccessListener { Log.d(TAG, "deleted userDoc") }
            .addOnFailureListener { e -> Log.w(TAG, "did not delete userDoc", e) }
        deletionTasks.add(rootTask)

        Tasks.whenAllComplete(deletionTasks).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "all data deleted")
            } else {
                Log.w(TAG, "failure for some")
                authError.value = "no deletions completed"
            }
        }
        viewModelScope.launch {
            cacheManager.clearAllCache()
        }
    }
}

