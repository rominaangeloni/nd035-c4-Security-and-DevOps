package load

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

fun pkceVerifier(): String =
    ByteArray(32)
        .also { SecureRandom().nextBytes(it) }
        .base64()

fun pkceCodeChallenge(verifier: String): String =
    verifier
        .sha256()
        .base64()

private fun ByteArray.base64() = Base64.getUrlEncoder().withoutPadding().encodeToString(this)

private fun String.sha256() = toByteArray()
    .let {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(it, 0, it.size)
        messageDigest.digest()
    }
