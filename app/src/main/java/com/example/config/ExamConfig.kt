package com.example.config

enum class ExamCategory(val displayNameEn: String, val displayNameHi: String) {
    SSC("SSC Exams", "एसएससी परीक्षाएं"),
    STATE("State Exams", "राज्य परीक्षाएं"),
    UPPSC("UPPSC Exams", "यूपीपीएससी परीक्षाएं"),
    UPSC("UPSC Civil Services", "यूपीएससी सिविल सेवा")
}

data class ExamPattern(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val category: ExamCategory,
    val stages: List<String>,
    val totalQuestions: Int,
    val totalMarks: Int,
    val durationMinutes: Int,
    val negativeMarkingPerWrong: Float, // e.g. 0.5f, 0.33f
    val sections: List<String>,
    val cutoffBandEstimated: Int
)

object ExamConfig {
    val EXAM_LIST = listOf(
        // SSC
        ExamPattern(
            id = "ssc_cgl",
            nameEn = "SSC CGL (Tier-1)",
            nameHi = "एसएससी सीजीएल (टियर-1)",
            category = ExamCategory.SSC,
            stages = listOf("Tier-1", "Tier-2"),
            totalQuestions = 100,
            totalMarks = 200,
            durationMinutes = 60,
            negativeMarkingPerWrong = 0.50f,
            sections = listOf("General Intelligence & Reasoning", "General Awareness", "Quantitative Aptitude", "English Comprehension"),
            cutoffBandEstimated = 145
        ),
        ExamPattern(
            id = "ssc_chsl",
            nameEn = "SSC CHSL (Tier-1)",
            nameHi = "एसएससी सीएचएसएल (टियर-1)",
            category = ExamCategory.SSC,
            stages = listOf("Tier-1", "Tier-2"),
            totalQuestions = 100,
            totalMarks = 200,
            durationMinutes = 60,
            negativeMarkingPerWrong = 0.50f,
            sections = listOf("General Intelligence", "General Awareness", "Quantitative Aptitude", "English Language"),
            cutoffBandEstimated = 152
        ),
        ExamPattern(
            id = "ssc_cpo",
            nameEn = "SSC CPO (Paper-1)",
            nameHi = "एसएससी सीपीओ (पेपर-1)",
            category = ExamCategory.SSC,
            stages = listOf("Paper-1", "PET/PST", "Paper-2"),
            totalQuestions = 200,
            totalMarks = 200,
            durationMinutes = 120,
            negativeMarkingPerWrong = 0.25f,
            sections = listOf("General Intelligence", "General Knowledge", "Quantitative Aptitude", "English"),
            cutoffBandEstimated = 120
        ),
        ExamPattern(
            id = "ssc_gd",
            nameEn = "SSC GD Constable",
            nameHi = "एसएससी जीडी कांस्टेबल",
            category = ExamCategory.SSC,
            stages = listOf("CBE", "PET/PST"),
            totalQuestions = 80,
            totalMarks = 160,
            durationMinutes = 60,
            negativeMarkingPerWrong = 0.25f,
            sections = listOf("Reasoning", "General Knowledge", "Elementary Maths", "Hindi / English"),
            cutoffBandEstimated = 130
        ),

        // STATE EXAMS
        ExamPattern(
            id = "up_police_constable",
            nameEn = "UP Police Constable",
            nameHi = "यूपी पुलिस कांस्टेबल",
            category = ExamCategory.STATE,
            stages = listOf("Written Exam", "DV/PST"),
            totalQuestions = 150,
            totalMarks = 300,
            durationMinutes = 120,
            negativeMarkingPerWrong = 0.50f,
            sections = listOf("General Knowledge", "General Hindi", "Numerical Ability", "Mental Aptitude & Reasoning"),
            cutoffBandEstimated = 210
        ),
        ExamPattern(
            id = "upsssc_pet",
            nameEn = "UPSSSC PET",
            nameHi = "यूपीएसएसएससी पीईटी",
            category = ExamCategory.STATE,
            stages = listOf("PET Scorecard"),
            totalQuestions = 100,
            totalMarks = 100,
            durationMinutes = 120,
            negativeMarkingPerWrong = 0.25f,
            sections = listOf("Indian History & National Movement", "Geography & Economy", "Indian Constitution", "General Science & Maths", "Hindi & English", "Logic & Current Affairs", "Data Interpretation"),
            cutoffBandEstimated = 75
        ),

        // UPPSC
        ExamPattern(
            id = "uppsc_pcs_prelims",
            nameEn = "UPPSC PCS (Prelims GS-1)",
            nameHi = "यूपीपीएससी पीसीएस (प्रारंभिक सामान्य अध्ययन-1)",
            category = ExamCategory.UPPSC,
            stages = listOf("Prelims", "Mains", "Interview"),
            totalQuestions = 150,
            totalMarks = 200,
            durationMinutes = 120,
            negativeMarkingPerWrong = 0.44f, // 1/3 of 1.33 marks
            sections = listOf("History of India", "Indian & World Geography", "Indian Polity & Governance", "Economic & Social Development", "Environment & Ecology", "General Science", "UP Special GK"),
            cutoffBandEstimated = 125
        ),
        ExamPattern(
            id = "uppsc_ro_aro",
            nameEn = "UPPSC RO / ARO (Samiksha Adhikari)",
            nameHi = "यूपीपीएससी आरओ / एआरओ (समीक्षा अधिकारी)",
            category = ExamCategory.UPPSC,
            stages = listOf("Prelims", "Mains"),
            totalQuestions = 200,
            totalMarks = 200,
            durationMinutes = 180,
            negativeMarkingPerWrong = 0.33f,
            sections = listOf("General Studies (140 Q)", "Samanya Hindi (60 Q)"),
            cutoffBandEstimated = 132
        ),

        // UPSC
        ExamPattern(
            id = "upsc_prelims_gs1",
            nameEn = "UPSC CSE Prelims (GS Paper-1)",
            nameHi = "यूपीएससी सिविल सेवा प्रारंभिक (जीएस पेपर-1)",
            category = ExamCategory.UPSC,
            stages = listOf("Prelims", "Mains", "Personality Test"),
            totalQuestions = 100,
            totalMarks = 200,
            durationMinutes = 120,
            negativeMarkingPerWrong = 0.66f, // 1/3 of 2.0 marks
            sections = listOf("History & Art & Culture", "Polity & Governance", "Geography & Environment", "Economy", "Science & Tech", "Current Affairs"),
            cutoffBandEstimated = 88
        ),
        ExamPattern(
            id = "upsc_prelims_csat",
            nameEn = "UPSC CSAT (Paper-2 Qualifying 33%)",
            nameHi = "यूपीएससी सीसैट (पेपर-2 क्वालीफाइंग 33%)",
            category = ExamCategory.UPSC,
            stages = listOf("Prelims"),
            totalQuestions = 80,
            totalMarks = 200,
            durationMinutes = 120,
            negativeMarkingPerWrong = 0.83f, // 1/3 of 2.5 marks
            sections = listOf("Reading Comprehension", "Logical Reasoning & Analytical Ability", "Basic Numeracy & Data Interpretation"),
            cutoffBandEstimated = 66
        )
    )

    fun getExamById(id: String): ExamPattern {
        return EXAM_LIST.firstOrNull { it.id == id } ?: EXAM_LIST[0]
    }
}
