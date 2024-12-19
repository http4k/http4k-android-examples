package http4k

import org.http4k.UUIDResponse
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import java.util.*

val fakeServer: HttpHandler = { _:Request -> Response(OK).json(UUIDResponse("test-${UUID.randomUUID()}")) }

val externalApis = module {
    single(qualifier("httpbin")) { fakeServer } withOptions { bind<HttpHandler>() }
}