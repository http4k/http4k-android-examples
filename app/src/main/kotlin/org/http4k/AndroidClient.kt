package org.http4k

import android.os.Handler
import android.os.Looper
import org.http4k.client.AsyncHttpHandler
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Jackson.json
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

class AndroidClient(val client: HttpHandler) : AsyncHttpHandler, HttpHandler {
    val executor = Executors.newCachedThreadPool { command ->
        Thread(command).also { thread ->
            thread.priority = Thread.NORM_PRIORITY
            thread.isDaemon = true
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    var callbackExecutor: Executor = Executor { command -> handler.post(command) }


    override fun invoke(request: Request, fn: (Response) -> Unit) {
        executor.execute {
            val result: Response = client(request)
            callbackExecutor.execute { fn(result) }
        }
    }

    @JvmName("invokeJson")
    inline operator fun <reified T> invoke(request: Request, crossinline fn: (T) -> Unit) {
        executor.execute {
            val response = client(request)
            callbackExecutor.execute { fn(response.json()) }
        }
    }

    override fun invoke(request: Request): Response {
        val responsePromise = FutureTask { client(request) }
        executor.execute(responsePromise)
        return responsePromise.get()
    }
}