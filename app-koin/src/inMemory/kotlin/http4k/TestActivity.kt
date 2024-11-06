package org.http4k

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import theme.Http4kandroidexamplesTheme
import java.util.UUID

class TestActivity : ComponentActivity() {
    private val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@TestActivity)
            modules(testModule)
        }

        enableEdgeToEdge()
        setContent {
            Http4kandroidexamplesTheme {
                MainView(viewModel = viewModel)
            }
        }
    }
}

val fakeServer: HttpHandler = { _: Request -> Response(OK).body("{\"uuid\":\"${UUID.randomUUID()} (test value)\"}") }

val testModule = module {
    single { fakeServer } withOptions { bind<HttpHandler>() }
    factory { MainViewModel(get()) }
}