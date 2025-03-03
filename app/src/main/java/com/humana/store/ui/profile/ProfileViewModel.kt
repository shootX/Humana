package com.humana.store.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.humana.store.data.model.User
import com.humana.store.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    init {
        loadUserData()
    }

    private fun loadUserData() {
        auth.currentUser?.let { firebaseUser ->
            _user.value = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: "",
                phoneNumber = firebaseUser.phoneNumber ?: ""
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
