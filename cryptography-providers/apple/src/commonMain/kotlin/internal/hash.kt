/*
 * Copyright (c) 2023 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.whyoleg.cryptography.providers.apple.internal

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.digest.*
import platform.Security.*

internal fun CryptographyAlgorithmId<Digest>.rsaPssSecKeyAlgorithm(): SecKeyAlgorithm? = when (this) {
    SHA1   -> kSecKeyAlgorithmRSASignatureMessagePSSSHA1
    SHA224 -> kSecKeyAlgorithmRSASignatureMessagePSSSHA224
    SHA256 -> kSecKeyAlgorithmRSASignatureMessagePSSSHA256
    SHA384 -> kSecKeyAlgorithmRSASignatureMessagePSSSHA384
    SHA512 -> kSecKeyAlgorithmRSASignatureMessagePSSSHA512
    else   -> throw CryptographyException("Unsupported hash algorithm: $this")
}

internal fun CryptographyAlgorithmId<Digest>.rsaPkcs1SecKeyAlgorithm(): SecKeyAlgorithm? = when (this) {
    SHA1   -> kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA1
    SHA224 -> kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA224
    SHA256 -> kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA256
    SHA384 -> kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA384
    SHA512 -> kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA512
    else   -> throw CryptographyException("Unsupported hash algorithm: $this")
}

internal fun CryptographyAlgorithmId<Digest>.rsaOaepSecKeyAlgorithm(): SecKeyAlgorithm? = when (this) {
    SHA1   -> kSecKeyAlgorithmRSAEncryptionOAEPSHA1
    SHA224 -> kSecKeyAlgorithmRSAEncryptionOAEPSHA224
    SHA256 -> kSecKeyAlgorithmRSAEncryptionOAEPSHA256
    SHA384 -> kSecKeyAlgorithmRSAEncryptionOAEPSHA384
    SHA512 -> kSecKeyAlgorithmRSAEncryptionOAEPSHA512
    else   -> throw CryptographyException("Unsupported hash algorithm: $this")
}
