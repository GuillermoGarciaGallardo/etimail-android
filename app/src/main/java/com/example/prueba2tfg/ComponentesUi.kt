// ComponentesUi.kt
package com.example.prueba2tfg

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ComponentesUi {
    companion object {
        private const val TAG = "ComponentesUi"
    }
}

// Colores personalizados para el tema
object EmailLabelerTheme {
    // Gradientes principales
    val primaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6366F1), // Indigo moderno
            Color(0xFF8B5FE7)  // Púrpura suave
        )
    )

    val secondaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFEC4899), // Rosa vibrante
            Color(0xFFF97316)  // Naranja energético
        )
    )

    val successGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF10B981), // Verde esmeralda
            Color(0xFF34D399)  // Verde menta
        )
    )

    val warningGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF59E0B), // Ámbar
            Color(0xFFFBBF24)  // Amarillo dorado
        )
    )

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFEFBFF), // Blanco perlado
            Color(0xFFF8FAFC)  // Gris muy claro
        )
    )

    // Colores base
    val surfaceColor = Color(0xFFFAFAFC)
    val cardColor = Color.White
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    // Colores de estado
    val successColor = Color(0xFF10B981)
    val warningColor = Color(0xFFF59E0B)
    val errorColor = Color(0xFFEF4444)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    isLoading: Boolean = false
) {
    var logoRotation by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = logoRotation,
        animationSpec = tween(2000, easing = LinearEasing),
        label = "logo_rotation"
    )

    LaunchedEffect(Unit) {
        while (true) {
            logoRotation += 360f
            delay(3000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF667eea)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Elementos decorativos de fondo
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size((100 + index * 50).dp)
                    .offset(
                        x = (-200 + index * 100).dp,
                        y = (-300 + index * 150).dp
                    )
                    .alpha(0.1f)
                    .rotate(animatedRotation * (index + 1) * 0.3f)
                    .background(
                        Color.White,
                        CircleShape
                    )
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Logo animado con efecto halo
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Halo effect
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF667eea).copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    // Logo principal
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                brush = EmailLabelerTheme.primaryGradient,
                                shape = CircleShape
                            )
                            .rotate(animatedRotation * 0.5f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "App Logo",
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }
                }

                // Título con efecto degradado
                Text(
                    text = "EtiMail" ,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = EmailLabelerTheme.textPrimary
                )

                // Subtítulo elegante
                Text(
                    text = "✨ Organiza tus correos automáticamente con IA",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = EmailLabelerTheme.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de login con gradiente y animación
                AnimatedGradientButton(
                    onClick = onLoginClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    gradient = EmailLabelerTheme.primaryGradient
                ) {
                    if (isLoading) {
                        PulsingLoadingIndicator()
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = "Google Icon",
                                tint = Color.White
                            )
                            Text(
                                text = "Iniciar sesión con Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Información adicional
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security",
                        modifier = Modifier.size(16.dp),
                        tint = EmailLabelerTheme.textSecondary
                    )
                    Text(
                        text = "Conexión segura con tu cuenta de Google",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = EmailLabelerTheme.textSecondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: FirebaseUser?,
    firestore: FirebaseFirestore,
    gmailService: GmailService?,
    onLogout: () -> Unit
) {
    var emails by remember { mutableStateOf<List<EmailDataWithId>>(emptyList()) }
    var labeledEmails by remember { mutableStateOf<List<Pair<EmailDataWithId, EmailLabel?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    var showUserMenu by remember { mutableStateOf(false) }

    var labelingStates by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var applyingStates by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val emailLabeler = remember { EmailGroqLabeler() }

    // Debug al inicializar
    LaunchedEffect(gmailService) {
        Log.d("HomeScreen", "GmailService inicializado: ${gmailService != null}")
        if (gmailService != null) {
            Log.d("HomeScreen", "GmailService disponible para usar")
        } else {
            Log.w("HomeScreen", "GmailService es null - verificar autenticación")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmailLabelerTheme.surfaceColor)
    ) {
        // Top App Bar mejorado
        EnhancedTopAppBar(
            user = user,
            showUserMenu = showUserMenu,
            onUserMenuToggle = { showUserMenu = !showUserMenu },
            onLogout = onLogout
        )

        // Tab Row estilizado
        StylizedTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // Contenido según la pestaña seleccionada
        when (selectedTab) {
            0 -> EmailsTab(
                emails = emails,
                isLoading = isLoading,
                labelingStates = labelingStates,
                applyingStates = applyingStates,
                gmailService = gmailService,
                emailLabeler = emailLabeler,
                onEmailsLoaded = { loadedEmails ->
                    Log.d("HomeScreen", "Actualizando emails: ${loadedEmails.size}")
                    emails = loadedEmails
                    labelingStates = emptyMap()
                    applyingStates = emptyMap()
                },
                onEmailsLabeled = { labeled ->
                    Log.d("HomeScreen", "Emails etiquetados: ${labeled.size}")
                    labeledEmails = labeled
                    // MODIFICACIÓN: No cambiar automáticamente de pestaña
                    // selectedTab = 1  // Esta línea se comenta para mantener la vista actual
                },
                onLoadingStateChanged = { loading ->
                    Log.d("HomeScreen", "Estado de carga cambiado: $loading")
                    isLoading = loading
                },
                onIndividualLabelingStateChanged = { emailKey, isLabeling ->
                    labelingStates = labelingStates.toMutableMap().apply {
                        if (isLabeling) put(emailKey, true) else remove(emailKey)
                    }
                },
                onIndividualApplyingStateChanged = { emailKey, isApplying ->
                    applyingStates = applyingStates.toMutableMap().apply {
                        if (isApplying) put(emailKey, true) else remove(emailKey)
                    }
                },
                onIndividualEmailLabeled = { email, label ->
                    labeledEmails = labeledEmails.toMutableList().apply {
                        removeAll { it.first.id == email.id }
                        add(email to label)
                    }
                }
            )

            1 -> LabeledEmailsTab(
                labeledEmails = labeledEmails,
                gmailService = gmailService,
                applyingStates = applyingStates,
                onApplyingStateChanged = { emailKey, isApplying ->
                    applyingStates = applyingStates.toMutableMap().apply {
                        if (isApplying) put(emailKey, true) else remove(emailKey)
                    }
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTopAppBar(
    user: FirebaseUser?,
    showUserMenu: Boolean,
    onUserMenuToggle: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = EmailLabelerTheme.primaryGradient,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "App Icon",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                Text(
                    text = "Email Labeler",
                    fontWeight = FontWeight.Bold,
                    color = EmailLabelerTheme.textPrimary
                )
            }
        },
        actions = {
            Box {
                IconButton(
                    onClick = onUserMenuToggle,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = EmailLabelerTheme.primaryGradient,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Perfil de usuario",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = showUserMenu,
                    onDismissRequest = { onUserMenuToggle() },
                    modifier = Modifier
                        .background(
                            Color.White,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            Color.Black.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    user?.let { currentUser ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = currentUser.displayName ?: "Usuario",
                                        fontWeight = FontWeight.SemiBold,
                                        color = EmailLabelerTheme.textPrimary
                                    )
                                    Text(
                                        text = currentUser.email ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EmailLabelerTheme.textSecondary
                                    )
                                }
                            },
                            onClick = { }
                        )
                        HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
                    }

                    DropdownMenuItem(
                        text = {
                            Text(
                                "Cerrar sesión",
                                color = EmailLabelerTheme.textPrimary
                            )
                        },
                        onClick = {
                            onUserMenuToggle()
                            onLogout()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = EmailLabelerTheme.textSecondary
                            )
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        modifier = Modifier.shadow(
            elevation = 4.dp,
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
    )
}

@Composable
fun StylizedTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = EmailLabelerTheme.textPrimary,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .height(3.dp)
                    .background(
                        brush = EmailLabelerTheme.primaryGradient,
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                    )
            )
        }
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = {
                Text(
                    "📧 Correos",
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                )
            }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = {
                Text(
                    "⭐ Etiquetados",
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                )
            }
        )
    }
}

@Composable
fun EmailsTab(
    emails: List<EmailDataWithId>,
    isLoading: Boolean,
    labelingStates: Map<String, Boolean>,
    applyingStates: Map<String, Boolean>,
    gmailService: GmailService?,
    emailLabeler: EmailGroqLabeler,
    onEmailsLoaded: (List<EmailDataWithId>) -> Unit,
    onEmailsLabeled: (List<Pair<EmailDataWithId, EmailLabel?>>) -> Unit,
    onLoadingStateChanged: (Boolean) -> Unit,
    onIndividualLabelingStateChanged: (String, Boolean) -> Unit,
    onIndividualApplyingStateChanged: (String, Boolean) -> Unit,
    onIndividualEmailLabeled: (EmailDataWithId, EmailLabel?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botones de acción mejorados con mejor debug
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedGradientButton(
                onClick = {
                    scope.launch {
                        onLoadingStateChanged(true)
                        try {
                            Log.d("EmailsTab", "Iniciando carga de correos...")
                            Log.d("EmailsTab", "GmailService es null: ${gmailService == null}")

                            gmailService?.let { service ->
                                Log.d("EmailsTab", "Llamando a getRecentEmailsWithIds...")
                                val loadedEmails = service.getRecentEmailsWithIds(10)
                                Log.d("EmailsTab", "Correos obtenidos: ${loadedEmails.size}")

                                // Debug de cada email
                                loadedEmails.forEachIndexed { index, email ->
                                    Log.d("EmailsTab", "Email $index: ${email.subject} - ${email.sender}")
                                }

                                onEmailsLoaded(loadedEmails)

                                // Mostrar Toast de confirmación
                                Toast.makeText(
                                    context,
                                    "✅ ${loadedEmails.size} correos cargados exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } ?: run {
                                Log.e("EmailsTab", "GmailService es null")
                                Toast.makeText(
                                    context,
                                    "❌ Error: Servicio de Gmail no disponible",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            Log.e("EmailsTab", "Error cargando correos", e)
                            Toast.makeText(
                                context,
                                "❌ Error cargando correos: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            onLoadingStateChanged(false)
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
                gradient = EmailLabelerTheme.primaryGradient
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isLoading) {
                        PulsingLoadingIndicator(size = 16.dp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Cargar correos",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = if (isLoading) "Cargando..." else "Cargar correos",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            AnimatedGradientButton(
                onClick = {
                    scope.launch {
                        onLoadingStateChanged(true)
                        try {
                            Log.d("EmailsTab", "Iniciando etiquetado de ${emails.size} correos...")
                            val emailsForLabeling = emails.map { emailWithId ->
                                EmailData(
                                    subject = emailWithId.subject,
                                    sender = emailWithId.sender,
                                    body = emailWithId.body,
                                    timestamp = emailWithId.timestamp
                                )
                            }
                            val labeledResults = emailLabeler.labelMultipleEmails(emailsForLabeling)
                            val labeledWithIds = emails.zip(labeledResults.map { it.second }).map { (emailWithId, label) ->
                                emailWithId to label
                            }
                            onEmailsLabeled(labeledWithIds)
                            Log.d("EmailsTab", "Correos etiquetados: ${labeledWithIds.size}")

                            Toast.makeText(
                                context,
                                "✅ ${labeledWithIds.size} correos etiquetados exitosamente. Ve a la pestaña 'Etiquetados' para verlos.",
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            Log.e("EmailsTab", "Error etiquetando correos", e)
                            Toast.makeText(
                                context,
                                "❌ Error etiquetando correos: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            onLoadingStateChanged(false)
                        }
                    }
                },
                enabled = !isLoading && emails.isNotEmpty(),
                modifier = Modifier.weight(1f),
                gradient = EmailLabelerTheme.secondaryGradient
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Etiquetar todos",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Text(
                        "Etiquetar todos",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Debug info (solo en desarrollo)
        if (BuildConfig.DEBUG) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Yellow.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "🔧 Debug Info:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Cyan
                    )
                    Text(
                        text = "• Gmail Service: ${if (gmailService != null) "✅ Conectado" else "❌ No disponible"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "• Correos cargados: ${emails.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "• Estado carga: ${if (isLoading) "Cargando..." else "Listo"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // Lista de correos mejorada
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Loading indicator mientras carga
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PulsingLoadingIndicator(size = 48.dp)
                            Text(
                                text = "Cargando correos desde Gmail...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = EmailLabelerTheme.textPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Esto puede tomar unos segundos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EmailLabelerTheme.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            // Estado vacío cuando no hay correos y no está cargando
            else if (emails.isEmpty()) {
                item {
                    EnhancedEmptyStateMessage(
                        icon = Icons.Outlined.Email,
                        title = "No hay correos cargados",
                        subtitle = "Presiona 'Cargar correos' para obtener tus correos recientes de Gmail",
                        gradient = EmailLabelerTheme.primaryGradient
                    )
                }
            }
            // Mostrar correos cargados
            else {
                items(emails) { email ->
                    val emailKey = "${email.id}_${email.timestamp}"
                    val isEmailLabeling = labelingStates[emailKey] == true

                    EnhancedEmailItem(
                        email = email,
                        isLabeling = isEmailLabeling,
                        onLabelClick = {
                            scope.launch {
                                onIndividualLabelingStateChanged(emailKey, true)
                                try {
                                    val emailData = EmailData(
                                        subject = email.subject,
                                        sender = email.sender,
                                        body = email.body,
                                        timestamp = email.timestamp
                                    )
                                    val label = emailLabeler.labelEmail(emailData)
                                    onIndividualEmailLabeled(email, label)
                                    Log.d("EmailsTab", "Email etiquetado individualmente: ${email.subject}")

                                    Toast.makeText(
                                        context,
                                        "✅ Email etiquetado: ${label?.label ?: "Sin etiqueta"}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    Log.e("EmailsTab", "Error etiquetando email individual", e)
                                    Toast.makeText(
                                        context,
                                        "❌ Error etiquetando email: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } finally {
                                    onIndividualLabelingStateChanged(emailKey, false)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun LabeledEmailsTab(
    labeledEmails: List<Pair<EmailDataWithId, EmailLabel?>>,
    gmailService: GmailService?,
    applyingStates: Map<String, Boolean>,
    onApplyingStateChanged: (String, Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (labeledEmails.isEmpty()) {
            EnhancedEmptyStateMessage(
                icon = Icons.Outlined.Star,
                title = "No hay correos etiquetados",
                subtitle = "Primero carga algunos correos y luego etiquétalos",
                gradient = EmailLabelerTheme.secondaryGradient
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(labeledEmails) { (email, label) ->
                    val emailKey = "${email.id}_apply"
                    val isApplying = applyingStates[emailKey] == true

                    EnhancedLabeledEmailItem(
                        email = email,
                        label = label,
                        isApplying = isApplying,
                        onApplyToGmail = {
                            scope.launch {
                                label?.let { emailLabel ->
                                    onApplyingStateChanged(emailKey, true)
                                    try {
                                        val success = gmailService?.applyLabelToEmail(email.id, emailLabel.label)
                                        if (success == true) {
                                            Toast.makeText(
                                                context,
                                                "✅ Etiqueta '${emailLabel.label}' aplicada en Gmail",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.d("LabeledEmailsTab", "Etiqueta aplicada en Gmail: ${email.subject}")
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "❌ Error aplicando etiqueta en Gmail",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("LabeledEmailsTab", "Error aplicando etiqueta en Gmail", e)
                                        Toast.makeText(
                                            context,
                                            "❌ Error: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } finally {
                                        onApplyingStateChanged(emailKey, false)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// Componentes personalizados mejorados

@Composable
fun AnimatedGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: Brush,
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (enabled) 8.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) gradient else Brush.linearGradient(
                        colors = listOf(Color.Gray, Color.LightGray)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(content = content)
        }
    }
}

@Composable
fun PulsingLoadingIndicator(
    size: androidx.compose.ui.unit.Dp = 24.dp
) {
    var scale by remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LaunchedEffect(Unit) {
        while (true) {
            scale = if (scale == 1f) 1.3f else 1f
            delay(800)
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .scale(animatedScale)
            .background(
                Color.White.copy(alpha = 0.8f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size * 0.7f),
            color = Color.White,
            strokeWidth = 2.dp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedEmailItem(
    email: EmailDataWithId,
    isLabeling: Boolean = false,
    onLabelClick: () -> Unit = {}
) {
    var isHovered by remember { mutableStateOf(false) }
    val elevation by animateFloatAsState(
        targetValue = if (isHovered) 12f else 4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .clickable { isHovered = !isHovered },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmailLabelerTheme.cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Subject con mejor tipografía
                    Text(
                        text = email.subject,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = EmailLabelerTheme.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sender con ícono
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Sender",
                            modifier = Modifier.size(16.dp),
                            tint = EmailLabelerTheme.textSecondary
                        )
                        Text(
                            text = email.sender,
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmailLabelerTheme.textSecondary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Timestamp con ícono
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Time",
                            modifier = Modifier.size(14.dp),
                            tint = EmailLabelerTheme.textSecondary
                        )
                        Text(
                            text = formatTimestamp(email.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = EmailLabelerTheme.textSecondary
                        )
                    }

                    // Botón de etiquetado mejorado
                    AnimatedGradientButton(
                        onClick = onLabelClick,
                        enabled = !isLabeling,
                        modifier = Modifier.height(36.dp),
                        gradient = EmailLabelerTheme.secondaryGradient
                    ) {
                        if (isLabeling) {
                            PulsingLoadingIndicator(size = 16.dp)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Etiquetar",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Text(
                                    text = "Etiquetar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body del email con mejor presentación
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = EmailLabelerTheme.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        EmailLabelerTheme.surfaceColor.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedLabeledEmailItem(
    email: EmailDataWithId,
    label: EmailLabel?,
    isApplying: Boolean = false,
    onApplyToGmail: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isBodyExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmailLabelerTheme.cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header del email
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Subject
                    Text(
                        text = email.subject,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = EmailLabelerTheme.textPrimary,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sender
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Sender",
                            modifier = Modifier.size(16.dp),
                            tint = EmailLabelerTheme.textSecondary
                        )
                        Text(
                            text = email.sender,
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmailLabelerTheme.textSecondary,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                        )
                    }

                    // Timestamp
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Time",
                            modifier = Modifier.size(14.dp),
                            tint = EmailLabelerTheme.textSecondary
                        )
                        Text(
                            text = formatTimestamp(email.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = EmailLabelerTheme.textSecondary
                        )
                    }
                }

                // Label y botones
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    label?.let { labelData ->
                        // Chip de etiqueta
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = EmailLabelerTheme.successGradient,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Label",
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Text(
                                    text = labelData.label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Botón aplicar a Gmail
                    AnimatedGradientButton(
                        onClick = onApplyToGmail,
                        enabled = !isApplying && label != null,
                        modifier = Modifier.height(36.dp),
                        gradient = EmailLabelerTheme.successGradient
                    ) {
                        if (isApplying) {
                            PulsingLoadingIndicator(size = 16.dp)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Aplicar en Gmail",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Text(
                                    text = "Aplicar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body del email con capacidad de expansión completa
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isBodyExpanded = !isBodyExpanded },
                colors = CardDefaults.cardColors(
                    containerColor = EmailLabelerTheme.surfaceColor.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = email.body,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp
                        ),
                        color = EmailLabelerTheme.textSecondary,
                        maxLines = if (isBodyExpanded) Int.MAX_VALUE else 4,
                        overflow = if (isBodyExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                    )

                    // Indicador de expansión si el texto es largo
                    if (email.body.length > 200) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                onClick = { isBodyExpanded = !isBodyExpanded },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = EmailLabelerTheme.textSecondary
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (isBodyExpanded) "Ver menos" else "Ver más",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Icon(
                                        imageVector = if (isBodyExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (isBodyExpanded) "Contraer" else "Expandir",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Detalles de la etiqueta
            label?.let { labelData ->
                Spacer(modifier = Modifier.height(12.dp))

                // Botón para expandir/contraer detalles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalles de etiquetado",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = EmailLabelerTheme.textPrimary
                    )

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Contraer detalles" else "Expandir detalles",
                        modifier = Modifier.size(20.dp),
                        tint = EmailLabelerTheme.textSecondary
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically(
                        animationSpec = tween(300, easing = EaseOutCubic)
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutVertically(
                        animationSpec = tween(300, easing = EaseInCubic)
                    ) + fadeOut(animationSpec = tween(300))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = EmailLabelerTheme.surfaceColor.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Barra de confianza mejorada
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Nivel de confianza:",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = EmailLabelerTheme.textPrimary
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LinearProgressIndicator(
                                        progress = labelData.confidence.toFloat(),
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = when {
                                            labelData.confidence > 0.8 -> Color(0xFF4CAF50)
                                            labelData.confidence > 0.6 -> Color(0xFFFF9800)
                                            else -> Color(0xFFf44336)
                                        },
                                        trackColor = Color.Gray.copy(alpha = 0.2f)
                                    )
                                    Text(
                                        text = "${(labelData.confidence * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = EmailLabelerTheme.textPrimary
                                    )
                                }
                            }

                            // Razonamiento expandible
                            labelData.reasoning?.let { reasoning ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Razonamiento",
                                            modifier = Modifier.size(18.dp),
                                            tint = EmailLabelerTheme.textSecondary
                                        )
                                        Text(
                                            text = "Razonamiento de la IA:",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = EmailLabelerTheme.textPrimary
                                        )
                                    }

                                    Text(
                                        text = reasoning,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            lineHeight = 18.sp
                                        ),
                                        color = EmailLabelerTheme.textSecondary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                Color.White.copy(alpha = 0.7f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun EnhancedEmptyStateMessage(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: Brush
) {
    var isAnimating by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "empty_state_scale"
    )

    LaunchedEffect(Unit) {
        isAnimating = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ícono con efecto de gradiente y animación
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(
                    brush = gradient,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = EmailLabelerTheme.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = EmailLabelerTheme.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Decoración adicional
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = EmailLabelerTheme.textSecondary.copy(
                                alpha = 0.3f - (index * 0.1f)
                            ),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < 60_000 -> "Ahora"
        diff < 3600_000 -> "${diff / 60_000}m"
        diff < 86400_000 -> "${diff / 3600_000}h"
        diff < 604800_000 -> "${diff / 86400_000}d"
        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
    }
}