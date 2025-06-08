// EmailGroqLabeler.kt
package com.example.prueba2tfg

import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Log
import java.util.concurrent.TimeUnit

@Serializable
data class EmailData(
    val subject: String,
    val sender: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class EmailLabel(
    val label: String,
    val confidence: Double,
    val reasoning: String? = null
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqRequest(
    val messages: List<GroqMessage>,
    val model: String,
    val temperature: Double = 0.3,
    val max_tokens: Int = 512,
    val top_p: Double = 1.0,
    val stream: Boolean = false
)

@Serializable
data class GroqChoice(
    val index: Int,
    val message: GroqMessage,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class GroqUsage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

@Serializable
data class GroqResponse(
    val id: String,
    @SerialName("object")
    val objectType: String,
    val created: Long,
    val model: String,
    val choices: List<GroqChoice>,
    val usage: GroqUsage
)

class EmailGroqLabeler(
    private val apiKey: String = "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"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        prettyPrint = false
    }

    private val baseUrl = "https://api.groq.com/openai/v1"

    // Available labels that can be customized
    private val availableLabels = mutableListOf(
        "Trabajo", "Personal", "Promocional", "Spam", "Importante",
        "Social", "Notificaciones", "Facturas", "Newsletters", "Urgente"
    )

    suspend fun labelEmail(emailData: EmailData): EmailLabel? = withContext(Dispatchers.IO) {
        Log.d("EmailGroqLabeler", "Labeling email: ${emailData.subject}")

        val keyValidation = validateApiKey()
        if (keyValidation != null) {
            Log.e("EmailGroqLabeler", keyValidation)
            return@withContext null
        }

        val emailPrompt = createEmailLabelingPrompt(emailData)
        val requestBody = GroqRequest(
            messages = listOf(GroqMessage(role = "user", content = emailPrompt)),
            model = "llama-3.3-70b-versatile",
            temperature = 0.2
        )

        val jsonBody = try {
            json.encodeToString(requestBody)
        } catch (e: Exception) {
            Log.e("EmailGroqLabeler", "Error serializing JSON: ${e.message}", e)
            return@withContext null
        }

        val response = sendRequestWithJson(jsonBody)
        return@withContext parseEmailLabel(response)
    }

    private fun createEmailLabelingPrompt(emailData: EmailData): String {
        return """
        Analiza el siguiente correo electrónico y asigna la etiqueta más apropiada.

        CORREO A ETIQUETAR:
        Remitente: ${emailData.sender}
        Asunto: ${emailData.subject}
        Contenido: ${emailData.body.take(300)}

        ETIQUETAS DISPONIBLES: ${availableLabels.joinToString(", ")}

        INSTRUCCIONES:
        1. Asigna UNA etiqueta de la lista proporcionada
        2. Proporciona un nivel de confianza (0.0 a 1.0)
        3. Explica brevemente tu razonamiento

        FORMATO DE RESPUESTA (responde exactamente en este formato JSON):
        {
            "label": "etiqueta_seleccionada",
            "confidence": 0.95,
            "reasoning": "breve explicación"
        }
        """.trimIndent()
    }

    private fun parseEmailLabel(response: String): EmailLabel? {
        return try {
            // Try to extract JSON from the response
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1

            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonString = response.substring(jsonStart, jsonEnd)
                Log.d("EmailGroqLabeler", "Parsing JSON: $jsonString")
                json.decodeFromString<EmailLabel>(jsonString)
            } else {
                // If no valid JSON, create default label
                Log.w("EmailGroqLabeler", "Could not parse response as JSON: $response")
                EmailLabel(
                    label = "Personal",
                    confidence = 0.5,
                    reasoning = "Automatic labeling due to parsing error"
                )
            }
        } catch (e: Exception) {
            Log.e("EmailGroqLabeler", "Error parsing label: ${e.message}", e)
            // Return default label in case of error
            EmailLabel(
                label = "Personal",
                confidence = 0.5,
                reasoning = "Error in automatic analysis"
            )
        }
    }

    private fun validateApiKey(): String? {
        return when {
            apiKey.isEmpty() -> "Error: Please configure your Groq API key"
            !apiKey.startsWith("gsk_") -> "Error: Groq API key must start with 'gsk_'"
            else -> null
        }
    }

    private suspend fun sendRequestWithJson(jsonBody: String): String {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            Log.d("EmailGroqLabeler", "Making HTTP request for labeling...")
            Log.d("EmailGroqLabeler", "Request body: $jsonBody")

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                Log.d("EmailGroqLabeler", "Response code: ${response.code}")
                Log.d("EmailGroqLabeler", "Response body: $responseBody")

                if (response.isSuccessful) {
                    val groqResponse = try {
                        json.decodeFromString<GroqResponse>(responseBody)
                    } catch (e: Exception) {
                        Log.e("EmailGroqLabeler", "Error parsing JSON response: ${e.message}", e)
                        return "Error parsing server response: ${e.message}"
                    }

                    return groqResponse.choices.firstOrNull()?.message?.content
                        ?: "Error: Empty response from server"
                } else {
                    return when (response.code) {
                        401 -> "Error: Invalid API key. Check your Groq key."
                        429 -> "Error: Usage limit exceeded. Try again later."
                        in 500..599 -> "Server error. Try again later."
                        else -> "HTTP Error ${response.code}: $responseBody"
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("EmailGroqLabeler", "Error in sendRequestWithJson: ${e.message}", e)
            "Connection error: ${e.message ?: "Unknown error"}"
        }
    }

    // Method to label multiple emails
    suspend fun labelMultipleEmails(emails: List<EmailData>): List<Pair<EmailData, EmailLabel?>> {
        return emails.map { email ->
            email to labelEmail(email)
        }
    }

    // Method to customize available labels
    fun updateAvailableLabels(newLabels: List<String>) {
        availableLabels.clear()
        availableLabels.addAll(newLabels)
    }

    // Method to get current available labels
    fun getAvailableLabels(): List<String> = availableLabels.toList()
}