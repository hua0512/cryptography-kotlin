package dev.whyoleg.cryptography.algorithms.aes

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.cipher.*
import dev.whyoleg.cryptography.key.*

private fun tests(engine: CryptographyEngine) {

    engine.get(AES.GCM).syncKeyGenerator {
        size = SymmetricKeySize.B256
    }.generateKey().syncCipher {
        tagSize = 128.bits
    }.encrypt("Hello, World!".encodeToByteArray())

    val gcm = engine.get(AES.GCM)

    val generator = gcm.syncKeyGenerator {
        size = SymmetricKeySize.B256
    }

    val key = generator.generateKey()

//    val exporter = key.syncKeyExporter {
//
//    }
//
//    exporter.exportKey(format.PEM, output)
//    exporter.exportKey(format.DER, output)

    val cipher = key.syncCipher {
        tagSize = 128.bits
    }

    cipher.encrypt("Hello, World!".encodeToByteArray())
}

public object AES {
    public abstract class CBC : KeyGeneratorProvider<CBC.Key, SymmetricKeyParameters> {
        public companion object : CryptographyAlgorithm<CBC>

        final override val defaultKeyGeneratorParameters: SymmetricKeyParameters get() = SymmetricKeyParameters.Default

        public abstract class Key : CipherProvider<CipherParameters> {
            final override val defaultCipherParameters: CipherParameters get() = CipherParameters.Default
        }

        public class CipherParameters(
            public val padding: Boolean = true,
        ) : CopyableCryptographyParameters<CipherParameters, CipherParameters.Builder>() {
            override fun builder(): Builder = Builder(padding)
            override fun build(builder: Builder): CipherParameters = CipherParameters(builder.padding)

            public class Builder internal constructor(
                public var padding: Boolean,
            )

            public companion object {
                public val Default: CipherParameters = CipherParameters()
            }
        }
    }

    public abstract class GCM : KeyGeneratorProvider<GCM.Key, SymmetricKeyParameters> {
        public companion object : CryptographyAlgorithm<GCM>

        public class Box(
            public val nonce: Buffer,
            public val ciphertext: Buffer,
            public val tag: Buffer,
        )

        final override val defaultKeyGeneratorParameters: SymmetricKeyParameters get() = SymmetricKeyParameters(SymmetricKeySize.B256)

//
//        //sync and async
//        public fun importKey(format: SymmetricKeyFormat, data: Buffer): Key = TODO()
//
//        //sync and async
//        public fun generateKey(size: SymmetricKeySize): Key = TODO()

        public class CipherParameters(
            public val tagSize: BinarySize = 128.bits,
        ) : CopyableCryptographyParameters<CipherParameters, CipherParameters.Builder>() {
            override fun builder(): Builder = Builder(tagSize)
            override fun build(builder: Builder): CipherParameters = CipherParameters(builder.tagSize)

            public class Builder internal constructor(
                public var tagSize: BinarySize,
            )

            public companion object {
                public val Default: CipherParameters = CipherParameters()
            }
        }


        //boxed
        //boxed async
        //encryp/decrypt function
        public abstract class Key : CipherProvider<CipherParameters> {
            final override val defaultCipherParameters: CipherParameters get() = CipherParameters.Default


            //expoort sync and async
//            public fun export(format: SymmetricKeyFormat): Buffer
//            public fun export(format: SymmetricKeyFormat, output: Buffer): Buffer
        }
        //create from key?
    }
}