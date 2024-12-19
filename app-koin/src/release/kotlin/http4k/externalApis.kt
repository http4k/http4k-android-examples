package http4k

import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val externalApis = module {
    single(qualifier("httpbin")) { OkHttp() } withOptions { bind<HttpHandler>() }
}