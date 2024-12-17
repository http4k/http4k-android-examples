package org.http4k

import org.http4k.core.Request
import org.http4k.format.Jackson.json

@JvmName("invokeJson")
inline operator fun <reified T> AndroidClient.invoke(request: Request, crossinline fn: (T) -> Unit) {
    executor.execute {
        val response = client(request)
        callbackExecutor.execute { fn(response.json()) }
    }
}