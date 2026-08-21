package com.modxlab.admin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("AuthViewModel", "FirebaseAuth init error: ${e.message}")
            FirebaseAuth.getInstance()
        }
    }

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun silentLogin() {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                _isLoggedIn.value = true
                return
            }

            val email = "admin@kayesahmmed.com"
            val password = "AdminPass2026!"

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _isLoggedIn.value = true
                }
                .addOnFailureListener { signInException ->
                    // If login fails because user doesn't exist, try creating the admin account once
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            _isLoggedIn.value = true
                        }
                        .addOnFailureListener { createException ->
                            Log.w("AuthViewModel", "Silent email auth failed: ${createException.message}. Trying anonymous auth...")
                            auth.signInAnonymously()
                                .addOnSuccessListener {
                                    _isLoggedIn.value = true
                                }
                                .addOnFailureListener { anonException ->
                                    Log.e("AuthViewModel", "Silent anonymous auth failed: ${anonException.message}")
                                }
                        }
                }
        } catch (e: Exception) {
            Log.w("AuthViewModel", "Silent auth exception: ${e.message}")
        }
    }
}

