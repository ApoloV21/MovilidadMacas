package com.example.movilidadmacas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClick@{
                if (password != confirmPassword) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                loading = true
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        loading = false
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()

        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }

        if (loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
