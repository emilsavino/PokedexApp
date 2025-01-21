package com.example.pokedex.manager

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.pokedex.R
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class GoogleAuthenticationManager(val context: Context) {
    val auth = Firebase.auth
    private var isSignedIn = false

    init {
        CoroutineScope(Dispatchers.IO).launch { isSignedIn = fetchSignedIn() }
    }

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
    }

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val credentialManager = CredentialManager.create(context)
            val result = withContext(Dispatchers.IO) {
                credentialManager.getCredential(context = context, request = request)
            }

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val firebaseCredential =
                        GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                trySend(AuthResponse.Success)
                                launch {
                                    isSignedIn = true
                                }
                                close()
                            } else {
                                trySend(
                                    AuthResponse.Error(
                                        task.exception?.message ?: "Unknown error"
                                    )
                                )
                                close()
                            }
                        }
                } catch (e: Exception) {
                    trySend(AuthResponse.Error("Error parsing Google ID token: ${e.message}"))
                    close()
                }
            } else {
                trySend(AuthResponse.Error("Received invalid credential type"))
                close()
            }
        } catch (e: Exception) {
            trySend(AuthResponse.Error("Error retrieving credential: ${e.message}"))
            close()
        }
        awaitClose()
    }

    fun fetchSignedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }
}

interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val message: String) : AuthResponse
}