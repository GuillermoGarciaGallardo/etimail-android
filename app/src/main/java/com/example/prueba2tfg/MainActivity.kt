// MainActivity.kt
package com.example.prueba2tfg

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prueba2tfg.ui.theme.Prueba2tfgTheme
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.gmail.GmailScopes
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleAccountCredential: GoogleAccountCredential

    private val TAG = "MainActivity"

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(TAG, "Google Sign-In result received")
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Google Sign-In successful, authenticating with Firebase")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-In failed: ${e.statusCode} - ${e.message}")
            showSignInError()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "Initial auth state: ${auth.currentUser != null}")

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configure Google Sign-In with the scopes necessary for Gmail (INCLUDING MODIFY SCOPE)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(
                Scope(GmailScopes.GMAIL_READONLY),
                Scope(GmailScopes.GMAIL_MODIFY), // Permite modificar etiquetas
                Scope(GmailScopes.GMAIL_LABELS)  // Permite crear/gestionar etiquetas
            )
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Google Account Credential for Gmail API (WITH MODIFY PERMISSIONS)
        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
            applicationContext,
            listOf(
                GmailScopes.GMAIL_READONLY,
                GmailScopes.GMAIL_MODIFY,
                GmailScopes.GMAIL_LABELS
            )
        )

        setContent {
            Prueba2tfgTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContent(
                        auth = auth,
                        firestore = firestore,
                        googleAccountCredential = googleAccountCredential,
                        onSignIn = { signIn() },
                        onSignOut = { signOut() }
                    )
                }
            }
        }
    }

    private fun showSignInError() {
        Toast.makeText(
            this,
            "Error al iniciar sesión con Google. Inténtalo de nuevo.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun signIn() {
        Log.d(TAG, "Starting Google sign-in flow")
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        // Sign out from Firebase and Google
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "Signed out from Google")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Firebase authentication successful")

                    // Configure credentials for Gmail API
                    val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
                    if (googleAccount != null) {
                        googleAccountCredential.selectedAccount = googleAccount.account
                    }
                } else {
                    Log.e(TAG, "Firebase authentication failed", task.exception)
                    showSignInError()
                }
            }
    }

    // Ahora esta función está dentro de la clase MainActivity
    private fun sendWorkflowRequest(
        chatInput: String,
        sessionId: String = "12345",
        action: String = "sendMessage"
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "x" // Replace with your actual n8n workflow URL

                // Create JSON payload
                val jsonObject = JSONObject().apply {
                    put("chatInput", chatInput)
                    put("sessionId", sessionId)
                    put("action", action)
                }

                // Set up the request
                val client = OkHttpClient()
                val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .build()

                // Execute the request
                val response = client.newCall(request).execute()

                // Parse the response
                if (response.isSuccessful) {
                    val responseData = JSONObject(response.body?.string() ?: "{}")

                    // Handle the response data here
                    withContext(Dispatchers.Main) {
                        // You can show a toast or update UI elements here
                        Toast.makeText(
                            this@MainActivity,
                            "API request successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ApiCall", "Response: $responseData")
                    }
                } else {
                    Log.e("ApiCall", "Error: ${response.code} - ${response.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "API request failed: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ApiCall", "Exception during API call", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "API request error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    googleAccountCredential: GoogleAccountCredential,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()

    // Use collectAsState for auth state to make it more reactive
    val authState = remember { mutableStateOf(auth.currentUser) }

    // Create Gmail service if user is authenticated
    val gmailService = remember(authState.value) {
        if (authState.value != null && googleAccountCredential.selectedAccount != null) {
            GmailService(googleAccountCredential)
        } else {
            null
        }
    }

    // Observe auth changes
    DisposableEffect(key1 = true) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            Log.d("MainAppContent", "Auth state changed: ${firebaseAuth.currentUser != null}")
            authState.value = firebaseAuth.currentUser
        }

        // Register listener
        auth.addAuthStateListener(authStateListener)

        // Remove listener when disposed
        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    // Set up navigation system
    NavHost(
        navController = navController,
        startDestination = if (authState.value != null) "home" else "login"
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginClick = onSignIn,
                isLoading = false // Add a loading state if needed
            )
        }

        // Home Screen
        composable("home") {
            HomeScreen(
                user = authState.value,
                firestore = firestore,
                gmailService = gmailService,
                onLogout = onSignOut
            )
        }
    }

    // Handle navigation based on auth state
    LaunchedEffect(authState.value) {
        val destination = if (authState.value != null) "home" else "login"
        Log.d("MainAppContent", "Auth state triggered navigation to: $destination")
        navController.navigate(destination) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }
}