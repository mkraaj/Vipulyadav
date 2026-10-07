package com.example.core.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import org.json.JSONArray
import org.json.JSONObject

/**
 * Enterprise-grade PBKDF2 + AES-GCM (256-bit) encryption for exporting and importing
 * Gemini API keys securely with a user-chosen passphrase.
 */
object KeyCrypto {

    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 12 // GCM standard IV length
    private const val TAG_LENGTH_BIT = 128

    data class KeyExportItem(
        val nickname: String,
        val apiKey: String,
        val isEnabled: Boolean
    )

    private fun base64Encode(bytes: ByteArray): String {
        return try {
            java.util.Base64.getEncoder().encodeToString(bytes)
        } catch (_: Throwable) {
            android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        }
    }

    private fun base64Decode(str: String): ByteArray {
        return try {
            java.util.Base64.getDecoder().decode(str)
        } catch (_: Throwable) {
            android.util.Base64.decode(str, android.util.Base64.DEFAULT)
        }
    }

    fun encryptKeys(keys: List<KeyExportItem>, passphrase: String): String {
        val jsonArray = JSONArray()
        keys.forEach {
            val obj = JSONObject().apply {
                put("nickname", it.nickname)
                put("apiKey", it.apiKey)
                put("isEnabled", it.isEnabled)
            }
            jsonArray.put(obj)
        }
        val plaintext = jsonArray.toString().toByteArray(Charsets.UTF_8)

        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }

        val keySpec = PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val secretKeyBytes = factory.generateSecret(keySpec).encoded
        val secretKey = SecretKeySpec(secretKeyBytes, "AES")

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        val ciphertext = cipher.doFinal(plaintext)

        val payload = JSONObject().apply {
            put("version", 1)
            put("salt", base64Encode(salt))
            put("iv", base64Encode(iv))
            put("ciphertext", base64Encode(ciphertext))
        }

        return base64Encode(payload.toString().toByteArray(Charsets.UTF_8))
    }

    fun decryptKeys(encodedPackage: String, passphrase: String): Result<List<KeyExportItem>> {
        return runCatching {
            val packageJsonStr = String(base64Decode(encodedPackage.trim()), Charsets.UTF_8)
            val json = JSONObject(packageJsonStr)
            val salt = base64Decode(json.getString("salt"))
            val iv = base64Decode(json.getString("iv"))
            val ciphertext = base64Decode(json.getString("ciphertext"))

            val keySpec = PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val secretKeyBytes = factory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(secretKeyBytes, "AES")

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            val plaintext = cipher.doFinal(ciphertext)

            val array = JSONArray(String(plaintext, Charsets.UTF_8))
            val result = mutableListOf<KeyExportItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    KeyExportItem(
                        nickname = obj.getString("nickname"),
                        apiKey = obj.getString("apiKey"),
                        isEnabled = obj.optBoolean("isEnabled", true)
                    )
                )
            }
            result
        }
    }
}
