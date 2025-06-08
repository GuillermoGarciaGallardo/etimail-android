# EtiMail: Sistema de Etiquetado Automático de Correos Electrónicos mediante IA

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white&labelColor=101010)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6.0-4285F4?style=for-the-badge&logo=android&logoColor=white&labelColor=101010)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-9.0.0-FFCA28?style=for-the-badge&logo=firebase&logoColor=white&labelColor=101010)](https://firebase.google.com)
[![Gmail API](https://img.shields.io/badge/Gmail%20API-v1-EA4335?style=for-the-badge&logo=gmail&logoColor=white&labelColor=101010)](https://developers.google.com/gmail/api)
[![Groq AI](https://img.shields.io/badge/Groq%20AI-llama3-00A393?style=for-the-badge&logo=cloudsmith&logoColor=white&labelColor=101010)](https://groq.com)
[![Gradle](https://img.shields.io/badge/Gradle-8.2.1-02303A?style=for-the-badge&logo=gradle&logoColor=white&labelColor=101010)](https://gradle.org)

---

## Descripción

**EtiMail** es una aplicación Android diseñada para usuarios que reciben grandes volúmenes de correos electrónicos y desean mantener su bandeja de entrada organizada sin esfuerzo. Utiliza inteligencia artificial para analizar el contenido de los emails en Gmail y aplica etiquetas automáticas según el contexto y la urgencia. La app ofrece explicaciones del razonamiento de la IA y niveles de confianza en cada clasificación. Cuenta con una interfaz moderna basada en Jetpack Compose y autenticación segura mediante Firebase.

---

## Tabla comparativa

| Característica      | Gmail (nativo) | Spark Mail   | Edison Mail | **EtiMail**          |
|---------------------|----------------|--------------|-------------|----------------------|
| Etiquetado IA       | Básico         | Sí           | Sí          | **Avanzado**         |
| Explicación IA      | No             | Parcial      | No          | **Sí**               |
| Personalización     | Limitada       | Media        | Media       | **Alta**             |
| Plataforma          | Android/iOS/Web| Multiplataforma| Multiplataforma | **Android**    |
| Precio              | Gratuito       | Freemium     | Freemium    | **Gratuito**         |

---

## Características principales

- Etiquetado automático de emails con IA (Llama-3) según contenido y la urgencia
- Explicaciones y nivel de confianza en cada clasificación
- Interfaz moderna con Jetpack Compose y Material Design 3
- Autenticación segura con Firebase y Google OAuth 2.0
- Optimización para grandes volúmenes de correos: paginación, caché y procesamiento asíncrono
- Personalización de categorías y umbrales de confianza
- Probado en dispositivos reales y emuladores Android (API 24+)

---

## Arquitectura y tecnologías

- **Lenguaje principal:** Kotlin 1.9.10
- **UI:** Jetpack Compose, Material Design 3
- **Backend:** Firebase Authentication, Firestore
- **APIs externas:** Gmail API, Groq AI (llama-3)
- **IDE recomendado:** Android Studio Giraffe 2022.3.1+
- **Control de versiones:** Git

---

## Instalación y configuración

### Requisitos

- Android Studio Flamingo/Giraffe 2022.2.1 o superior
- JDK 17+
- Android SDK API 24+ (recomendado API 34)
- Dispositivo Android con 4GB RAM mínimo

### Pasos rápidos

1. Clona el repositorio:
```bash
git clone https://github.com/GuillermoGarciaGallardo/etimail-android.git
cd etimail-android
```

2. Configura las credenciales:
- Descarga `google-services.json` desde Firebase Console y colócalo en `app/`
- Configura OAuth 2.0 para Gmail API en Google Cloud Console
- Crea tu clave de API de Groq y guárdala de forma segura
3. Abre el proyecto en Android Studio y sincroniza dependencias
4. Ejecuta:
```bash
./gradlew clean build
./gradlew installDebug
```
5. Inicia sesión con Google en la app y concede permisos de Gmail

Para detalles avanzados, consulta el **Manual de instalación y despliegue** en la documentación.

---

## Estructura del proyecto
```bash
app/
├── src/main/java/com/etimail/
│ ├── ui/ComponentesUi.kt
│ ├── services/GmailService.kt
│ ├── services/groqAtaque.kt
│ ├── models/EmailDataWithId.kt
│ └── MainActivity.kt
└── build.gradle
```
---

## Principales módulos

- **ComponentesUi.kt**: Pantallas y componentes Jetpack Compose (Login, Home, Tabs, Animaciones)
- **GmailService.kt**: Integración con Gmail API (lectura, etiquetado, permisos)
- **groqAtaque.kt**: Comunicación con Groq AI para clasificación y explicación de emails
- **Modelos de datos**: Estructuras para emails, etiquetas, preferencias de usuario
- **Tests**: Pruebas unitarias, integración y UI automatizadas

---

## Seguridad y privacidad

- Autenticación y permisos con OAuth 2.0 y Firebase
- Almacenamiento seguro de tokens y claves (Android Keystore)
- Procesamiento mínimo de datos personales, cumpliendo con GDPR
- Uso de HTTPS/TLS y certificate pinning
- Opciones de ofuscación de código y protección de API keys

---

## Roadmap y mejoras futuras

- Versión multiplataforma (iOS, Web)
- Modelos de IA ejecutados localmente para mayor privacidad
- Integración con otros proveedores de correo (Outlook, Yahoo)
- Funcionalidades inteligentes adicionales: resúmenes automáticos, respuestas sugeridas, integración con calendarios y plataformas de productividad
- Aprendizaje automático personalizado según feedback del usuario

---

## Contribuciones

Las contribuciones son bienvenidas. Abre un issue para sugerencias o reportar bugs, y consulta las guías de desarrollo incluidas en la documentación técnica.

---

## Licencia

Este proyecto está licenciado bajo los términos especificados en el repositorio.

---

## Referencias

- [Documentación Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Gmail API](https://developers.google.com/gmail/api)
- [Groq AI](https://groq.com)
- [Kotlin](https://kotlinlang.org/docs/)
- [Material Design 3](https://m3.material.io/)

---

> EtiMail demuestra cómo la inteligencia artificial puede aplicarse de forma efectiva a la gestión del correo electrónico, integrando tecnologías modernas y servicios en la nube en una experiencia móvil eficiente y segura.

