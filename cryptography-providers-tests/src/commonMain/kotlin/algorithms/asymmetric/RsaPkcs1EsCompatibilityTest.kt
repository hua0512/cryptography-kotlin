/*
 * Copyright (c) 2024 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.whyoleg.cryptography.providers.tests.algorithms.asymmetric

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.algorithms.asymmetric.*
import dev.whyoleg.cryptography.providers.tests.api.*
import dev.whyoleg.cryptography.providers.tests.api.compatibility.*
import dev.whyoleg.cryptography.random.*
import kotlin.test.*

abstract class RsaPkcs1EsCompatibilityTest(provider: CryptographyProvider) :
    RsaBasedCompatibilityTest<RSA.PKCS1.PublicKey, RSA.PKCS1.PrivateKey, RSA.PKCS1.KeyPair, RSA.PKCS1>(RSA.PKCS1, provider) {

    override suspend fun CompatibilityTestScope<RSA.PKCS1>.generate(isStressTest: Boolean) {
        if (!supportsEncryption()) return

        val cipherIterations = when {
            isStressTest -> 5
            else         -> 2
        }

        val cipherParametersId = api.ciphers.saveParameters(TestParameters.Empty)
        generateKeys(isStressTest) { keyPair, keyReference, keyParameters ->
            val maxPlaintextSize = keyParameters.keySizeBits.bits.inBytes - 2 - 2 * keyParameters.digestSizeBytes
            logger.log { "maxPlaintextSize.size = $maxPlaintextSize" }
            val encryptor = keyPair.publicKey.encryptor()
            val decryptor = keyPair.privateKey.decryptor()

            repeat(cipherIterations) {
                val plaintextSize = CryptographyRandom.nextInt(maxPlaintextSize)
                logger.log { "plaintext.size        = $plaintextSize" }
                val plaintext = CryptographyRandom.nextBytes(plaintextSize)
                val ciphertext = encryptor.encrypt(plaintext)
                logger.log { "ciphertext.size       = ${ciphertext.size}" }

                assertContentEquals(plaintext, decryptor.decrypt(ciphertext), "Initial Decrypt")

                api.ciphers.saveData(cipherParametersId, CipherData(keyReference, plaintext, ciphertext))
            }
        }
    }

    override suspend fun CompatibilityTestScope<RSA.PKCS1>.validate() {
        if (!supportsEncryption()) return

        val keyPairs = validateKeys()

        api.ciphers.getParameters<TestParameters.Empty> { _, parametersId, _ ->
            api.ciphers.getData<CipherData>(parametersId) { (keyReference, plaintext, ciphertext), _, _ ->
                val (publicKeys, privateKeys) = keyPairs[keyReference] ?: return@getData
                val encryptors = publicKeys.map { it.encryptor() }
                val decryptors = privateKeys.map { it.decryptor() }

                decryptors.forEach { decryptor ->
                    assertContentEquals(plaintext, decryptor.decrypt(ciphertext), "Decrypt")

                    encryptors.forEach { encryptor ->
                        assertContentEquals(
                            plaintext,
                            decryptor.decrypt(encryptor.encrypt(plaintext)),
                            "Encrypt-Decrypt"
                        )
                    }
                }
            }
        }
    }
}
