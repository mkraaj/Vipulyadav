package com.example

import com.example.config.ExamCategory
import com.example.config.ExamConfig
import com.example.config.ModelConfig
import com.example.config.ModelProfileType
import com.example.core.crypto.KeyCrypto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MultiKeyEngineTest {

    @Test
    fun testKeyEncryptionAndDecryption() {
        val originalKeys = listOf(
            KeyCrypto.KeyExportItem("Primary Key", "AIzaSyFakeKey1234567890", true),
            KeyCrypto.KeyExportItem("Backup Key", "AIzaSyBackupKey987654321", false)
        )
        val passphrase = "MasterPassphrase@2026"

        val encryptedPackage = KeyCrypto.encryptKeys(originalKeys, passphrase)
        assertNotNull(encryptedPackage)
        assertTrue(encryptedPackage.isNotEmpty())

        val decryptedResult = KeyCrypto.decryptKeys(encryptedPackage, passphrase)
        assertTrue("Decryption should succeed", decryptedResult.isSuccess)

        val decryptedKeys = decryptedResult.getOrThrow()
        assertEquals(2, decryptedKeys.size)
        assertEquals("Primary Key", decryptedKeys[0].nickname)
        assertEquals("AIzaSyFakeKey1234567890", decryptedKeys[0].apiKey)
        assertTrue(decryptedKeys[0].isEnabled)

        assertEquals("Backup Key", decryptedKeys[1].nickname)
        assertEquals(false, decryptedKeys[1].isEnabled)
    }

    @Test
    fun testWrongPassphraseDecryptionFails() {
        val keys = listOf(KeyCrypto.KeyExportItem("Key 1", "AIzaSyTestKey", true))
        val encryptedPackage = KeyCrypto.encryptKeys(keys, "CorrectPassword")

        val wrongResult = KeyCrypto.decryptKeys(encryptedPackage, "WrongPassword")
        assertTrue("Decryption with wrong password must fail", wrongResult.isFailure)
    }

    @Test
    fun testExamPatternsIntegrity() {
        val sscCgl = ExamConfig.getExamById("ssc_cgl")
        assertEquals(100, sscCgl.totalQuestions)
        assertEquals(200, sscCgl.totalMarks)
        assertEquals(0.50f, sscCgl.negativeMarkingPerWrong)
        assertEquals(ExamCategory.SSC, sscCgl.category)

        val upsc = ExamConfig.getExamById("upsc_prelims_gs1")
        assertEquals(100, upsc.totalQuestions)
        assertEquals(200, upsc.totalMarks)
        assertEquals(0.66f, upsc.negativeMarkingPerWrong)
        assertEquals(ExamCategory.UPSC, upsc.category)
    }

    @Test
    fun testModelProfilesConfiguration() {
        assertEquals("gemini-3.5-flash", ModelConfig.DEFAULT_FAST_PROFILE.modelId)
        assertEquals(ModelProfileType.FAST, ModelConfig.DEFAULT_FAST_PROFILE.type)

        assertEquals("gemini-3.1-pro-preview", ModelConfig.DEFAULT_SMART_PROFILE.modelId)
        assertEquals(ModelProfileType.SMART, ModelConfig.DEFAULT_SMART_PROFILE.type)
    }
}
