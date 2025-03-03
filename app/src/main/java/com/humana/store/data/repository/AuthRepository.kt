package com.humana.store.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.humana.store.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("მომხმარებელი ვერ მოიძებნა")
            val userData = firestore.collection("users").document(user.uid).get().await()
            Result.success(
                User(
                    id = user.uid,
                    name = userData.getString("name") ?: "",
                    email = user.email ?: "",
                    isAdmin = userData.getBoolean("isAdmin") ?: false
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw Exception("მომხმარებელი ვერ მოიძებნა")

            val userDoc = firestore.collection("users").document(user.uid)
            val userData = userDoc.get().await()

            if (!userData.exists()) {
                val newUser = hashMapOf(
                    "name" to (user.displayName ?: ""),
                    "email" to (user.email ?: ""),
                    "isAdmin" to false
                )
                userDoc.set(newUser).await()
                return Result.success(
                    User(
                        id = user.uid,
                        name = user.displayName ?: "",
                        email = user.email ?: "",
                        isAdmin = false
                    )
                )
            }

            Result.success(
                User(
                    id = user.uid,
                    name = userData.getString("name") ?: user.displayName ?: "",
                    email = userData.getString("email") ?: user.email ?: "",
                    isAdmin = userData.getBoolean("isAdmin") ?: false
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("მომხმარებელი ვერ შეიქმნა")

            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "isAdmin" to false
            )

            firestore.collection("users").document(user.uid).set(userData).await()

            Result.success(
                User(
                    id = user.uid,
                    name = name,
                    email = email,
                    isAdmin = false
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun getUserData(userId: String): User {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                User(
                    id = document.id,
                    email = document.getString("email") ?: "",
                    name = document.getString("name") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: "",
                    profileImageUrl = document.getString("profileImageUrl") ?: "",
                    isAdmin = document.getBoolean("isAdmin") ?: false
                )
            } else {
                User()
            }
        } catch (e: Exception) {
            User()
        }
    }

    private suspend fun saveUserData(user: User) {
        try {
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}
