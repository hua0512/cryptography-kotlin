package dev.whyoleg.cryptography.webcrypto.internal

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.digest.*
import dev.whyoleg.cryptography.algorithms.symmetric.mac.*
import dev.whyoleg.cryptography.io.*
import dev.whyoleg.cryptography.operations.*
import dev.whyoleg.cryptography.operations.key.*
import dev.whyoleg.cryptography.operations.signature.*
import dev.whyoleg.cryptography.webcrypto.external.*

internal object HmacKeyGeneratorProvider : KeyGeneratorProvider<HMAC.KeyGeneratorParameters, HMAC.Key>() {
    override fun provideOperation(parameters: HMAC.KeyGeneratorParameters): KeyGenerator<HMAC.Key> {
        val hashAlgorithm = when (parameters.digest) {
            SHA1   -> "SHA-1"
            SHA512 -> "SHA-512"
            else   -> throw CryptographyException("Unsupported hash algorithm: ${parameters.digest}")
        }
        return HmacKeyGenerator(hashAlgorithm)
    }
}

internal class HmacKeyGenerator(
    private val hashAlgorithm: String,
) : KeyGenerator<HMAC.Key> {

    override suspend fun generateKey(): HMAC.Key {
        val key = WebCrypto.subtle.generateKey(
            Algorithm<HmacKeyGenerationAlgorithm>(name = "HMAC") {
                this.hash = hashAlgorithm
            },
            true,
            arrayOf("sign", "verify"),
        ).await()
        return HMAC.Key(
            WebCryptoHmacSignatureProvider(key),
            NotSupportedProvider()
        )
    }

    override fun generateKeyBlocking(): HMAC.Key = nonBlocking()
}

internal class WebCryptoHmacSignatureProvider(
    private val key: CryptoKey,
) : SignatureProvider<CryptographyOperationParameters.Empty>() {
    override fun provideOperation(parameters: CryptographyOperationParameters.Empty): Signature = WebCryptoHmacSignature(key)
}


internal class WebCryptoHmacSignature(
    private val key: CryptoKey,
) : Signature {
    override val signatureSize: Int
        get() = TODO("Not yet implemented")

    override suspend fun sign(dataInput: Buffer): Buffer {
        return WebCrypto.subtle.sign(Algorithm("HMAC"), key, dataInput).await()
    }

    override suspend fun sign(dataInput: Buffer, signatureOutput: Buffer): Buffer {
        return sign(dataInput).copyInto(signatureOutput)
    }

    override suspend fun verify(dataInput: Buffer, signatureInput: Buffer): Boolean {
        return WebCrypto.subtle.verify(Algorithm("HMAC"), key, signatureInput, dataInput).await()
    }

    override fun signBlocking(dataInput: Buffer): Buffer = nonBlocking()
    override fun signBlocking(dataInput: Buffer, signatureOutput: Buffer): Buffer = nonBlocking()
    override fun verifyBlocking(dataInput: Buffer, signatureInput: Buffer): Boolean = nonBlocking()

    override fun signFunction(): SignFunction = noFunction()
    override fun verifyFunction(): VerifyFunction = noFunction()
}