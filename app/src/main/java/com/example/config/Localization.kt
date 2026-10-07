package com.example.config

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिन्दी")
}

enum class QuestionDisplayMode(val code: String, val labelEn: String, val labelHi: String) {
    BILINGUAL("bilingual", "Bilingual (Eng + हि)", "द्विभाषी (अंग्रेजी + हि)"),
    ENGLISH_ONLY("en", "English Only", "केवल अंग्रेजी"),
    HINDI_ONLY("hi", "Hindi Only", "केवल हिन्दी")
}

object Strings {
    fun get(key: String, language: AppLanguage): String {
        val entry = dictionary[key] ?: return key
        return when (language) {
            AppLanguage.ENGLISH -> entry.en
            AppLanguage.HINDI -> entry.hi
        }
    }

    private data class LangString(val en: String, val hi: String)

    private val dictionary = mapOf(
        "app_title" to LangString("ExamPilot", "एग्जामपायलट"),
        "app_tagline" to LangString("AI-Powered Indian Exam Cockpit", "भारतीय प्रतियोगी परीक्षाओं का AI कॉकपिट"),
        "nav_home" to LangString("Home", "होम"),
        "nav_engine" to LangString("AI Engine", "AI इंजन"),
        "nav_exams" to LangString("Exams", "परीक्षाएं"),
        "nav_notes" to LangString("Library", "लाइब्रेरी"),
        "nav_settings" to LangString("Settings", "सेटिंग्स"),

        // Engine & Keys
        "multi_key_engine" to LangString("Multi-Key Engine", "मल्टी-की इंजन"),
        "engine_desc" to LangString("Smart routing across multiple Gemini API keys with automatic failover, cooldown, and 0-cost caching.", "स्वचालित फेलओवर, कूलडाउन और शून्य-लागत कैशिंग के साथ कई जेमिनी एपीआई कीज़ पर स्मार्ट रूटिंग।"),
        "add_key" to LangString("Add Gemini Key", "जेमिनी की जोड़ें"),
        "export_keys" to LangString("Export Keys", "कीज़ निर्यात करें"),
        "import_keys" to LangString("Import Keys", "कीज़ आयात करें"),
        "test_key" to LangString("Test Key", "जांचें"),
        "key_healthy" to LangString("Healthy", "सक्रिय"),
        "key_cooldown" to LangString("Cooldown", "कूलडाउन"),
        "key_rate_limited" to LangString("Rate Limited (429)", "सीमा समाप्त (429)"),
        "key_invalid" to LangString("Invalid (400)", "अमान्य (400)"),
        "calls_today" to LangString("Calls Today", "आज के अनुरोध"),
        "tokens_today" to LangString("Tokens", "टोकन"),
        "last_success" to LangString("Last Success", "अंतिम सफल"),
        "cooldown_timer" to LangString("Cooldown ends in", "कूलडाउन शेष"),
        "data_saver_mode" to LangString("Data Saver Mode", "डेटा सेवर मोड"),
        "data_saver_desc" to LangString("Generates concise explanations to minimize token consumption.", "टोकन बचाने के लिए संक्षिप्त उत्तर उत्पन्न करता है।"),
        "cached_responses" to LangString("Cached Responses", "कैश किए गए उत्तर"),
        "clear_cache" to LangString("Clear Cache", "कैश खाली करें"),
        "request_queue" to LangString("Request Queue", "अनुरोध कतार"),
        "no_keys_warning" to LangString("No Gemini keys found! Add at least one key to generate AI quizzes and explanations.", "कोई जेमिनी की नहीं मिली! AI क्विज़ और उत्तरों के लिए कम से कम एक की जोड़ें।"),
        "keys_safe_note" to LangString("Your API keys are stored locally on your device and never sent to any third party server.", "आपकी एपीआई की केवल आपके डिवाइस पर सुरक्षित रहती हैं और किसी तीसरे पक्ष को नहीं भेजी जातीं।"),

        // Dashboard
        "target_exam" to LangString("Target Exam", "लक्ष्य परीक्षा"),
        "switch_exam" to LangString("Change", "बदलें"),
        "readiness_score" to LangString("Exam Readiness", "परीक्षा तैयारी स्तर"),
        "daily_streak" to LangString("Study Streak", "अध्ययन स्ट्रीक"),
        "days" to LangString("Days", "दिन"),
        "quick_actions" to LangString("Quick Cockpit", "त्वरित कार्य"),
        "start_mock" to LangString("Start Mock Test", "मॉक टेस्ट शुरू करें"),
        "generate_quiz" to LangString("AI Quiz Generator", "AI क्विज़ बनाएं"),
        "pdf_notes" to LangString("Study Notes", "अध्ययन नोट्स"),
        "mistake_notebook" to LangString("Mistake Notebook", "गलतियों की डायरी"),

        // Dialogs
        "key_nickname" to LangString("Key Nickname", "की का उपनाम"),
        "key_value" to LangString("Gemini API Key (AIzaSy...)", "जेमिनी एपीआई की (AIzaSy...)"),
        "passphrase" to LangString("Encryption Passphrase", "एन्क्रिप्शन पासफ़्रेज़"),
        "cancel" to LangString("Cancel", "रद्द करें"),
        "save" to LangString("Save", "सुरक्षित करें"),
        "delete" to LangString("Delete", "हटाएं"),
        "copied" to LangString("Copied to clipboard", "क्लिपबोर्ड पर कॉपी किया गया")
    )
}
