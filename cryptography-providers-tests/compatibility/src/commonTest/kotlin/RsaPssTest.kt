/*
 * Copyright (c) 2023 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.whyoleg.cryptography.providers.tests.compatibility

import dev.whyoleg.cryptography.BinarySize.Companion.bytes
import dev.whyoleg.cryptography.algorithms.asymmetric.*
import dev.whyoleg.cryptography.providers.tests.compatibility.api.*
import dev.whyoleg.cryptography.providers.tests.support.*
import dev.whyoleg.cryptography.random.*
import kotlinx.serialization.*
import kotlin.math.*
import kotlin.test.*

private const val saltIterations = 3
private const val signatureIterations = 3
private const val maxDataSize = 10000

class RsaPssTest : RsaBasedTest<RSA.PSS.PublicKey, RSA.PSS.PrivateKey, RSA.PSS.KeyPair, RSA.PSS>(RSA.PSS) {

    @Serializable
    private data class SignatureParameters(val saltSizeBytes: Int?) : TestParameters

    override suspend fun CompatibilityTestScope<RSA.PSS>.generate() {
        generateKeys { keyPair, keyReference, (keySizeBites, _, digestSizeBytes) ->
            val maxSaltSize = (ceil((keySizeBites - 1) / 8.0) - digestSizeBytes - 2).toInt()
            (List(saltIterations) { CryptographyRandom.nextInt(maxSaltSize) } + null).forEach { saltSizeBytes ->
                if (!supportsSaltSize(saltSizeBytes)) return@forEach

                val signatureParametersId = api.signatures.saveParameters(SignatureParameters(saltSizeBytes))

                logger.log { "salt.size      = $saltSizeBytes" }

                val (signatureGenerator, signatureVerifier) = when (saltSizeBytes) {
                    null -> keyPair.privateKey.signatureGenerator() to keyPair.publicKey.signatureVerifier()
                    else -> keyPair.privateKey.signatureGenerator(saltSizeBytes.bytes) to keyPair.publicKey.signatureVerifier(saltSizeBytes.bytes)
                }

                repeat(signatureIterations) {
                    val dataSize = CryptographyRandom.nextInt(maxDataSize)
                    logger.log { "data.size      = $dataSize" }
                    val data = CryptographyRandom.nextBytes(dataSize)
                    val signature = signatureGenerator.generateSignature(data)
                    logger.log { "signature.size = ${signature.size}" }

                    assertTrue(signatureVerifier.verifySignature(data, signature), "Initial Verify")

                    api.signatures.saveData(signatureParametersId, SignatureData(keyReference, data, signature))
                }
            }
        }
    }

    override suspend fun CompatibilityTestScope<RSA.PSS>.validate() {
        val keyPairs = validateKeys()

        api.signatures.getParameters<SignatureParameters> { (saltSizeBytes), parametersId, _ ->
            if (!supportsSaltSize(saltSizeBytes)) return@getParameters

            api.signatures.getData<SignatureData>(parametersId) { (keyReference, data, signature), _, _ ->
                val (publicKeys, privateKeys) = keyPairs[keyReference] ?: return@getData
                val verifiers = publicKeys.map {
                    when (saltSizeBytes) {
                        null -> it.signatureVerifier()
                        else -> it.signatureVerifier(saltSizeBytes.bytes)
                    }
                }
                val generators = privateKeys.map {
                    when (saltSizeBytes) {
                        null -> it.signatureGenerator()
                        else -> it.signatureGenerator(saltSizeBytes.bytes)
                    }
                }

                verifiers.forEach { verifier ->
                    assertTrue(verifier.verifySignature(data, signature), "Verify")

                    generators.forEach { generator ->
                        assertTrue(verifier.verifySignature(data, generator.generateSignature(data)), "Sign-Verify")
                    }
                }
            }
        }
    }
}
