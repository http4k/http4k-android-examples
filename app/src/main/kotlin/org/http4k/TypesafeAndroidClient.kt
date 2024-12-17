package org.http4k

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.http4k.core.Request
import org.http4k.format.Jackson.json

@JvmName("invokeJson")
inline operator fun <reified T> AndroidClient.invoke(request: Request, crossinline fn: (T) -> Unit) {
    executor.execute {
        val response = client(request)
        callbackExecutor.execute { fn(response.json()) }
    }
}

@JvmName("invokeJson")
inline operator fun <reified T> CoroutineClient.invoke(request: Request, crossinline fn: (T) -> Unit) {
    scope.launch {
        fn(withContext(IO) { client(request).json() })
    }
}