package com.example.rumattire

import android.content.Context
import android.util.Base64
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal data class LocalAccount(val name: String, val email: String, val passwordChangedAt: Long)

internal class LocalAccounts(context: Context) {
    private val prefs = context.getSharedPreferences("rum_attire_local", Context.MODE_PRIVATE)

    var onboardingComplete: Boolean
        get() = prefs.getBoolean("onboarding_complete", false)
        set(value) { prefs.edit().putBoolean("onboarding_complete", value).apply() }

    private fun normalize(email: String) = email.trim().lowercase(Locale.ROOT)

    private fun record(email: String): JSONObject? =
        prefs.getString("account:${normalize(email)}", null)?.let(::JSONObject)

    fun remembered(): LocalAccount? {
        val email = prefs.getString("session_email", null) ?: return null
        return record(email)?.toAccount(email)
    }

    @Synchronized
    fun register(name: String, email: String, phone: String, atelier: String, password: String): Boolean {
        val key = normalize(email)
        if (record(key) != null) return false
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val account = JSONObject()
            .put("name", name.trim())
            .put("phone", phone.trim())
            .put("atelier", atelier.trim())
            .put("salt", encode(salt))
            .put("verifier", encode(derive(password, salt)))
            .put("password_changed", System.currentTimeMillis())
        return prefs.edit().putString("account:$key", account.toString()).commit()
    }

    @Synchronized
    fun login(email: String, password: String, displayName: String, remember: Boolean): LocalAccount? {
        val key = normalize(email)
        val account = record(key) ?: return null
        if (!matches(account, password)) return null
        if (displayName.isNotBlank()) account.put("name", displayName.trim())

        val editor = prefs.edit().putString("account:$key", account.toString())
        if (remember) editor.putString("session_email", key)
        else editor.remove("session_email")
        return if (editor.commit()) account.toAccount(key) else null
    }

    @Synchronized
    fun changePassword(email: String, current: String, replacement: String): LocalAccount? {
        val key = normalize(email)
        val account = record(key) ?: return null
        if (!matches(account, current)) return null
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        account.put("salt", encode(salt))
            .put("verifier", encode(derive(replacement, salt)))
            .put("password_changed", System.currentTimeMillis())
        val saved = prefs.edit().putString("account:$key", account.toString()).commit()
        return if (saved) account.toAccount(key) else null
    }

    @Synchronized
    fun logout(): Boolean = prefs.edit().remove("session_email").commit()

    private fun JSONObject.toAccount(email: String) =
        LocalAccount(getString("name"), email, optLong("password_changed"))

    private fun matches(account: JSONObject, password: String): Boolean {
        val salt = Base64.decode(account.getString("salt"), Base64.NO_WRAP)
        val expected = Base64.decode(account.getString("verifier"), Base64.NO_WRAP)
        return MessageDigest.isEqual(expected, derive(password, salt))
    }

    private fun encode(bytes: ByteArray) = Base64.encodeToString(bytes, Base64.NO_WRAP)

    private fun derive(password: String, salt: ByteArray): ByteArray {
        val passwordChars = password.toCharArray()
        val spec = PBEKeySpec(passwordChars, salt, 600_000, 256)
        passwordChars.fill('\u0000')
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
