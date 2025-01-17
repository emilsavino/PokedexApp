package com.example.pokedex.data

import androidx.lifecycle.asLiveData
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await

class DatabaseService<T : Any>(private val key: String, private val clazz: Class<T>) {
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private val db = FirebaseFirestore.getInstance()
    private val gson = Gson()

    suspend fun storeList(objects: List<T>): Boolean {
        if (connectivityRepository.isConnected.asLiveData().value == false) {
            return false
        }
        var succesful = false
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        val docRef = db.collection("users").document(userId).collection("userData")
            .document(key)

        try {
            val data = gson.toJson(objects)

            val objectType = object : TypeToken<List<Map<String, Any>>>() {}.type
            val objectArray: List<Map<String, Any>> = gson.fromJson(data, objectType)

            docRef.set(mapOf(key to objectArray)).await()
            succesful = true
        } catch (error: Exception) {
            println("Error encoding favorites: ${error.localizedMessage}")
        }
        return succesful
    }

    suspend fun storeObject(obj: T) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(userId)

        try {
            val data = gson.toJson(obj)
            val objectMap = gson.fromJson(data, Map::class.java) as? Map<String, Any>

            userDocRef.set(mapOf(key to objectMap)).await()
        } catch (error: Exception) {
            println("Error encoding $key: ${error.localizedMessage}")
        }
    }

    suspend fun fetchObject(): T? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return null

        return try {
            val documentSnapshot = db.collection("users").document(userId).get().await()
            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data?.get(key) as? Map<String, Any>
                val jsonData = gson.toJson(data)
                gson.fromJson(jsonData, clazz)
            } else {
                println("No documents")
                null
            }
        } catch (error: Exception) {
            println("Error decoding $key: ${error.localizedMessage}")
            null
        }
    }

    suspend fun addListenerForList(completion: (List<T>?) -> Unit): ListenerRegistration {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return ListenerRegistration { }
        val docRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("userData")
            .document(key)

        val initializationSignal = CompletableDeferred<Unit>()

        val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error listening for document changes: ${error.localizedMessage}")
                completion(null)
                initializationSignal.complete(Unit)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists() && snapshot.contains(key)) {
                val favoritesJson = snapshot[key]

                try {
                    val gson = Gson()

                    val objectType = TypeToken.getParameterized(List::class.java, clazz).type
                    val data = gson.fromJson<List<T>>(gson.toJson(favoritesJson), objectType)

                    completion(data)
                } catch (e: Exception) {
                    println("Error deserializing data: ${e.localizedMessage}")
                    completion(null)
                }
            } else {
                completion(null)
            }

            initializationSignal.complete(Unit)
        }

        initializationSignal.await()
        return listenerRegistration
    }


    fun addListenerForObject(completion: (T?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).addSnapshotListener { documentSnapshot, _ ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val data = documentSnapshot.data?.get(key) as? Map<String, Any>
                if (data != null) {
                    try {
                        val jsonData = gson.toJson(data)
                        val obj = gson.fromJson(jsonData, clazz)
                        completion(obj)
                    } catch (error: Exception) {
                        println("Error decoding $key: ${error.localizedMessage}")
                    }
                } else {
                    println("No data found in document.")
                    completion(null)
                }
            } else {
                println("No documents")
                completion(null)
            }
        }
    }

    suspend fun deleteUser(documentKey: String) {
        try {
            db.collection("users").document(documentKey).delete().await()
        } catch (error: Exception) {
            println("Error deleting document: ${error.localizedMessage}")
        }
    }
}
