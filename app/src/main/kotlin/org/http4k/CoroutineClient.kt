package org.http4k

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.http4k.client.AsyncHttpHandler
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response

class CoroutineClient(
    val client: HttpHandler,
    val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = IO
) : AsyncHttpHandler, HttpHandler {

    override fun invoke(request: Request, fn: (Response) -> Unit) {
        scope.launch {
            fn(withContext(dispatcher) { client(request) })
        }
    }

    override fun invoke(request: Request): Response =
        runBlocking {
            withContext(dispatcher) { client(request) }
        }
}