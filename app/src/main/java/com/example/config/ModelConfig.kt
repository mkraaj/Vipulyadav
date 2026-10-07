package com.example.config

enum class ModelProfileType {
    FAST,
    SMART
}

data class ModelProfile(
    val type: ModelProfileType,
    val modelId: String,
    val displayName: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val defaultTemperature: Float = 0.7f,
    val timeoutSeconds: Long = 60L
)

object ModelConfig {
    // Prohibited: gemini-1.5-flash, gemini-1.5-pro, gemini-2.0-flash
    // Supported per skill: gemini-3.5-flash (Fast/General), gemini-3.1-pro-preview (Smart/Reasoning)
    val DEFAULT_FAST_PROFILE = ModelProfile(
        type = ModelProfileType.FAST,
        modelId = "gemini-3.5-flash",
        displayName = "Fast Engine (gemini-3.5-flash)",
        descriptionEn = "Optimized for speed: instant MCQs, flashcards, vocabulary & rapid doubts.",
        descriptionHi = "गति के लिए अनुकूलित: त्वरित बहुविकल्पीय प्रश्न, फ्लैशकार्ड और शब्दावली।",
        defaultTemperature = 0.4f,
        timeoutSeconds = 45L
    )

    val DEFAULT_SMART_PROFILE = ModelProfile(
        type = ModelProfileType.SMART,
        modelId = "gemini-3.1-pro-preview",
        displayName = "Smart Engine (gemini-3.1-pro-preview)",
        descriptionEn = "Deep reasoning: UPSC Mains evaluation, multi-statement questions & detailed PDF grounding.",
        descriptionHi = "गहन विश्लेषण: UPSC मेन्स मूल्यांकन, बहु-कथन प्रश्न और विस्तृत PDF विश्लेषण।",
        defaultTemperature = 0.3f,
        timeoutSeconds = 90L
    )

    val BASE_GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
}
