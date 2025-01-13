package com.example.pokedex.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await

class DatabaseService<T : Any>(private val key: String, private val clazz: Class<T>) {

    private val db = FirebaseFirestore.getInstance()
    private val gson = Gson()

    // Store Array
    suspend fun storeArray(objects: List<T>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(userId)

        try {
            val data = gson.toJson(objects)

            val objectType = object : TypeToken<List<Map<String, Any>>>() {}.type
            val objectArray: List<Map<String, Any>> = gson.fromJson(data, objectType)

            userDocRef.set(mapOf(key to objectArray)).await()
        } catch (error: Exception) {
            println("Error encoding $key: ${error.localizedMessage}")
        }
    }

    // Store Object
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

    // Fetch Object
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

    fun addListenerForArray(completion: (List<T>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).addSnapshotListener { documentSnapshot, _ ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val data = documentSnapshot.data?.get(key) as? List<Map<String, Any>>
                if (data != null) {
                    try {
                        val jsonData = gson.toJson(data)

                        val typeToken = object : TypeToken<List<T>>() {}.type
                        val objectArray: List<T> = gson.fromJson(jsonData, typeToken)

                        completion(objectArray)
                    } catch (error: Exception) {
                        println("Error decoding $key: ${error.localizedMessage}")
                    }
                } else {
                    println("No data found in document.")
                    completion(emptyList())
                }
            } else {
                println("No documents")
                completion(emptyList())
            }
        }
    }

    // Add Listener for Object
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

    // Delete User
    suspend fun deleteUser(documentKey: String) {
        try {
            db.collection("users").document(documentKey).delete().await()
        } catch (error: Exception) {
            println("Error deleting document: ${error.localizedMessage}")
        }
    }
}
