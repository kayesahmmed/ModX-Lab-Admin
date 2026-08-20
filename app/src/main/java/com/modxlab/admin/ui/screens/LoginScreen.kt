package com.modxlab.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.modxlab.admin.ui.components.GlassBox
import com.modxlab.admin.ui.components.GlassPrimaryButton
import com.modxlab.admin.ui.components.GlassTextField
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassBox(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ADMIN LOGIN",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GlassTextField(
                    value = email,
                    onValueChange = { email = it; error = null },
                    label = "Admin Email",
                    placeholder = "Enter admin email",
                    leadingIcon = Icons.Default.Email,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                GlassTextField(
                    value = password,
                    onValueChange = { password = it; error = null },
                    label = "Admin Password",
                    placeholder = "Enter admin password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GlassPrimaryButton(
                    text = "LOGIN",
                    isLoading = isLoading,
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            error = "Email and Password required"
                            return@GlassPrimaryButton
                        }
                        isLoading = true
                        scope.launch {
                            try {
                                val result = FirebaseAuth.getInstance()
                                    .signInWithEmailAndPassword(email.trim(), password)
                                    .await()
                                
                                // Check admin claim (optional client check, rules enforce it)
                                val tokenResult = result.user?.getIdToken(true)?.await()
                                if (tokenResult?.claims?.get("admin") == true) {
                                    onLoginSuccess()
                                } else {
                                    FirebaseAuth.getInstance().signOut()
                                    error = "Not an admin account."
                                }
                            } catch (e: Exception) {
                                error = e.localizedMessage ?: "Login failed"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )
            }
        }
    }
}
