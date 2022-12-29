package dev.whyoleg.cryptography.provider

//TODO
public interface BlockingAdaptor {
    public fun <T> execute(block: suspend () -> T): T
}

public interface SuspendAdaptor {
    public suspend fun <T> execute(block: () -> T): T
}