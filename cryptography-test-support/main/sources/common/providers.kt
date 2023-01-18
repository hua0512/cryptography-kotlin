package dev.whyoleg.cryptography.test.support

import dev.whyoleg.cryptography.provider.*

expect val availableProviders: List<CryptographyProvider>

val CryptographyProvider.isWebCrypto: Boolean
    get() = name == "WebCrypto"

val CryptographyProvider.isJdk: Boolean
    get() = name == "JDK"

val CryptographyProvider.isApple: Boolean
    get() = name == "Apple"

val CryptographyProvider.supportsJwk: Boolean
    get() = isWebCrypto