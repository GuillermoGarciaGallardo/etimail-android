// GmailService.kt - Corrección para manejo de etiqueta IMPORTANT
package com.example.prueba2tfg

import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.ModifyMessageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Base64

data class EmailDataWithId(
    val id: String,
    val subject: String,
    val sender: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis()
)

class GmailService(private val credential: GoogleAccountCredential) {
    private val TAG = "GmailService"

    private val MAX_RETRIES = 3
    private val RETRY_DELAY_MS = 2000L
    private val BATCH_DELAY_MS = 1000L
    private val VERIFICATION_DELAY_MS = 2000L

    private val gmailService: Gmail by lazy {
        Gmail.Builder(NetHttpTransport(), GsonFactory(), credential)
            .setApplicationName("Email Labeler")
            .build()
    }

    @Volatile
    private var existingLabels: Map<String, String> = emptyMap()
    private val labelsMutex = Mutex()

    // CORRECCIÓN: Mapa actualizado de etiquetas del sistema
   
    private val systemLabels = mapOf(
        "inbox" to "INBOX",
        "enviados" to "SENT",
        "sent" to "SENT",
        "spam" to "SPAM",
        "papelera" to "TRASH",
        "trash" to "TRASH",
        "borradores" to "DRAFT",
        "draft" to "DRAFT",
        "starred" to "STARRED",
        "destacado" to "STARRED",
        "unread" to "UNREAD",
        "no leído" to "UNREAD"
    )

    suspend fun getRecentEmailsWithIds(maxResults: Int = 10): List<EmailDataWithId> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Obteniendo correos recientes con IDs...")

            val listResponse: ListMessagesResponse = gmailService.users().messages()
                .list("me")
                .setMaxResults(maxResults.toLong())
                .setQ("in:inbox")
                .execute()

            val messages = listResponse.messages ?: return@withContext emptyList()
            Log.d(TAG, "Encontrados ${messages.size} mensajes")

            val emails = mutableListOf<EmailDataWithId>()

            for (message in messages) {
                try {
                    val fullMessage = gmailService.users().messages()
                        .get("me", message.id)
                        .setFormat("full")
                        .execute()

                    val headers = fullMessage.payload?.headers
                    val subject = headers?.find { it.name == "Subject" }?.value ?: "Sin asunto"
                    val sender = headers?.find { it.name == "From" }?.value ?: "Remitente desconocido"
                    val body = extractEmailBody(fullMessage.payload)
                    val timestamp = fullMessage.internalDate ?: System.currentTimeMillis()

                    val emailData = EmailDataWithId(
                        id = message.id,
                        subject = subject,
                        sender = cleanSenderName(sender),
                        body = body.take(500),
                        timestamp = timestamp
                    )

                    emails.add(emailData)
                    Log.d(TAG, "Procesado correo: $subject")

                } catch (e: Exception) {
                    Log.e(TAG, "Error procesando mensaje ${message.id}", e)
                }
            }

            Log.d(TAG, "Total correos procesados: ${emails.size}")
            return@withContext emails

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo correos", e)
            return@withContext emptyList()
        }
    }

    /**
     * FUNCIÓN PRINCIPAL CORREGIDA: Manejo especial para etiqueta IMPORTANT
     */
    suspend fun applyLabelToEmail(emailId: String, labelName: String): Boolean = withContext(Dispatchers.IO) {
        var lastException: Exception? = null

        repeat(MAX_RETRIES) { attempt ->
            try {
                Log.d(TAG, "🔄 Intento ${attempt + 1}/$MAX_RETRIES - Aplicando etiqueta '$labelName' al correo $emailId")

                if (emailId.isBlank()) {
                    Log.e(TAG, "❌ Email ID vacío")
                    return@withContext false
                }

                if (labelName.isBlank()) {
                    Log.e(TAG, "❌ Nombre de etiqueta vacío")
                    return@withContext false
                }

                // CORRECCIÓN: Manejo especial para etiquetas de importancia
                if (isImportantLabel(labelName)) {
                    return@withContext applyImportanceToEmail(emailId, labelName)
                }

                // Obtener el ID de la etiqueta para etiquetas normales
                val labelId = when {
                    isSystemLabel(labelName) -> {
                        val systemLabelId = getSystemLabelId(labelName)
                        Log.d(TAG, "🏷️ Etiqueta del sistema: '$labelName' -> '$systemLabelId'")
                        systemLabelId
                    }
                    else -> {
                        val normalizedLabelName = normalizeLabelName(labelName)
                        Log.d(TAG, "🏷️ Etiqueta personalizada: '$labelName' -> '$normalizedLabelName'")
                        getOrCreateLabelWithRetry(normalizedLabelName)
                    }
                }

                if (labelId == null) {
                    Log.e(TAG, "❌ No se pudo obtener/crear la etiqueta: $labelName")
                    if (attempt < MAX_RETRIES - 1) {
                        delay(RETRY_DELAY_MS * (attempt + 1))
                        return@repeat
                    }
                    return@withContext false
                }

                Log.d(TAG, "✅ ID de etiqueta obtenida: $labelId")

                // Verificar que el correo existe
                val existingMessage = try {
                    gmailService.users().messages()
                        .get("me", emailId)
                        .setFormat("minimal")
                        .execute()
                } catch (e: Exception) {
                    Log.e(TAG, "❌ El correo con ID $emailId no existe o no es accesible", e)
                    return@withContext false
                }

                // Verificar si la etiqueta ya está aplicada
                val currentLabels = existingMessage.labelIds ?: emptyList()
                if (currentLabels.contains(labelId)) {
                    Log.d(TAG, "ℹ️ La etiqueta '$labelName' ($labelId) ya está aplicada al correo $emailId")
                    return@withContext true
                }

                Log.d(TAG, "📋 Etiquetas actuales del correo: ${currentLabels.joinToString(", ")}")

                // Aplicar la etiqueta
                val modifyRequest = ModifyMessageRequest().apply {
                    addLabelIds = listOf(labelId)
                    removeLabelIds = emptyList()
                }

                Log.d(TAG, "📤 Enviando request para aplicar etiqueta $labelId...")
                val result = gmailService.users().messages()
                    .modify("me", emailId, modifyRequest)
                    .execute()

                Log.d(TAG, "📥 Respuesta recibida - Message ID: ${result?.id}")

                delay(VERIFICATION_DELAY_MS)

                val verificationSuccess = verifyLabelApplied(emailId, labelId, labelName)

                if (verificationSuccess) {
                    Log.d(TAG, "🎉 Etiqueta '$labelName' aplicada y verificada exitosamente")
                    return@withContext true
                } else {
                    Log.w(TAG, "⚠️ La etiqueta no se pudo verificar después de aplicarla")
                    if (isSystemLabel(labelName) && attempt < MAX_RETRIES - 1) {
                        Log.d(TAG, "🔄 Reintentando para etiqueta del sistema...")
                        delay(RETRY_DELAY_MS)
                        return@repeat
                    }
                    return@withContext false
                }

            } catch (e: Exception) {
                lastException = e
                Log.e(TAG, "❌ Error en intento ${attempt + 1}/$MAX_RETRIES", e)

                if (e.message?.contains("insufficient", ignoreCase = true) == true ||
                    e.message?.contains("permission", ignoreCase = true) == true) {
                    Log.e(TAG, "💔 Error de permisos detectado - verificar scopes de Gmail API")
                    return@withContext false
                }

                if (e.message?.contains("rate", ignoreCase = true) == true ||
                    e.message?.contains("quota", ignoreCase = true) == true) {
                    Log.w(TAG, "⏰ Rate limiting detectado, esperando más tiempo...")
                    if (attempt < MAX_RETRIES - 1) {
                        delay(RETRY_DELAY_MS * 2)
                        return@repeat
                    }
                }

                if (attempt < MAX_RETRIES - 1) {
                    val delayTime = RETRY_DELAY_MS * (attempt + 1)
                    Log.d(TAG, "⏳ Esperando ${delayTime}ms antes del siguiente intento...")
                    delay(delayTime)
                } else {
                    Log.e(TAG, "💥 Todos los intentos fallaron para aplicar etiqueta '$labelName'")
                    e.printStackTrace()
                }
            }
        }

        return@withContext false
    }

    
    private fun isImportantLabel(labelName: String): Boolean {
        val importantLabels = listOf("importante", "important", "IMPORTANT")
        return importantLabels.any { it.equals(labelName, ignoreCase = true) }
    }

    
    private suspend fun applyImportanceToEmail(emailId: String, labelName: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "⭐ Aplicando importancia especial al correo $emailId")

            // Opción 1: Usar la etiqueta STARRED como alternativa
            // Gmail no permite modificar directamente IMPORTANT, pero podemos usar STARRED
            val modifyRequest = ModifyMessageRequest().apply {
                addLabelIds = listOf("STARRED")
                removeLabelIds = emptyList()
            }

            val result = gmailService.users().messages()
                .modify("me", emailId, modifyRequest)
                .execute()

            delay(VERIFICATION_DELAY_MS)

            // Verificar que se aplicó la etiqueta STARRED
            val message = gmailService.users().messages()
                .get("me", emailId)
                .setFormat("minimal")
                .execute()

            val hasStarred = message.labelIds?.contains("STARRED") == true

            if (hasStarred) {
                Log.d(TAG, "🎉 Correo marcado como importante (STARRED) exitosamente")
                return@withContext true
            } else {
                Log.w(TAG, "⚠️ No se pudo marcar el correo como importante")
                return@withContext false
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error aplicando importancia al correo", e)

            // Opción 2: Crear una etiqueta personalizada "Importante" si falla
            try {
                Log.d(TAG, "🔄 Intentando crear etiqueta personalizada 'Importante'")
                return@withContext applyCustomImportantLabel(emailId)
            } catch (e2: Exception) {
                Log.e(TAG, "❌ Error creando etiqueta personalizada", e2)
                return@withContext false
            }
        }
    }

    private suspend fun applyCustomImportantLabel(emailId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val customLabelName = "📌 Importante"
            val labelId = getOrCreateLabelWithRetry(customLabelName)

            if (labelId != null) {
                val modifyRequest = ModifyMessageRequest().apply {
                    addLabelIds = listOf(labelId)
                    removeLabelIds = emptyList()
                }

                gmailService.users().messages()
                    .modify("me", emailId, modifyRequest)
                    .execute()

                delay(VERIFICATION_DELAY_MS)

                val verification = verifyLabelApplied(emailId, labelId, customLabelName)
                Log.d(TAG, "🏷️ Etiqueta personalizada 'Importante' aplicada: $verification")
                verification
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error con etiqueta personalizada importante", e)
            false
        }
    }

    private suspend fun verifyLabelApplied(emailId: String, labelId: String, labelName: String): Boolean = withContext(Dispatchers.IO) {
        repeat(3) { attempt ->
            try {
                delay(1000L * (attempt + 1))

                val message = gmailService.users().messages()
                    .get("me", emailId)
                    .setFormat("minimal")
                    .execute()

                val currentLabels = message.labelIds ?: emptyList()
                val hasLabel = currentLabels.contains(labelId)

                Log.d(TAG, "🔍 Verificación ${attempt + 1}/3 - Etiqueta '$labelName' ($labelId) presente: $hasLabel")
                Log.d(TAG, "📋 Etiquetas actuales: ${currentLabels.joinToString(", ")}")

                if (hasLabel) {
                    return@withContext true
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en verificación ${attempt + 1}", e)
            }
        }
        return@withContext false
    }

    private fun isSystemLabel(labelName: String): Boolean {
        return systemLabels.keys.any { it.equals(labelName, ignoreCase = true) }
    }

    private fun getSystemLabelId(labelName: String): String? {
        return systemLabels.entries.find {
            it.key.equals(labelName, ignoreCase = true)
        }?.value
    }

    private fun normalizeLabelName(labelName: String): String {
        val normalized = labelName.trim()
            .replace(Regex("\\s+"), " ")
            .replace(Regex("[<>\"'`]"), "")
            .takeIf { it.isNotBlank() && it.length <= 100 }
            ?: "Etiqueta_${System.currentTimeMillis()}"

        Log.d(TAG, "🔤 Normalización: '$labelName' -> '$normalized'")
        return normalized
    }

    private suspend fun getOrCreateLabelWithRetry(labelName: String): String? = withContext(Dispatchers.IO) {
        repeat(MAX_RETRIES) { attempt ->
            try {
                return@withContext getOrCreateLabel(labelName)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en intento ${attempt + 1} obteniendo/creando etiqueta '$labelName'", e)
                if (attempt < MAX_RETRIES - 1) {
                    delay(RETRY_DELAY_MS * (attempt + 1))
                }
            }
        }
        return@withContext null
    }

    private suspend fun getOrCreateLabel(labelName: String): String? = withContext(Dispatchers.IO) {
        try {
            labelsMutex.withLock {
                refreshLabelsCache()
            }

            var existingLabelId = existingLabels[labelName]

            if (existingLabelId == null) {
                existingLabelId = existingLabels.entries.find {
                    it.key.equals(labelName, ignoreCase = true)
                }?.value
            }

            if (existingLabelId != null) {
                Log.d(TAG, "🔍 Etiqueta '$labelName' encontrada con ID: $existingLabelId")
                return@withContext existingLabelId
            }

            Log.d(TAG, "🆕 Creando nueva etiqueta: $labelName")
            val newLabel = Label().apply {
                name = labelName
                messageListVisibility = "show"
                labelListVisibility = "labelShow"
                type = "user"
                color = com.google.api.services.gmail.model.LabelColor().apply {
                    textColor = "#000000"
                    backgroundColor = "#ffad46"
                }
            }

            val createdLabel = gmailService.users().labels()
                .create("me", newLabel)
                .execute()

            val labelId = createdLabel.id
            if (labelId != null) {
                labelsMutex.withLock {
                    existingLabels = existingLabels + (labelName to labelId)
                }
                Log.d(TAG, "🎉 Etiqueta '$labelName' creada con ID: $labelId")

                delay(1000L)

                return@withContext labelId
            } else {
                Log.e(TAG, "❌ La etiqueta se creó pero no se recibió ID válido")
                return@withContext null
            }

        } catch (e: Exception) {
            Log.e(TAG, "💥 Error obteniendo/creando etiqueta '$labelName'", e)
            throw e
        }
    }

    private suspend fun refreshLabelsCache() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔄 Actualizando cache de etiquetas...")
            val labelsResponse = gmailService.users().labels().list("me").execute()
            val labels = labelsResponse.labels ?: emptyList()

            existingLabels = labels.filter { it.name != null && it.id != null }
                .associate { it.name to it.id }

            Log.d(TAG, "📋 Cache actualizado con ${existingLabels.size} etiquetas")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error actualizando cache de etiquetas", e)
            throw e
        }
    }

    suspend fun listAllLabels(): Map<String, String> = withContext(Dispatchers.IO) {
        labelsMutex.withLock {
            refreshLabelsCache()
        }
        return@withContext existingLabels.toMap()
    }

    suspend fun applyLabelsToEmails(emailsWithLabels: List<Pair<EmailDataWithId, String>>): Map<String, Boolean> = withContext(Dispatchers.IO) {
        val results = mutableMapOf<String, Boolean>()

        Log.d(TAG, "🚀 Iniciando etiquetado batch de ${emailsWithLabels.size} correos")

        for ((index, emailLabel) in emailsWithLabels.withIndex()) {
            val (email, labelName) = emailLabel
            Log.d(TAG, "🏷️ Procesando ${index + 1}/${emailsWithLabels.size}: ${email.subject} -> $labelName")

            val success = applyLabelToEmail(email.id, labelName)
            results[email.id] = success

            if (success) {
                Log.d(TAG, "✅ Etiqueta '$labelName' aplicada a: ${email.subject}")
            } else {
                Log.e(TAG, "❌ Error con etiqueta '$labelName' en: ${email.subject}")
            }

            if (index < emailsWithLabels.size - 1) {
                delay(BATCH_DELAY_MS)
            }
        }

        val successCount = results.values.count { it }
        val failCount = results.values.count { !it }

        Log.d(TAG, "🏁 Etiquetado batch completado. ✅ Éxitos: $successCount, ❌ Fallos: $failCount")

        return@withContext results
    }

    
    private fun extractEmailBody(payload: com.google.api.services.gmail.model.MessagePart?): String {
        return try {
            when {
                payload?.body?.data != null -> {
                    val decodedBytes = Base64.getUrlDecoder().decode(payload.body.data)
                    String(decodedBytes, Charsets.UTF_8)
                }
                payload?.parts != null -> {
                    extractTextFromParts(payload.parts)
                }
                else -> "Sin contenido"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extrayendo cuerpo del correo", e)
            "Error extrayendo contenido"
        }
    }

    private fun extractTextFromParts(parts: List<com.google.api.services.gmail.model.MessagePart>): String {
        for (part in parts) {
            when {
                part.mimeType == "text/plain" && part.body?.data != null -> {
                    return try {
                        val decodedBytes = Base64.getUrlDecoder().decode(part.body.data)
                        String(decodedBytes, Charsets.UTF_8)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error decodificando parte de texto", e)
                        "Error decodificando texto"
                    }
                }
                part.mimeType == "text/html" && part.body?.data != null -> {
                    return try {
                        val decodedBytes = Base64.getUrlDecoder().decode(part.body.data)
                        val htmlContent = String(decodedBytes, Charsets.UTF_8)
                        htmlContent.replace(Regex("<[^>]*>"), " ")
                            .replace(Regex("\\s+"), " ")
                            .trim()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error decodificando HTML", e)
                        "Error decodificando HTML"
                    }
                }
                part.parts != null -> {
                    val result = extractTextFromParts(part.parts)
                    if (result.isNotEmpty() && result != "Sin contenido") {
                        return result
                    }
                }
            }
        }
        return "Sin contenido de texto"
    }

    private fun cleanSenderName(sender: String): String {
        return try {
            val nameMatch = Regex("^(.*?)\\s*<.*>$").find(sender)
            nameMatch?.groupValues?.get(1)?.trim()?.takeIf { it.isNotEmpty() } ?: sender
        } catch (e: Exception) {
            sender
        }
    }

    suspend fun getEmailCount(): Int = withContext(Dispatchers.IO) {
        try {
            val profile = gmailService.users().getProfile("me").execute()
            profile.messagesTotal?.toInt() ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo conteo de correos", e)
            0
        }
    }
}
